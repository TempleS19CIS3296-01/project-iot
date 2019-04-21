
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
    private Set<Integer> priorityPorts;


    public PriorityScanner(HashMap portMap, String ip){
        this.portMap = portMap;
        IP = ip;
        priorityPorts = new PortNameMap().getMap().keySet();
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

        for (Integer port : priorityPorts){
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
                System.out.println("IP: "+ IP+ " port found at : " + port);

                //do matts curl here
                URL socketURL = new URL("http://" + IP + ":" + port + "/setup/eureka_info");
                if (successfulSocketConn(socketURL)){
                    System.out.println("A successful socket connection was established at: " + socketURL);
                    System.out.println("*****************************************************************");
                    System.out.println(jsonConverter(socketURL));
                    System.out.println("*****************************************************************");
                }

            } catch (Exception itHappens) {// If we miss a port because of a bad connection, it's OK.
            }
        }
    }

}