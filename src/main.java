/*Matthew Anthony; CIS 3296; IOT Security Project

The program will: tell ip, scan network for iot devices, run port scans on iot devices, print firmwares and
help for updating if devices need to be. All will be printed in audit.

*/

import java.net.InetAddress;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class main {

    static int devicesFound = 0;
    static final int NUM_WORKERS = 4;// How many threads we have going.



    /**
     * Populates an array of IP addresses
     * @param ip - base IP address ex)192.168.1, 10.0.0
     * @param min - starting IP address ex) 0, 1
     * @param max - ending IP address ex)256, 20
     * @return IPs - string of IP addresses
     */
    public static String[][] populateIPRange(String ip, int min, int max, int size){
        String[][] IPs = new String[size][max/size];
        int index1 = 0;
        int index2 = 0;
        while(index1 < size){
            String s = ip + "." + min;
            IPs[index1][index2] = s;
            min++;
            index2++;
            if (index2 >= max/size){
                index1++;
                index2 = 0;
            }
        }
        return IPs;
    }



    // Driver code
    public static void main(String[] args) throws IOException {
       Scanner scan = new Scanner(System.in);
       System.out.println("Welcome to SHIT Scanner. Would you like to run a quick scan (1) or would you like to scan a certain " +
                "range (2)? Enter 1 or 2.");
       int choice = scan.nextInt();

       Scan[] pool = new Scan[NUM_WORKERS];// Our pool of SHIT-scanners.
       Thread[] threads = new Thread[NUM_WORKERS];// Pool of threads........ this gets awkward.
       String[][] IPMax;// 2d array so that each thread can get its own IPrange.
       LinkedList hits = new LinkedList();// Keep a linked list for storing all ip addresses we find.
       switch(choice) {
           case 1:
               InetAddress localHost = InetAddress.getLocalHost(); //get the ip address of the machine running the scan
               System.out.println("Current IP address is : " + localHost.getHostAddress());
               String subnetString = localHost.getHostAddress(); //get the local ip as a string
               String subnet = subnetString.substring(0, subnetString.lastIndexOf("."));//get the subnet
               System.out.println("Current IP subnet to scan is : " + subnet);
               IPMax = populateIPRange(subnet, 1, 255, NUM_WORKERS);  //edit subnet here
               // EVERYONE GET READY TO START YOUR ENGINES.
               for (int i = 0; i < NUM_WORKERS; i++){
                   pool[i] = new Scan(hits, IPMax[i]);
                   threads[i] = new Thread(pool[i], "Worker " + i);
                   threads[i].start();// Start all threads.
               }
               break;
           case 2:
               System.out.println("Enter your desired subnet to scan: ");
               String sub = scan.next();
               System.out.println("Enter your max range: ");
               int maxRange = scan.nextInt();
               IPMax = populateIPRange(sub, 1, maxRange, NUM_WORKERS);
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
            System.out.println("Worker " + i + " found " + pool[i].getDevicesFound() + " devices.");
        }
       System.out.println("We found " + hits.size() + " devices.");

    }
}
