package agora;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ThriftPaxosImpl implements ThriftPaxos.Iface {
    static private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    static private  Map<String, Auction> auctions = new HashMap<>();

    static private List<Cohort> cohorts;

    static private int numCohorts;
    static private AtomicInteger seq = new AtomicInteger(0);
    static private int majorityNum;

    private final Logger logger = new Logger("./server_log.txt");

    private static class Acceptor {
        static PaxosData data = null;
    }

    public ThriftPaxosImpl(List<Cohort> cohortList) {
        cohorts = cohortList;
        numCohorts = cohortList.size();
        majorityNum = numCohorts/2 - 1;

        /* recovery code for server */
        ArrayList<ThriftPaxos.Client> clients = initClients();
        for (ThriftPaxos.Client client: clients) {
            try {
                Map<String, Auction> map = client.getAllAuctions();
                if (map.size() > auctions.size()) {
                    lock.writeLock().lock();
                    auctions = map;
                    lock.writeLock().unlock();
                    break;
                }
            } catch (TException e) {
//                e.printStackTrace();
                logger.log(e.toString());
            }
        }
    }

    private ArrayList<ThriftPaxos.Client> initClients() {
        ArrayList<ThriftPaxos.Client> arrayList = new ArrayList<>();
        for (Cohort cohort: cohorts) {
            TTransport transport = new TSocket(cohort.inetAddress.getHostName(), cohort.port);
            try {
                transport.open();
            } catch (TTransportException e) {
                logger.log("cannot connect to " + cohort.inetAddress.getHostName() + ":" + cohort.port);
                e.printStackTrace();
                continue;
            }
            arrayList.add(new ThriftPaxos.Client(new TBinaryProtocol(transport)));
        }
        return arrayList;
    }

    private boolean doPaxos(Value value) {
        System.out.println("in do paxos");

        ArrayList<ThriftPaxos.Client> clients = initClients();
        ArrayList<PaxosData> promises = new ArrayList<>();
        boolean isNack;
        int highestSeq;
        do {
            isNack = false;
            /* propose/promise phase */
            /* contact all acceptors */
            for (ThriftPaxos.Client client: clients) {
                PaxosData pd = new PaxosData();
                pd.setN(seq.get());
                pd.setV(value);
                pd.nack = false;

                try {
                    PaxosData promise = client.prepare(pd);
                    if (promise != null) {
                        promises.add(promise);
                        /* Lamport's performance optimization to abandon and retry */
                        if (promise.nack) {
                            isNack = true;
                            break;
                        }
                    }
                } catch (TException e) {
                    logger.log(e.toString());
                    e.printStackTrace();
                }
            }

            highestSeq = seq.getAndIncrement();

            if (promises.size() < majorityNum) {
                return false;
            }
        } while (isNack);

        /* in accept/accepted/announce phase */
        PaxosData pd = new PaxosData();
        pd.setN(highestSeq-1);
        pd.setV(value);
        pd.nack = false;

        /* do paxos accept on all acceptors */
        for (ThriftPaxos.Client client: clients) {
            try {
                /* accept returns accepted and triggers announce*/
                client.accept(pd);
            } catch (TException e) {
                /* simulated failure of acceptor gets caught here */
                logger.log(e.toString());
                e.printStackTrace();
            }
        }

        return true;
    }

    private void create(Item item) {
        Auction auction = new Auction();
        auction.item = item;
        auction.open = true;

        lock.writeLock().lock();
        auctions.put(item.itemName, auction);
        lock.writeLock().unlock();
    }

    private boolean delete(String itemName, User user) {
        lock.writeLock().lock();
        auctions.remove(itemName);
        lock.writeLock().unlock();

        return true;
    }

    private boolean close(String itemName, User user) {
        lock.writeLock().lock();
        if (auctions.containsKey(itemName)) {
            auctions.get(itemName).open = false;
        }
        lock.writeLock().unlock();

        return true;
    }

    private boolean bid(Bid bid) {
        boolean succ = false;
        lock.writeLock().lock();
        if (auctions.containsKey(bid.itemName)) {
            Auction auction = auctions.get(bid.itemName);
            if (bid.price > auction.item.price) {
                auction.item.price = bid.price;
                auction.highestBidder = bid.bidder;
                succ = true;
            }
        }
        lock.writeLock().unlock();
        return succ;
    }

    /**
     * Paxos Announce to learners
     * @param pd
     */
    private void announce(PaxosData pd) {
        System.out.println("in announce");
        switch (pd.v.action) {
            case "CREATE":
                create(pd.v.item);
                break;
            case "DELETE":
                delete(pd.v.itemName, pd.v.user);
                break;
            case "CLOSE":
                close(pd.v.itemName, pd.v.user);
                break;
            case "BID":
                bid(pd.v.bid);
                break;
            default:
                assert false;
                break;
        }
    }

    @Override
    public PaxosData prepare(PaxosData pd) throws org.apache.thrift.TException {
        if (Acceptor.data == null) {
            Acceptor.data = pd;
            return pd;
        } else if (pd.n > Acceptor.data.n) {
            Acceptor.data = pd;
            return pd;
        } else {
            PaxosData nack = Acceptor.data.deepCopy();
            nack.setNack(true);
            return nack;
        }
    }

    @Override
    public PaxosData accept(PaxosData pd) throws org.apache.thrift.TException {
        Acceptor.data = pd;
        announce(pd);
        return pd;
    }

    @Override
    public Map<String, Auction> getAllAuctions() throws org.apache.thrift.TException {
        return auctions;
    }

    @Override
    public boolean bid(String itemName, User user, int price) throws org.apache.thrift.TException {
        System.out.println("bid");

        int currPrice = -1;
        boolean open = false;
        lock.readLock().lock();
        if (auctions.containsKey(itemName)) {
            currPrice = auctions.get(itemName).item.price;
            open = auctions.get(itemName).open;
        }
        lock.readLock().unlock();

        if (currPrice == -1 || currPrice >= price || !open) {
            return false;
        }

        Bid bid = new Bid();
        bid.itemName = itemName;
        bid.price = price;
        bid.bidder = user;

        Value val = new Value();
        val.action = "BID";
        val.bid = bid;

        return doPaxos(val);
    }

    @Override
    public boolean createAuction(Item item, User user) throws org.apache.thrift.TException {
        System.out.println("createAuction");
        Value val = new Value();
        val.action = "CREATE";
        val.item = item;

        return doPaxos(val);
    }

    @Override
    public boolean delAuction(String itemName, User user) throws org.apache.thrift.TException {
        System.out.println("delAuction");
        String creatorUUID = "";
        lock.readLock().lock();
        if (auctions.containsKey(itemName)) {
            creatorUUID = auctions.get(itemName).item.creator.uuid;
        }
        lock.readLock().unlock();

        if (!creatorUUID.equals(user.uuid)) {
            return false;
        }

        Value val = new Value();
        val.action = "DELETE";
        val.itemName = itemName;
        val.user = user;

        return doPaxos(val);
    }

    @Override
    public boolean closeAuction(String itemName, User user) throws org.apache.thrift.TException {
        System.out.println("closeAuction");

        String creatorUUID = "";
        lock.readLock().lock();
        if (auctions.containsKey(itemName)) {
            creatorUUID = auctions.get(itemName).item.creator.uuid;
        }
        lock.readLock().unlock();

        if (!creatorUUID.equals(user.uuid)) {
            return false;
        }

        Value val = new Value();
        val.action = "CLOSE";
        val.itemName = itemName;
        val.user = user;

        return doPaxos(val);
    }
}
