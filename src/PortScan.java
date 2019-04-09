import java.net.InetSocketAddress;
import java.net.Socket;

public class PortScan implements Runnable{
    private String ip;

    public PortScan(String ip){
        this.ip = ip;
    }

    @Override
    public void run() {
        System.out.println("Starting port sweep of IP: " + ip);
        for (int port = 1; port <= 65535; port++) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), 1000);
                socket.close();
                System.out.println("IP is " + ip + " and Port " + port + " is open");
            } catch (Exception expected) {
            }
        }
    }
}
