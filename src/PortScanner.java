
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class PortScanner implements Runnable{
    private HashMap<String, LinkedListInt> portMap;
    private String IP;

    public PortScanner(HashMap portMap, String ip){
        this.portMap = portMap;
        IP = ip;
    }
    
    public void run(){
        for(int port = 0; port < 65535; port++){
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(IP, port), 300);
                socket.close();
            } catch (Exception ex) {
                continue;
            }
            synchronized (portMap) {
                LinkedListInt portList = portMap.get(IP);
                if (portList != null){// If we already have an element for this IP address.
                    portList.add(port);
                    portMap.put(IP, portList);
                } else {// We do not have any ports yet for this IP address.
                    portList = new LinkedListInt();
                    portList.add(port);
                    portMap.put(IP, portList);
                }
            }
            System.out.println("IP: "+ IP+ " port found at : " + port);
        }
    }
    // Accessor for ports.
    public HashMap getPorts(){
        return portMap;
    }
}