
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
    // NOTE: Currently, nmap4j only works for POSIX machines.
    private Nmap4j nmap4j = new Nmap4j("/usr/local");

    public Audit(String ip){
        this.ip = ip;
    }


    @Override
    public void run() {
        // includeHosts adds the ip address to be audited.
        nmap4j.includeHosts(ip);
        // -Pn treats the host as online, so we don't get blocked.
        nmap4j.addFlags("-Pn");
        try {
            // If this execution does not have an error, the output will be an XML string.
            nmap4j.execute();
        } catch (NMapInitializationException | NMapExecutionException e){
            e.printStackTrace();
        }
        if (!nmap4j.hasError()) {
            // Everything we do could throw an error!!
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            ByteArrayInputStream input = null;
            Document doc = null;
            // I honestly have no idea what any of this does but it is more or less copied from GeeksForGeeks so it must be good.
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e){
                e.printStackTrace();
                return;
            }
            StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append(nmap4j.getOutput());
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
            // This begins the XML parsing.
            Element root = doc.getDocumentElement();
            System.out.println(root.getAttribute("ports"));
            System.out.println(nmap4j.getOutput());
        } else {
            System.out.println(nmap4j.getExecutionResults().getErrors());
        }

    }
}
