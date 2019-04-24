import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Logger implements Runnable {

    private HashMap<String, Queue> logger;
    private HashMap namedPorts;
    private String IP;
    private int port;
    private Thread thread;
    private cURLthread t;

    public Logger(HashMap logger, String IP, Thread thread, int port, HashMap namedPorts, cURLthread t) {
        this.logger = logger;
        this.IP = IP;
        this.thread = thread;
        this.port = port;
        this.namedPorts = namedPorts;
        this.t = t;
    }

    @Override
    public void run() {
        try {
            thread.join();
        } catch (InterruptedException ohDear) {
        }
        int versionCheck = t.getUpToDate();
        Queue<String> tmp;
        synchronized (logger) {
            if (logger.get(IP) != null) {
                // If we already have something logged for this IP, add to it.
                tmp = logger.get(IP);
                if (namedPorts.containsKey(port)) {
                    tmp.add(port + ": " + namedPorts.get(port) + ".");
                } else {
                    tmp.add(port + ": Unnamed port.");
                }
            } else {
                tmp = new LinkedList<>();
                if (namedPorts.containsKey(port)) {
                    tmp.add(port + ": " + namedPorts.get(port) + ".");
                } else {
                    tmp.add(port + ": Unnamed port.");
                }
            }
            tmp.add("Up to date: " + versionCheck);
            logger.put(IP, tmp);
        }
    }


}
