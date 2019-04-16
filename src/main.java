/*Matthew Anthony; CIS 3296; IOT Security Project

The program will: tell ip, scan network for iot devices, run port scans on iot devices, print firmwares and
help for updating if devices need to be. All will be printed in audit.

*/

import java.net.InetAddress;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.time.Clock;

public class main {

    static int devicesFound = 0;
    static final int NUM_WORKERS = 255;// How many threads we have going.


    // ASCII COLORS
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";


    /**
     * Populates an array of IP addresses
     * @param ip - base IP address ex)192.168.1, 10.0.0
     * @param min - starting IP address ex) 0, 1
     * @param max - ending IP address ex)256, 20
     * @return IPs - string of IP addresses
     */
    public static String[][] populateIPRange(String ip, int min, int max){
        // The ceiling of the quotient makes sure we don't miss any IP addresses.
        // Any additional array elements created by this will be set to null, and
        // our sendIPRequest function handles null strings.
        String[][] IPs = new String[NUM_WORKERS][(int)Math.ceil(1.0*max/NUM_WORKERS)];
        int index1 = 0;
        int index2 = 0;
        String s;
        while(index2 < Math.ceil(1.0*max/NUM_WORKERS)){
            // If we have already populated 255 ip addresses, then add null.
            if (index2 * NUM_WORKERS + index1 >= max){
                s = null;
            } else {
                s = ip + "." + min;
            }
            IPs[index1][index2] = s;
            min++;
            index1++;
            if (index1 >= NUM_WORKERS){
                index1 = 0;
                index2++;
            }
        }
        return IPs;
    }



    // Driver code
    public static void main(String[] args) throws IOException {
       Scanner scan = new Scanner(System.in);
       printOpening();
       System.out.println("Would you like to run a quick scan (1) or would you like to scan a certain " +
                "range (2)? Enter 1 or 2.");
       int choice = scan.nextInt();

       Scan[] pool = new Scan[NUM_WORKERS];// Our pool of SHIT-scanners.
       Thread[] threads = new Thread[NUM_WORKERS];// Pool of threads........ this gets awkward.
       String[][] IPMax;// 2d array so that each thread can get its own IPrange.
       LinkedListString hits = new LinkedListString();// Keep a linked list for storing all ip addresses we find.
       Clock clock = Clock.systemDefaultZone();
       long start = clock.millis();
       switch(choice) {
           case 1:
               InetAddress localHost = InetAddress.getLocalHost(); //get the ip address of the machine running the scan
               System.out.println("Current IP address is : " + localHost.getHostAddress());
               String subnetString = localHost.getHostAddress(); //get the local ip as a string
               String subnet = subnetString.substring(0, subnetString.lastIndexOf("."));//get the subnet
               System.out.println("Current IP subnet to scan is : " + subnet);
               IPMax = populateIPRange(subnet, 1, 255);  //edit subnet here
               // EVERYONE GET READY TO START YOUR ENGINES.
               for (int i = 0; i < NUM_WORKERS; i++){
                   pool[i] = new Scan(hits, IPMax[i]);
                   threads[i] = new Thread(pool[i], "Worker " + i);
                   threads[i].start();// Start all threads.
               }
               //needs to wait till all threads are done
               break;
           case 2:
               System.out.println("Enter your desired subnet to scan: ");
               String sub = scan.next();
               System.out.println("Enter your max range: ");
               int maxRange = scan.nextInt();
               IPMax = populateIPRange(sub, 1, maxRange);
               // I don't want these engines to start.
               for (int i = 0; i < NUM_WORKERS; i++){
                   pool[i] = new Scan(hits, IPMax[i]);
                   threads[i] = new Thread(pool[i], "Worker " + i);
                   threads[i].start();// But they do.
               }
               //needs to wait till all threads are done
               break;
           default:
               System.out.println("Error: value entered was not in range.");
               System.exit(0);
       }

       for (int i = 0; i < NUM_WORKERS; i++){
           try {// Join all threads (i.e. wait for them to finish) and then find how many devices we connected to.
               threads[i].join();
           } catch (InterruptedException e){
               System.out.println("Interrupted during join");
           }
       }
        for (int i = 0; i < NUM_WORKERS; i++){
            if(pool[i].getDevicesFound() == 0) {
                continue;//don't print threads that returned no devices.
            }
            System.out.println("Worker " + i + " found " + pool[i].getDevicesFound() + " devices.");
        }
       long end = clock.millis();
       System.out.println("We found " + hits.length() + " devices in " + (end - start) / 1000 + " seconds.");

       System.out.println("Starting port sweep of all devices...");
       PortScanner[] portPool = new PortScanner[hits.length()];
       threads = new Thread[hits.length()];
       HashMap<String, LinkedListInt> openPorts = new HashMap<>();
       int i = 0;
       LinkedListString.Node tmp = hits.head.next;
       start = clock.millis();
       while (tmp != null){
           portPool[i] = new PortScanner(openPorts, tmp.val);
           threads[i] = new Thread(portPool[i], "PortScanner " + i);
           threads[i].start();
           tmp = tmp.next;
           i++;
       }

       for (i = 0; i < hits.length(); i++){
           try {// Join all threads (i.e. wait for them to finish) and then find how many devices we connected to.
               threads[i].join();
           } catch (InterruptedException e){
               System.out.println("Interrupted during join");
           }
       }
       end = clock.millis();
       System.out.println("We swept through " + hits.length() + " devices in " + (end - start) / 1000 + " seconds.");

       // portIP is a dictionary where each key is a string representing the IP address and each value is a LinkedList representing all accessible Ports.
       HashMap portIP = portPool[0].getPorts();

       // To get the set of all keys, use portIP.keySet().
       for (Object key : portIP.keySet()){
           // To access a certain element, use get.
           LinkedListInt element = (LinkedListInt)portIP.get((String)key);
           // Example of how to get ip-port pair.
           String ipAddress = (String) key;
           int portNumber = element.remove();
       }



    }

    public static void printOpening(){
        System.out.println();
        System.out.println(ANSI_RED+" $$$$$$\\"+ANSI_YELLOW+"  $$\\   $$\\"+ANSI_GREEN+" $$$$$$\\"+ANSI_CYAN+" $$$$$$$$\\         "+ANSI_BLUE+" $$$$$$\\  "+ANSI_PURPLE+" $$$$$$\\"+ANSI_RED+"   $$$$$$\\ "+ANSI_YELLOW+" $$\\   $$\\"+ANSI_GREEN+" $$\\   $$\\"+ANSI_CYAN+" $$$$$$$$\\ "+ANSI_BLUE+"$$$$$$$\\");
        System.out.println(ANSI_RED+"$$  __$$\\"+ANSI_YELLOW+" $$ |  $$ |"+ANSI_GREEN+"\\_$$  _|"+ANSI_CYAN+"\\__$$  __|       "+ANSI_BLUE+" $$  __$$\\ "+ANSI_PURPLE+"$$  __$$\\"+ANSI_RED+" $$  __$$\\"+ANSI_YELLOW+" $$$\\  $$ |"+ANSI_GREEN+"$$$\\  $$ |"+ANSI_CYAN+"$$  _____|"+ANSI_BLUE+"$$  __$$\\");
        System.out.println(ANSI_RED+"$$ /  \\__|"+ANSI_YELLOW+"$$ |  $$ |"+ANSI_GREEN+"  $$ |     "+ANSI_CYAN+"$$ |           "+ANSI_BLUE+"$$ /  \\__|"+ANSI_PURPLE+"$$ /  \\__|"+ANSI_RED+"$$ /  $$ |"+ANSI_YELLOW+"$$$$\\ $$ |"+ANSI_GREEN+"$$$$\\ $$ |"+ANSI_CYAN+"$$ |      "+ANSI_BLUE+"$$ |  $$ |");
        System.out.println(ANSI_RED+"\\$$$$$$\\  "+ANSI_YELLOW+"$$$$$$$$ |"+ANSI_GREEN+"  $$ |     "+ANSI_CYAN+"$$ |          "+ANSI_BLUE+" \\$$$$$$\\  "+ANSI_PURPLE+"$$ |      "+ANSI_RED+"$$$$$$$$ |"+ANSI_YELLOW+"$$ $$\\$$ |"+ANSI_GREEN+"$$ $$\\$$ |"+ANSI_CYAN+"$$$$$\\    "+ANSI_BLUE+"$$$$$$$  |");
        System.out.println(ANSI_RED+" \\____$$\\ "+ANSI_YELLOW+"$$  __$$ |"+ANSI_GREEN+"  $$ | "+ANSI_CYAN+"    $$ |            "+ANSI_BLUE+"\\____$$\\"+ANSI_PURPLE+" $$ |      "+ANSI_RED+"$$  __$$ |"+ANSI_YELLOW+"$$ \\$$$$ |"+ANSI_GREEN+"$$ \\$$$$ |"+ANSI_CYAN+"$$  __|"+ANSI_BLUE+"   $$  __$$<");
        System.out.println(ANSI_RED+"$$\\   $$ |"+ANSI_YELLOW+"$$ |  $$ |"+ANSI_GREEN+"  $$ |     "+ANSI_CYAN+"$$ |           "+ANSI_BLUE+"$$\\   $$ |"+ANSI_PURPLE+"$$ |  $$\\"+ANSI_RED+" $$ |  $$ |"+ANSI_YELLOW+"$$ |\\$$$ |"+ANSI_GREEN+"$$ |\\$$$ |"+ANSI_CYAN+"$$ |      "+ANSI_BLUE+"$$ |  $$ |");
        System.out.println(ANSI_RED+"\\$$$$$$  |"+ANSI_YELLOW+"$$ |  $$ |"+ANSI_GREEN+"$$$$$$\\    "+ANSI_CYAN+"$$ |           "+ANSI_BLUE+"\\$$$$$$  |"+ANSI_PURPLE+"\\$$$$$$  |"+ANSI_RED+"$$ |  $$ |"+ANSI_YELLOW+"$$ | \\$$ |"+ANSI_GREEN+"$$ | \\$$ |"+ANSI_CYAN+"$$$$$$$$\\ "+ANSI_BLUE+"$$ |  $$ |");
        System.out.println(ANSI_RED+" \\______/ "+ANSI_YELLOW+"\\__|  \\__|"+ANSI_GREEN+"\\______|   "+ANSI_CYAN+"\\__|           "+ANSI_BLUE+" \\______/  "+ANSI_PURPLE+"\\______/ "+ANSI_RED+"\\__|  \\__|"+ANSI_YELLOW+"\\__|  \\__|"+ANSI_GREEN+"\\__|  \\__|"+ANSI_CYAN+"\\________|"+ANSI_BLUE+"\\__|  \\__|"+ANSI_RESET);
        System.out.println();
    }
}
