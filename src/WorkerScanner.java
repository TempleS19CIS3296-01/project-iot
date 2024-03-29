

import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class WorkerScanner implements Runnable{
    private HashMap<String, Queue> logger;
    private HashMap<String, LinkedListInt> portMap;
    private String IP;
    private HashMap namedPorts = new PortNameMap().getMap();

    public WorkerScanner(HashMap logger, HashMap portMap, String ip){
        this.logger = logger;
        this.portMap = portMap;
        IP = ip;
    }

    public void run(){
        // Iterate over all ports.
        LinkedList<Thread> loggingThreadPool = new LinkedList<>();
        for(int port = 0; port < 65535; port++){
            try {
                // Try to establish a socket connection with IP and port.
                // TODO: timeout? Is 300 a good number?
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(IP, port), 300);
                socket.close();

                addToPortMap(port);


                // TODO: We don't want worker threads printing... Or do we?
                if (namedPorts.containsKey(port)){
                    System.out.println("IP: " + IP + ", " + namedPorts.get(port) + " port found at : " + port);
                } else {
                    System.out.println("IP: " + IP + " Unnamed port found at : " + port);
                }

                cURLthread t = new cURLthread(port, IP);
                Thread thread = new Thread(t, "cURLer");
                thread.start();
                Logger log = new Logger(logger, IP, thread, port, namedPorts, t);
                Thread loggingThread = new Thread(log, "Logger");
                loggingThreadPool.add(loggingThread);
                loggingThread.start();
                //log(port, thread);
            } catch (Exception expected) {// We expect we won't be able to hit many ports.
            }
        }
        for (int i = 0; i < loggingThreadPool.size(); i++){
            try {
                loggingThreadPool.get(i).join();
            } catch (InterruptedException e){
            }
        }
    }
    // TODO: Do we need synchronized? no other thread will have the same IP address.
    synchronized void addToPortMap(int port){
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
}