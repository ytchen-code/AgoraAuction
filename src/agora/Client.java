package agora;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Client {
    /* PAXOS related */
    private ThriftPaxos.Client auctionServer;
    private InetAddress inetAddress;
    private int port;

    private Logger logger;

    private User user = new User();

    private void connectServer() {
        TTransport transport = new TSocket(this.inetAddress.getHostName(), this.port);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        auctionServer = new ThriftPaxos.Client(new TBinaryProtocol(transport));
    }

    public Client(String host, int port) {
        try {
            this.inetAddress = InetAddress.getByName(host);
            this.port = port;
            this.logger = new Logger("./client_log.txt");
            this.logger.log("Initialized a AgoraLiveAuction Client");
//            TTransport transport = new TSocket(this.inetAddress.getHostName(), this.port);
//            transport.open();
//            auctionServer = new ThriftPaxos.Client(new TBinaryProtocol(transport));
            connectServer();

            this.user.setUuid(UUID.randomUUID().toString());

        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void askClientName() {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.printf("Please enter your name: ");
            this.user.setName(reader.readLine());
            System.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static private void printAuctions(Map<String, Auction> auctions) {
        if (auctions.size() == 0) {
            System.out.println("There are no auctions right now\n");
            return;
        }
        TreeMap<String, Auction> sorted = new TreeMap<>(auctions);
        System.out.printf("%-15s%-15s%-15s%-15s\n","itemName",
                "price", "highestBidder", "status");
        for (Map.Entry<String, Auction> entry : sorted.entrySet()) {
            Auction auction = entry.getValue();
//            assert auction.item != null;
//            System.out.println(auction.item.price);
            System.out.printf("%-15s%-15d%-15s%-15s\n",
                    auction.item.itemName,
                    auction.item.price,
                    auction.highestBidder!=null?auction.highestBidder.name:"no bidder",
                    auction.open?"open":"closed");
        }
    }

    private void prompt() {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("***** Now reading from STDIN *****");
            // Reading data using readLine
            try {

                System.out.println("*\tEnter command in format:");
                System.out.println("*\t\tLIST");
                System.out.println("*\t\tBID itemName price");
                System.out.println("*\t\tCREATE itemName startPrice");
                System.out.println("*\t\tDELETE itemName");
                System.out.println("*\t\tCLOSE itemName");
                System.out.flush();

                System.out.print("* Enter command: ");
                System.out.flush();
                String line = reader.readLine();
                String[] words = line.split(" ");

                try {
                    if (words[0].equals("LIST")) {
                        connectServer();
                        Map<String, Auction> auctions = this.auctionServer.getAllAuctions();
                        printAuctions(auctions);
                    } else if (words[0].equals("BID")) {
                        connectServer();
                        String itemName = words[1];
                        int bidPrice = Integer.parseInt(words[2]);

                        if (bidPrice < 0) {
                            this.logger.log("cannot bid with less than $0");
                            continue;
                        }

                        boolean res = this.auctionServer.bid(itemName, this.user, bidPrice);
                        if (res) {
                            this.logger.log("bidding for " + itemName + " was successful");
                        } else {
                            this.logger.log("bidding for " + itemName + " was unsuccessful");
                        }
                    } else if (words[0].equals("CREATE")) {
                        connectServer();
                        Item item = new Item();
                        item.itemName = words[1];
                        item.price = Integer.parseInt(words[2]);;
                        item.creator = this.user;

                        if (item.price < 0) {
                            this.logger.log("cannot create item with price less than $0");
                            continue;
                        }

                        boolean res = this.auctionServer.createAuction(item, this.user);

                        if (res) {
                            this.logger.log("creating auction " + item.itemName + " was successful");
                        } else {
                            this.logger.log("creating auction " + item.itemName + " was unsuccessful");
                        }
                    } else if (words[0].equals("DELETE")) {
                        connectServer();
                        String itemName = words[1];
                        boolean res = this.auctionServer.delAuction(itemName, this.user);

                        if (res) {
                            this.logger.log("deleting auction " + itemName + " was successful");
                        } else {
                            this.logger.log("deleting auction " + itemName + " was unsuccessful");
                            this.logger.log("make sure that you are the original creator of the auction");
                        }
                        // TODO indicate whether del was successful
                    } else if (words[0].equals("CLOSE")) {
                        connectServer();
                        String itemName = words[1];
                        boolean res = this.auctionServer.closeAuction(itemName, this.user);
                        if (res) {
                            this.logger.log("closing auction " + itemName + " was successful");
                        } else {
                            this.logger.log("closing auction " + itemName + " was unsuccessful");
                            this.logger.log("make sure that you are the original creator of the auction");
                        }
                        // TODO indicate whether close was successful
                    } else {
                        System.out.println("malformed input... please try again");
                        continue;
                    }
                } catch (TException e) {
                    e.printStackTrace();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("malformed input... please try again");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        askClientName();
        prompt();
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            String[] sp = args[0].split(":");
            int port =  Integer.parseInt(sp[1]);
            String host = sp[0];
            Client client = new Client(host, port);
            client.run();

        } else {
            System.out.println("e.g. java -jar -ea ./jars/client/Client.jar localhost:30000");
            throw new RuntimeException("Incorrect argument");
        }
    }

}