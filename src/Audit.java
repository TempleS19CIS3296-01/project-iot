
import org.nmap4j.Nmap4j;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.nmap4j.data.NMapRun;

public class Audit implements Runnable{

    private String ip;
    private Nmap4j nmap4j = new Nmap4j("/usr/local");

    public Audit(String ip){
        this.ip = ip;
    }


    @Override
    public void run() {
        nmap4j.includeHosts(ip);
        nmap4j.addFlags("-Pn");
        try {
            nmap4j.execute();
            if (!nmap4j.hasError()) {
                System.out.println(nmap4j.getOutput());
            } else {
                System.out.println(nmap4j.getExecutionResults().getErrors());
            }
        } catch (NMapInitializationException | NMapExecutionException e){
            e.printStackTrace();
        }
    }
}
