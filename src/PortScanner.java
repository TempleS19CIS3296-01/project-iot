import java.util.LinkedList;
import java.net.SocketAddress;
import java.net.Socket;
import java.io.*;
import java.net.InetSocketAddress;

public class PortScanner{
    private int timeout;

    public PortScanner(int toVal){
        this.timeout = toVal; 
    }
    
    public LinkedList<Integer> checkOpenPorts(String IP){
        LinkedList<Integer> ports = new LinkedList<Integer>();
        for(int i = 0; i < 65535; i++){
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(IP, i), timeout);
                socket.close();
                ports.add(i);
                System.out.println("Open Port found at : " + i);
            } catch (Exception ex) {
                continue;
            }
        }
        return ports;
    }
}