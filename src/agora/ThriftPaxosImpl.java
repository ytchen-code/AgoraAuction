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
    static int port;
    static private AtomicInteger seq = new AtomicInteger(0);
    static private int majorityNum;

    private final Logger logger = new Logger("./server_log.txt");

    private static class Acceptor {
        static PaxosData data = null;
    }

    /**
     * Initialize the thrift server with a list of peer servers
     * @param cohortList peer servers
     */
    public ThriftPaxosImpl(List<Cohort> cohortList, int port) {
        cohorts = cohortList;
        numCohorts = cohortList.size();
        majorityNum = numCohorts/2 - 1;
        ThriftPaxosImpl.port = port;

        /* recovery code for server */
        ArrayList<ThriftPaxos.Client> clients = initClients(true);
        for (ThriftPaxos.Client client: clients) {
            try {
                Map<String, Auction> map = client.getAllAuctions();
                if (map.size() > auctions.size()) {
                    lock.writeLock().lock();
                    auctions = map;
                    lock.writeLock().unlock();
                    logger.log("recovered all data by sync'ing to the cluster");
                    break;
                }
            } catch (TException e) {
//                e.printStackTrace();
                logger.silentLog(e.getMessage());
            }
        }
    }

    /**
     * initialize the thrift object representing other servers in the cluster
     * @return list of thrift object clients
     */
    private ArrayList<ThriftPaxos.Client> initClients(boolean skipMyself) {
        ArrayList<ThriftPaxos.Client> arrayList = new ArrayList<>();
        for (Cohort cohort: cohorts) {
            if (skipMyself && cohort.port == ThriftPaxosImpl.port) { continue; }
            TTransport transport = new TSocket(cohort.inetAddress.getHostName(), cohort.port);
            try {
                transport.open();
            } catch (TTransportException e) {
                logger.silentLog("cannot connect to " + cohort.inetAddress.getHostName() + ":" + cohort.port);
//                e.printStackTrace();
                continue;
            }
            arrayList.add(new ThriftPaxos.Client(new TBinaryProtocol(transport)));
        }
        return arrayList;
    }

    /**
     * the paxos logic is here
     * @param value the action that needs to be performed via paxos
     * @return true if sucessful. false otherwise
     */
    private boolean doPaxos(Value value) {
        logger.log("starting paxos");

        ArrayList<ThriftPaxos.Client> clients = initClients(false);
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
                    logger.log(e.getMessage());
//                    e.printStackTrace();
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
                logger.log(e.getMessage());
//                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * helper for announce() method
     * create auction item
     * @param item item to be sold
     */
    private void create(Item item) {
        logger.log("create " + item.itemName);
        Auction auction = new Auction();
        auction.item = item;
        auction.open = true;

        lock.writeLock().lock();
        auctions.put(item.itemName, auction);
        lock.writeLock().unlock();
    }

    /**
     * helper for announce() method
     * delete auction
     * @param itemName item to delete from auction
     * @param user credentials of the user performing this action
     * @return not relevant
     */
    private boolean delete(String itemName, User user) {
        logger.log("delete " + itemName);
        lock.writeLock().lock();
        auctions.remove(itemName);
        lock.writeLock().unlock();

        return true;
    }

    /**
     * helper for announce() method
     * close auction
     * @param itemName item to close the auction for
     * @param user credentials of the user performing this action
     * @return not relevant
     */
    private boolean close(String itemName, User user) {
        logger.log("close " + itemName);
        lock.writeLock().lock();
        if (auctions.containsKey(itemName)) {
            auctions.get(itemName).open = false;
        }
        lock.writeLock().unlock();

        return true;
    }

    /**
     * bid for an item
     * @param bid
     * @return
     */
    private boolean bid(Bid bid) {
        boolean succ = false;
        logger.log("attempting bid " + bid.itemName + " " + bid.price);
        lock.writeLock().lock();
        if (auctions.containsKey(bid.itemName)) {
            Auction auction = auctions.get(bid.itemName);
            if (bid.price > auction.item.price) {
                auction.item.price = bid.price;
                auction.highestBidder = bid.bidder;
                succ = true;
                logger.log("bid successful");
            } else {
                logger.log("bid unsuccessful");
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
        logger.log("in announce");
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

    /**
     * Paxos prepare
     * Returns paxos promise from acceptors
     * Acceptors returns a nack if not a valid seq number (as per Lamport's optimization suggestion)
     * @param pd data containing sequence number and transaction details
     * @return Paxos Promise
     */
    @Override
    public PaxosData prepare(PaxosData pd) throws org.apache.thrift.TException {
        logger.log("in prepare");
        if (Acceptor.data == null) {
            Acceptor.data = pd;
            logger.log("in promise");
            return pd;
        } else if (pd.n > Acceptor.data.n) {
            Acceptor.data = pd;
            logger.log("in promise");
            return pd;
        } else {
            logger.log("in promise - nack");
            PaxosData nack = Acceptor.data.deepCopy();
            nack.setNack(true);
            return nack;
        }
    }

    /**
     * Paxos Accept
     * Returns paxos Accepted and triggers paxos Anounce
     * @param pd data containing sequence number and transaction details
     * @return transaction that was accepted
     */
    @Override
    public PaxosData accept(PaxosData pd) throws org.apache.thrift.TException {
        logger.log("in accept");
        Acceptor.data = pd;
        announce(pd);
        return pd;
    }

    /**
     * Method for client to get all information about auctions
     * @return
     * @throws org.apache.thrift.TException
     */
    @Override
    public Map<String, Auction> getAllAuctions() throws org.apache.thrift.TException {
        logger.log("in getAllAuctions");
        return auctions;
    }

    /**
     * Method for client to bid on an auction item
     * @param itemName item to bid on
     * @param user user credentials of the bidder
     * @param price bidding price
     * @return indicate whether bid was successful
     * @throws org.apache.thrift.TException
     */
    @Override
    public boolean bid(String itemName, User user, int price) throws org.apache.thrift.TException {
        logger.log("received req for bid " + itemName);

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

    /**
     * Method for client to create auctions
     * @param item item to create the auction for
     * @param user credential of user wanting to create this auction
     * @return boolean indicating whether the creation was successful
     * @throws org.apache.thrift.TException
     */
    @Override
    public boolean createAuction(Item item, User user) throws org.apache.thrift.TException {
        logger.log("received req for createAuction " + item.itemName);

        boolean alreadyExist = false;

        lock.readLock().lock();
        if (auctions.containsKey(item.itemName)) {
            alreadyExist = true;
        }
        lock.readLock().unlock();

        if (alreadyExist) {
            return false;
        }

        Value val = new Value();
        val.action = "CREATE";
        val.item = item;

        return doPaxos(val);
    }

    /**
     * Method for client to delete an auction
     * Only the original creator of the auction can delete the auction
     * @param itemName item to delete the auction for
     * @param user credential of the user wanting to delete
     * @return  boolean indicating whether the deletion was successful
     * @throws org.apache.thrift.TException
     */
    @Override
    public boolean delAuction(String itemName, User user) throws org.apache.thrift.TException {
        logger.log("received req for delAuction " + itemName);
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

    /**
     * Method for client to close an auction
     * Only the original creator of the auction can close the auction
     * @param itemName item to close the auction for
     * @param user credential of the user wanting to close the auction
     * @return boolean indicating whether the close was successful
     * @throws org.apache.thrift.TException
     */
    @Override
    public boolean closeAuction(String itemName, User user) throws org.apache.thrift.TException {
        logger.log("received req for closeAuction " + itemName);

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
