
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.HashMap;


public class PriorityScanner implements Runnable{
    private HashMap<String, LinkedListInt> portMap;
    private String IP;
    LinkedListInt targetPorts;


    public PriorityScanner(HashMap portMap, String ip){
        this.portMap = portMap;
        IP = ip;
        targetPorts = getTargetPorts();//generate list of "high risk" ports
    }
    
    public void run(){
        // Iterate over all ports.
        LinkedListInt.Node port = targetPorts.head.next;
        boolean morePorts = true;

        while (morePorts){
            try {
                // Try to establish a socket connection with IP and port.
                // TODO: timeout? Is 300 a good number?
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(IP, port.val), 300);
                socket.close();
                // TODO: Do we need synchronized? no other thread will have the same IP address.
                synchronized (portMap) {
                    // If there is already an entry the hashmap, add to the linked list.
                    // Otherwise, create a new linkedlist and add that to the hashmap.
                    LinkedListInt portList = portMap.get(IP);
                    if (portList != null){// If we already have an element for this IP address.
                        portList.add(port.val);
                        portMap.put(IP, portList);
                    } else {// We do not have any ports yet for this IP address.
                        portList = new LinkedListInt();
                        portList.add(port.val);
                        portMap.put(IP, portList);
                    }
                }
                System.out.println("IP: "+ IP+ " port found at : " + port.val);
            } catch (Exception ex) {
            }

            if (port.next == null){
                morePorts = false;//if there are no more ports, exit while loop
            }
            else {
                port = port.next;//otherwise, go to next port
            }
        }
    }

    // Get linkedlist of target ports - eventually, this should be generated from our database
    public LinkedListInt getTargetPorts(){
        
        
        LinkedListInt targetPorts = new LinkedListInt();

        //eventually, this is where we'll hit a database to generate a more specific list of target ports
        targetPorts.add(20);
        targetPorts.add(21);
        targetPorts.add(22);
        targetPorts.add(23);
        targetPorts.add(25);
        targetPorts.add(53);
        targetPorts.add(67);
        targetPorts.add(68);
        targetPorts.add(69);
        targetPorts.add(80);
        targetPorts.add(110);
        targetPorts.add(123);
        targetPorts.add(137);
        targetPorts.add(138);
        targetPorts.add(139);
        targetPorts.add(143);
        targetPorts.add(161);
        targetPorts.add(162);
        targetPorts.add(179);
        targetPorts.add(389);
        targetPorts.add(443);
        targetPorts.add(636);
        targetPorts.add(989);
        targetPorts.add(990);

        return targetPorts;
    }
}