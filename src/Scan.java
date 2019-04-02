import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;

public class Scan implements Runnable{

    private volatile int devicesFound = 0;// Count how many devices we detected.
    private String[] subnetRange;
    private LinkedList hits;

    public Scan(LinkedList hits, String[] subnetRange){
        this.hits = hits;
        this.subnetRange = subnetRange;
    }



    /**
     * Sends pings to an IP address and checks if host is reachable
     * @param ipAddress - String to be pinged
     */
    public void sendPingRequest(String ipAddress)
            throws IOException
    {
        String deviceName;
        InetAddress IP = InetAddress.getByName(ipAddress);

        if (IP.isReachable(5000)) {
            System.out.println("\nHost is reachable with IP Address: " + ipAddress + "\nHost name: " +
                    IP.getCanonicalHostName());
            devicesFound++;
            synchronized (hits){
                hits.push(ipAddress);
            }
        }
        else
            System.out.println("PING FAILED: " + ipAddress);
    }

    /**
     * Scans a range of IP addresses and sees if each can be pinged
     * @param IP - a String[] of IP addresses to ping
     * @param max - ending IP address ex)256, 20
     */
    public void scanIPRange(String[] IP, int max) throws IOException{
        for (int i = 0; i < IP.length; i++){
            sendPingRequest(IP[i]);
        }
    }

    public int getDevicesFound(){
        return devicesFound;
    }

    @Override
    public void run() {
        try {
            scanIPRange(subnetRange, 256);
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("rip");
        }
    }
}
