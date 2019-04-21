
import java.net.*;
import java.util.HashMap;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Set;


public class PriorityScanner implements Runnable{
    private HashMap<String, LinkedListInt> portMap;
    private String IP;
    private HashMap priorityPorts;


    public PriorityScanner(HashMap portMap, String ip){
        this.portMap = portMap;
        IP = ip;
        priorityPorts = new PortNameMap().getMap();
    }


    public void run(){
        // Iterate over all ports.

        for (Integer port : (Set<Integer>)priorityPorts.keySet()){
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
                System.out.println("IP: " + IP + ", " + priorityPorts.get(port) + " port found at : " + port);

                cURLthread t = new cURLthread(port, IP);
                Thread thread = new Thread(t, "cURLer");
                thread.start();

            } catch (Exception itHappens) {// If we miss a port because of a bad connection, it's OK.
            }
        }
    }

}