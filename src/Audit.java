
import org.nmap4j.Nmap4j;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.text.html.parser.Parser;
import javax.xml.parsers.*;
import java.io.*;

public class Audit implements Runnable{

    private String ip;
    private BufferedWriter f;
    // NOTE: Currently, nmap4j only works for POSIX machines.
    private static String OS = System.getProperty("os.name").toLowerCase();
    private Nmap4j nmap;

    public Audit(String ip, BufferedWriter f) {
        this.ip = ip;
        this.f = f;
        if (isWindows()) {
            nmap = new Nmap4j("/c/Program Files/Nmap/nmap");
        } else if (isMac()) {
            nmap = new Nmap4j("/usr/local");
        }
    }


    @Override
    public void run() {
        // includeHosts adds the ip address to be audited.
        nmap.includeHosts(ip);
        // -Pn treats the host as online, so we don't get blocked.
        nmap.addFlags("-Pn");
        try {
            // If this execution does not have an error, the output will be an XML string.
            nmap.execute();
        } catch (NMapInitializationException | NMapExecutionException e){
            e.printStackTrace();
        }
        if (!nmap.hasError()) {
            // Everything we do could throw an error!!
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            ByteArrayInputStream input;
            Document doc;
            NodeList nList;
            // I honestly have no idea what any of this does but it is more or less copied from GeeksForGeeks so it must be good.
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e){
                e.printStackTrace();
                return;
            }
            StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append(nmap.getOutput());
            try {
                input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
                return;
            }
            try {
                doc = builder.parse(input);

            } catch (SAXException | IOException e){
                e.printStackTrace();
                return;
            }
            doc.getDocumentElement().normalize();
            // This begins the XML parsing.
            //Element root = doc.getDocumentElement();
            nList = doc.getElementsByTagName("port");
            int nListLength = nList.getLength();
            if (nListLength > 0){
                System.out.println("IP address: " + ip);
                System.out.print("Ports: ");
            }
            for (int tmp = 0; tmp < nListLength; tmp++){
                Node nNode = nList.item(tmp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element)nNode;
                    // Formatting to prevent a comma on the last element.
                    if (tmp < nListLength - 1) {
                        System.out.print(eElement.getAttribute("portid") + ", ");
                    } else {
                        System.out.println(eElement.getAttribute("portid"));
                    }
                }
            }
            // Write the entire XML to a log file.
            synchronized (f){
                try {
                    f.write(nmap.getOutput());
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println(nmap.getExecutionResults().getErrors());
        }

    }

    // The following functions test for different Operating Systems.
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }
    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }
    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

}
