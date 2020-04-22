package agora;

import javax.naming.InitialContext;
import java.net.InetAddress;

class Cohort {
    InetAddress inetAddress;
    int port;
    public Cohort(int port, InetAddress inetAddress) {
        this.port = port;
        this.inetAddress = inetAddress;
    }

    public String toString() {
        return inetAddress + " " + port + " ";
    }
}