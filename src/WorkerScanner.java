
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class WorkerScanner implements Runnable{
    private HashMap<String, LinkedListInt> portMap;
    private String IP;
    private HashMap namedPorts = new PortNameMap().getMap();

    public WorkerScanner(HashMap portMap, String ip){
        this.portMap = portMap;
        IP = ip;
    }

    public void run(){
        // Iterate over all ports.
        for(int port = 0; port < 65535; port++){
            try {
                // Try to establish a socket connection with IP and port.
                // TODO: timeout? Is 300 a good number?
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(IP, port), 300);
                socket.close();
                // TODO: Do we need synchronized? no other thread will have the same IP address.
                synchronized (portMap) {
                    // If there is already an entry the hashmap, add to the linked list.
                    // Otherwise, create a new linkedlist and add that to the hashmap.
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
                // TODO: We don't want worker threads printing.
                if (namedPorts.containsKey(port)){
                    System.out.println("IP: " + IP + ", " + namedPorts.get(port) + " port found at : " + port);
                } else {
                    System.out.println("IP: " + IP + " port found at : " + port);
                }
            } catch (Exception ex) {// If we miss a port, don't worry about it. We're in the background, anyways.
            }

        }
    }
}