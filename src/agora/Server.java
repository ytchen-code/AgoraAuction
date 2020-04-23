package agora;

import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final Logger logger = new Logger("./server_log.txt");

    int port;
    private List<Cohort> cohorts;

    /**
     * Initialize the Server with given peers
     * @param port port that this server listens to
     * @param peers address of peer servers in the cluster
     */
    public Server(int port, String peers) {
        this.logger.log("Initializing server");
        this.port = port;
        this.cohorts = new ArrayList<>();
        /* add remote nodes */
        for (String addr : peers.split(",")) {
            String[] sp = addr.split(":");
            try {
                Cohort cohort = new Cohort(Integer.parseInt(sp[1]), InetAddress.getByName(sp[0]));
                this.cohorts.add(cohort);
            } catch (UnknownHostException e) {
                this.logger.log(e.toString());
                e.printStackTrace();
            }
        }
        /* add local node */
        Cohort cohort = null;
        try {
            cohort = new Cohort(this.port, InetAddress.getByName("localhost"));
            this.cohorts.add(cohort);
        } catch (UnknownHostException e) {
            this.logger.log(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Start the server
     */
    public void start() {
        this.logger.log("Starting server");
        try {
            TServerTransport serverTransport = new TServerSocket(this.port);
            /* start a threadpool of Paxos workers */
            TThreadPoolServer.Args args = new TThreadPoolServer
                    .Args(serverTransport)
                    .minWorkerThreads(10)
                    .processor(new ThriftPaxos.Processor<>(new ThriftPaxosImpl(this.cohorts, this.port)));
            TThreadPoolServer server = new TThreadPoolServer(args);
            server.serve();
        } catch (TTransportException e) {
            this.logger.log(e.getMessage());
        }
    }



    public static void main(String[] args) {
        // write your code here
        if (args.length == 2) {
            int port;
            port = Integer.parseInt(args[0]);
            new Server(port, args[1]).start();
        } else {
            System.out.println("Invalid argument");
            System.out.println("e.g. java -jar ./jars/server/Server.jar 30000 localhost:30001,localhost:30002,localhost:30003,localhost:30004");
        }
    }

}
