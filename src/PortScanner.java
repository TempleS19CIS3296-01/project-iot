import java.util.LinkedList;
import java.net.SocketAddress;
import java.net.Socket;
import java.io.*;
import java.net.InetSocketAddress;

public class PortScanner implements Runnable{
    private int timeout;
    private LinkedList<Integer> ports;

    public PortScanner(int toVal){
        this.timeout = toVal; 
    }
    
    public void run(){

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
    }
}