// java curl implementation


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class curlTest {


    public static void main(String[] args) throws IOException {

        URL url = new URL("http://10.0.0.7:8008/setup/eureka_info");

        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (; (line = reader.readLine()) != null; ) {
                System.out.println(line);
            }

        }
    }

}
