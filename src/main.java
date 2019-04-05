/*Matthew Anthony; CIS 3296; IOT Security Project

The program will: tell ip, scan network for iot devices, run port scans on iot devices, print firmwares and
help for updating if devices need to be. All will be printed in audit.

*/

import java.net.InetAddress;
import java.io.*;
import java.util.Scanner;
import java.time.Clock;

public class main {

    static int devicesFound = 0;
    static final int NUM_WORKERS = 255;// How many threads we have going.



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



    /*
     * This is the simplified way to execute and parse Nmap output.  This is the
     * easiest way to run Nmap from this API.  Use of this class requires a little
     * more knowledge of Nmap's flags in order to work.
     * <p>
     * Here is an example of how to use this class:
     * <code>
     * Nmap4j nmap4j = new Nmap4j( "/usr/local" ) ;
     * nmap4j.includeHosts( "192.168.1.1-255" ) ;
     * nmap4j.excludeHosts( "192.168.1.110" ) ;
     * nmap4j.addFlags( "-T3 -oX - -O -sV" ) ;
     * nmap4j.execute() ;
     * if( !nma4j.hasError() ) {
     * NMapRun nmapRun = nmap4j.getResults() ;
     * } else {
     * System.out.println( nmap4j.getExecutionResults().getErrors() ) ;
     * }
     * </code>
     * <p>
     * This block would need a try/catch because the execute() method throws two
     * different exceptions.
     *
     */
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
       LinkedList2 hits = new LinkedList2();// Keep a linked list for storing all ip addresses we find.
       Clock clock = Clock.systemDefaultZone();
       System.out.println("Starting Scan...");
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
                   threads[i] = new Thread(pool[i], "Scanner " + i);
                   threads[i].start();// Start all threads.
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
                   pool[i] = new Scan(hits, IPMax[i]);
                   threads[i] = new Thread(pool[i], "Worker " + i);
                   threads[i].start();// But they do.
               }
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
       System.out.println("We found " + hits.length() + " devices in " + (end - start) / 1000.0 + " seconds.");

       Audit[] auditPool = new Audit[hits.length()];
       threads = new Thread[hits.length()];
       LinkedList2.Node tmp = hits.head.next;
       int i = 0;
       System.out.println("Starting nmap of all found devices...");
       start = clock.millis();
       while (tmp != null){
           auditPool[i] = new Audit(tmp.val);
           threads[i] = new Thread(auditPool[i], "Auditor " + i);
           threads[i].start();
           i++;
           tmp = tmp.next;
       }
        for (i = 0; i < auditPool.length; i++){
            try {// Join all threads (i.e. wait for them to finish) and then find how many devices we connected to.
                threads[i].join();
            } catch (InterruptedException e){
                System.out.println("Interrupted during join");
            }
        }
        end = clock.millis();
        System.out.println("We audited " + hits.length() + " devices in " + (end - start) / 1000.0 + " seconds.");
    }

    public static void printOpening(){
        System.out.println();
        System.out.println(" $$$$$$\\  $$\\   $$\\ $$$$$$\\ $$$$$$$$\\          $$$$$$\\   $$$$$$\\   $$$$$$\\  $$\\   $$\\ $$\\   $$\\ $$$$$$$$\\ $$$$$$$\\");
        System.out.println("$$  __$$\\ $$ |  $$ |\\_$$  _|\\__$$  __|        $$  __$$\\ $$  __$$\\ $$  __$$\\ $$$\\  $$ |$$$\\  $$ |$$  _____|$$  __$$\\");
        System.out.println("$$ /  \\__|$$ |  $$ |  $$ |     $$ |           $$ /  \\__|$$ /  \\__|$$ /  $$ |$$$$\\ $$ |$$$$\\ $$ |$$ |      $$ |  $$ |");
        System.out.println("\\$$$$$$\\  $$$$$$$$ |  $$ |     $$ |           \\$$$$$$\\  $$ |      $$$$$$$$ |$$ $$\\$$ |$$ $$\\$$ |$$$$$\\    $$$$$$$  |");
        System.out.println(" \\____$$\\ $$  __$$ |  $$ |     $$ |            \\____$$\\ $$ |      $$  __$$ |$$ \\$$$$ |$$ \\$$$$ |$$  __|   $$  __$$<");
        System.out.println("$$\\   $$ |$$ |  $$ |  $$ |     $$ |           $$\\   $$ |$$ |  $$\\ $$ |  $$ |$$ |\\$$$ |$$ |\\$$$ |$$ |      $$ |  $$ |");
        System.out.println("\\$$$$$$  |$$ |  $$ |$$$$$$\\    $$ |           \\$$$$$$  |\\$$$$$$  |$$ |  $$ |$$ | \\$$ |$$ | \\$$ |$$$$$$$$\\ $$ |  $$ |");
        System.out.println(" \\______/ \\__|  \\__|\\______|   \\__|            \\______/  \\______/ \\__|  \\__|\\__|  \\__|\\__|  \\__|\\________|\\__|  \\__|");
        System.out.println();
    }
}
