import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPScan implements Runnable{

    private volatile int devicesFound = 0;// Count how many devices we detected.
    private String[] subnetRange;
    private LinkedListString hits;

    public IPScan(LinkedListString hits, String[] subnetRange){
        this.hits = hits;
        this.subnetRange = subnetRange;
    }



    /**
     * Sends pings to an IP address and checks if host is reachable
     * @param ipAddress - String to be pinged
     */
    public void sendPingRequest(String ipAddress){
        String deviceName;
        InetAddress IP = null;
        try {
            IP = InetAddress.getByName(ipAddress);
            try {
                if (IP.isReachable(300)) {
                    System.out.println("\nHost is reachable with IP Address: " + ipAddress + "\nHost name: " +
                            IP.getCanonicalHostName());
                    devicesFound++;
                    synchronized (hits) {
                        hits.add(ipAddress);
                    }
                } //else
                    //System.out.println("PING FAILED: " + ipAddress);
            } catch (IOException e){
                System.out.println("IOException");
                e.printStackTrace();
            }
        } catch (UnknownHostException e){
            System.out.println("Unknown Host");
            e.printStackTrace();
        }

    }

    /**
     * Scans a range of IP addresses and sees if each can be pinged
     * @param IP - a String[] of IP addresses to ping
     * @param max - ending IP address ex)256, 20
     */
    public void scanIPRange(String[] IP, int max) throws IOException{
        for (int i = 0; i < IP.length; i++){
            if (IP[i] == null){// Once we get to a null string, the remaining strings in the array will be null (this should be the last element anyways.
                break;
            }
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
