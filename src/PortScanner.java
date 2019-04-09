import java.util.LinkedList;
import java.net.SocketAddress;
import java.net.Socket;
import java.io.*;
import java.net.InetSocketAddress;

public class PortScanner implements Runnable{
    private LinkedList<Integer> ports;
    private String IP;

    public PortScanner(String ip){
        IP = ip;
    }
    
    public void run(){
        for(int i = 0; i < 65535; i++){
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(IP, i), 300);
                socket.close();
            } catch (Exception ex) {
                continue;
            }
            ports.add(i);
            System.out.println("Open Port found at : " + i);
        }
    }
}