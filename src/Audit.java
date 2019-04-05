
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
        } catch (NMapInitializationException | NMapExecutionException e){
            e.printStackTrace();
        }
        if (!nmap4j.hasError()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            ByteArrayInputStream input = null;
            Document doc = null;
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
            //System.out.println(nmap4j.getOutput());
        } else {
            System.out.println(nmap4j.getExecutionResults().getErrors());
        }

    }
}
