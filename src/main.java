/*Matthew Anthony; CIS 3296; IOT Security Project

The program will: tell ip, scan network for iot devices, run port scans on iot devices, print firmwares and
help for updating if devices need to be. All will be printed in audit.

*/

import java.net.InetAddress;
import java.io.*;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.util.*;
import java.time.Clock;

public class main {

    static int devicesFound = 0;
    static final int NUM_WORKERS = 255;// How many threads we have going.
    static HashMap<String, Queue> data = new HashMap<>();

    // Driver code
    public static void main(String[] args) throws IOException {
       Scanner scan = new Scanner(System.in);
       printOpening();
       System.out.println("Would you like to run a quick scan (1) or would you like to scan a certain " +
                "range (2)? Enter 1 or 2.");
       int choice = scan.nextInt();
        /*
        Initialize the pool of scanners as well as the pool of threads.
        We have a matrix of IP addresses, which will divide up enough IP addresses for each thread.
        Then, we also make a linked list to keep track of all IP addresses which contain a device.
         */
       IPScan[] pool = new IPScan[NUM_WORKERS];
       Thread[] threads = new Thread[NUM_WORKERS];
       String[][] IPMax;
       LinkedListString hits = new LinkedListString();
       Clock clock = Clock.systemDefaultZone();
       long start = clock.millis();
       /*
       We get the user's IP address, then populate our IP matrix with all possible IP addresses within the range 0-255.
       Loop through the pool of Scanners, initialize them, and start the thread to scan over all IP addresses given to that thread.
       This will also extend to the linked list as soon as we find an IP address that contains a device.
        */
       switch(choice) {
           case 1:
               String subnet = getSubnet();
               System.out.println("Current IP subnet to scan is : " + subnet);
               IPMax = populateIPRange(subnet, 1, 255);  //edit subnet here
               // EVERYONE GET READY TO START YOUR ENGINES.
               for (int i = 0; i < NUM_WORKERS; i++){
                   pool[i] = new IPScan(hits, IPMax[i]);
                   threads[i] = new Thread(pool[i], "Worker " + i);
                   threads[i].start(); // Start all threads.
               }
               break;
           case 2:
               System.out.println("Enter your desired subnet to scan: ");
               String sub = scan.next();
               System.out.println("Enter your max range: ");
               int maxRange = scan.nextInt();
               IPMax = populateIPRange(sub, 1, maxRange);
               // I don't want these engines to start.
               for (int i = 0; i < NUM_WORKERS; i++){
                   pool[i] = new IPScan(hits, IPMax[i]);
                   threads[i] = new Thread(pool[i], "Worker " + i);
                   threads[i].start();// But they do.
               }
               break;
           default:
               System.out.println("Error: value entered was not in range.");
               System.exit(0);
       }

       /*
       Join all threads (i.e. wait for them to finish) and then find how many devices we connected to.
       Print a report per worker letting us know how many devices the worker found.
       If a worker does not find any devices, don't bother printing it out.
        */
       joinThreads(threads);
       for (int i = 0; i < NUM_WORKERS; i++) {
           if (pool[i].getDevicesFound() == 0) {
               continue;
           }
           System.out.println("Worker " + i + " found " + pool[i].getDevicesFound() + " devices.");
       }
       // Timing report.
       long end = clock.millis();
       System.out.println("We found " + hits.length() + " devices in " + (end - start) / 1000 + " seconds.");

       /*
       Begin to sweep ver the ports.
       This requires making 2 pools of port scanners: one for priority ports and one for non-priority ports.
       We also create a pool of threads for both port scanners.
       Each IP address we found is given a thread.
       We make 2 hashmaps, which map String keys to a linkedlist of integers. The linkedlist contains all open ports for the given String (which is an IP address).
        */
       System.out.println("\nStarting PRIORITY port sweep of all devices...");
       System.out.println("****************************************************");
       PriorityScanner[] priorityPortPool = new PriorityScanner[hits.length()];
       WorkerScanner[] workerPortPool = new WorkerScanner[hits.length()];
       Thread[] priorityThreads = new Thread[hits.length()];
       Thread[] workerThreads = new Thread[hits.length()];
       HashMap<String, LinkedListInt> priorityPortMap = new HashMap<>();
       HashMap<String, LinkedListInt>  workerPortMap = new HashMap<>();
       // i is used for placeholders in the pool arrays. It's more important to make sure tmp (the linkedList node) is not null, so we use a while loop instead of for loop over i.
       int i = 0;
       LinkedListString.Node tmp = hits.head.next;
       start = clock.millis();
       while (tmp != null){
           /*
           Instantiate the pool of scanners, and start them.
            */
           priorityPortPool[i] = new PriorityScanner(priorityPortMap, tmp.val);
           priorityThreads[i] = new Thread(priorityPortPool[i], "PriorityScanner " + i);
           priorityThreads[i].start();
           tmp = tmp.next;
           i++;
       }
        /*
        We need to wait for the priority threads to finish so that we can report on the priority ports we scanned.
         */
      joinThreads(priorityThreads);
      end = clock.millis();// Time reports.
      System.out.println("We priority swept through " + hits.length() + " devices in " + (end - start) / 1000 + " seconds.");


       // Now start the worker buddies.
      System.out.println("\nStarting NON-PRIORITY port sweep of all devices...");
      System.out.println("****************************************************");
      start = clock.millis();

      i = 0;
      tmp = hits.head.next;
      while (tmp != null){
           /*
           Instantiate the pool of scanners, and start them.
            */
            workerPortPool[i] = new WorkerScanner(data, workerPortMap, tmp.val);
            workerThreads[i] = new Thread(workerPortPool[i], "WorkerScanner " + i);
            workerThreads[i].start();
            tmp = tmp.next;
            i++;
        }


       joinThreads(workerThreads);
       end = clock.millis();// Time reports.
       System.out.println("We NON-PRIORITY swept through " + hits.length() + " devices in " +
               (end - start) / 1000 + " seconds.");

       outputLog(clock);
    }

    public static String getSubnet(){
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost(); //get the ip address of the machine running the scan
        } catch (UnknownHostException e){
            e.printStackTrace();
        }
        System.out.println("Current IP address is : " + localHost.getHostAddress());
        String subnetString = localHost.getHostAddress(); //get the local ip as a string
        String subnet = subnetString.substring(0, subnetString.lastIndexOf("."));//get the subnet
        return subnet;
    }

    // Function to join all threads within an array of threads.
    public static void joinThreads(Thread[] threads){
        for (int i = 0; i < threads.length; i++){
            try {// Join all threads (i.e. wait for them to finish) and then find how many devices we connected to.
                threads[i].join();
            } catch (InterruptedException e){
                System.out.println("Interrupted during join");
            }
        }
    }

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

    public static void outputLog(Clock clock){
        Set<String> keys = data.keySet();
        List<String> list = new ArrayList<>(keys);
        Collections.sort(list);
        BufferedWriter file;
        try {
            file = new BufferedWriter(new FileWriter("report.log"));
        } catch (IOException uhOh){
            System.out.println("Log file could not be created. Patrick, should we do something about this?");
            return;
        }
        try {
            file.write("SHIT Scanner Report: " + clock.system(ZoneId.of("America/NYC")));
        } catch (IOException uhOh){
            System.out.println("Trouble writing to file. Patty Ice, should we do something about this?");
        }

    }

    public static void printOpening(){
        // ASCII COLORS
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_PURPLE = "\u001B[35m";
        final String ANSI_CYAN = "\u001B[36m";

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
