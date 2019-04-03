// java curl implementation


import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class curlTest {

    public static void main(String[] args) throws IOException {

        URL url = new URL("http://10.0.0.7:8008/setup/eureka_info");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String jsonData; (jsonData = reader.readLine()) != null;) {

               JsonElement jsonElement = new JsonParser().parse(jsonData);
               Gson gson = new GsonBuilder().setPrettyPrinting().create();
               String json = gson.toJson(jsonElement);
               System.out.println(json);

            }
        }

    }

}
