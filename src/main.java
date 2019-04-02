/*Matthew Anthony; CIS 3296; IOT Security Project

The program will: tell ip, scan network for iot devices, run port scans on iot devices, print firmwares and
help for updating if devices need to be. All will be printed in audit.

*/

import java.net.InetAddress;
import java.io.*;
import java.util.Scanner;

public class main {

    static int devicesFound = 0;

    /**
     * Sends pings to an IP address and checks if host is reachable
     * @param ipAddress - String to be pinged
     */
    public static void sendPingRequest(String ipAddress)
            throws IOException
    {
        String deviceName;
        InetAddress IP = InetAddress.getByName(ipAddress);

        if (IP.isReachable(5000)) {
            //deviceName = IP.getCanonicalHostName().substring( 0, IP.getCanonicalHostName().indexOf("."));
            //System.out.println("\nHost is reachable with IP Address: " + ipAddress + "\nHost name: " +
              //      IP.getCanonicalHostName() + "\nDevice name: " + deviceName);
            System.out.println("\nHost is reachable with IP Address: " + ipAddress + "\nHost name: " +
                    IP.getCanonicalHostName());
            devicesFound++;
        }
        else
            System.out.println("PING FAILED");
    }

    /**
     * Populates an array of IP addresses
     * @param ip - base IP address ex)192.168.1, 10.0.0
     * @param min - starting IP address ex) 0, 1
     * @param max - ending IP address ex)256, 20
     * @return IPs - string of IP addresses
     */
    public static String[] populateIPRange(String ip, int min, int max){
        String[] IPs = new String[max];
        int i = 0;
        while(i < max){
            String s = ip + "." + min;
            IPs[i] = s;
            //System.out.println("IP Address: " + IPs[i]);
            min++;
            i++;
        }
        return IPs;
    }

    /**
     * Scans a range of IP addresses and sees if each can be pinged
     * @param IP - a String[] of IP addresses to ping
     * @param max - ending IP address ex)256, 20
     */
    public static void scanIPRange(String[] IP, int max) throws IOException{
        int i = 0;
        while (i < max){
            sendPingRequest(IP[i]);
            i++;
        }
        System.out.println("\n-------------------------------------------\n" +
                "Scan completed with " + devicesFound + " devices found. " +
                "\n-------------------------------------------");
    }

    // Driver code
    public static void main(String[] args) throws IOException {
       Scanner scan = new Scanner(System.in);
       System.out.println("Welcome to Owl Scan. Would you like to run a quick scan (1) or would you like to scan a certain " +
                "range (2)? Enter 1 or 2.");
       int choice = scan.nextInt();
       switch(choice) {
           case 1:
               InetAddress localHost = InetAddress.getLocalHost(); //get the ip address of the machine running the scan
               System.out.println("Current IP address is : " + localHost.getHostAddress());
               String subnetString = localHost.getHostAddress(); //get the local ip as a string
               String subnet = subnetString.substring(0, subnetString.lastIndexOf("."));//get the subnet
               System.out.println("Current IP subnet to scan is : " + subnet);
               String[] IPMax = populateIPRange(subnet, 1, 255);  //edit subnet here
               scanIPRange(IPMax, 256);
               break;
           case 2:
               System.out.println("Enter your desired subnet to scan: ");
               String sub = scan.next();
               System.out.println("Enter your max range: ");
               int maxRange = scan.nextInt();
               String[] IPRange = populateIPRange(sub, 1, maxRange); //edit subnet here
               scanIPRange(IPRange, maxRange);
               break;

           default:
               System.out.println("Error: value entered was not in range.");
               System.exit(0);
       }
    }
}
