import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class GoogleHome {
    // This is where we store the current version of firmware for the google home.
    private String recentFirmWareVersion;

    public GoogleHome(){
         recentFirmWareVersion = getFirmWareVersion();
    }
    // Function to scrape the web for the most up to date firmware version for google home.
    private String getFirmWareVersion(){
        // URL which stores google home firmware version.
        String url = "https://support.google.com/googlehome/answer/7365257?hl=en";
        // This will be our HTML document.
        Document doc = null;
        try {
            // Try to scrape the contents of the site.
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Select the Div element with class=cc, then get the first element with "strong", then convert that to text.
        String productionVersion = doc.select("div.cc").first().selectFirst("strong").text();
        return productionVersion;
    }

    /**
     *
     * @param version: The current firmware version of the Google Home (this will be found by parsing the JSON from the Audit.
     *
     */
    public boolean checkFirmware(String version){
        if (version.equals(recentFirmWareVersion)){
            System.out.println("Your Google Home is up to date.");
            return true;
        } else {
            System.out.println("Your Google Home is out of date. You are on Firmware verison " + version + ", but you should be on version " + recentFirmWareVersion + "\nPlease consider updating your device.");
            return false;
        }
    }



    // Accessor for recentFirmwareVersion.
    public String getRecentFirmWareVersion(){
        return recentFirmWareVersion;
    }

}
