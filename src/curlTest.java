import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;

public class curlTest {

    public static void main(String[] args) {
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
        System.out.println(productionVersion);

    }
}
