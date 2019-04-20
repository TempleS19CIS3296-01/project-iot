
import java.net.*;
import java.util.HashMap;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class PriorityScanner implements Runnable{
    private HashMap<String, LinkedListInt> portMap;
    private String IP;
    LinkedListInt targetPorts;


    public PriorityScanner(HashMap portMap, String ip){
        this.portMap = portMap;
        IP = ip;
        targetPorts = getTargetPorts();//generate list of "high risk" ports
    }

    //function takes in a url and returns a string of the formatted json data
    public String jsonConverter(URL url) {
        String json = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String jsonData; (jsonData = reader.readLine()) != null;) {

                JsonElement jsonElement = new JsonParser().parse(jsonData);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                json = gson.toJson(jsonElement);
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    //method to check is socket creates a successful connection
    public boolean successfulSocketConn(URL url) throws IOException {
        HttpURLConnection socketConn = (HttpURLConnection)url.openConnection();
        if (socketConn.getResponseCode() == 200){
            return true;
        }
        return false;
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

                //do matts curl here
                URL socketURL = new URL("http://" + IP + ":" + port.val + "/setup/eureka_info");
                if (successfulSocketConn(socketURL)){
                    System.out.println("A successful socket connection was established at: " + socketURL);
                    System.out.println("*****************************************************************");
                    System.out.println(jsonConverter(socketURL));
                    System.out.println("*****************************************************************");
                }

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
        targetPorts.add(8008); //google home

        return targetPorts;
    }
}