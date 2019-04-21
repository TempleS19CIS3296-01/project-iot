import com.google.gson.*;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class jsonParser {
    //function takes in a url and returns a string of the formatted json data
    public static String jsonConverter(URL url) {
        String json = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String jsonData; (jsonData = reader.readLine()) != null;) {

                JsonElement jsonElement = new JsonParser().parse(jsonData);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                json = gson.toJson(jsonElement);
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


    //function to parse json data
    public static String jsonParser(String fullJSON, String getParam) throws JSONException {
        String extractedJson = fullJSON;
        JSONObject obj = new JSONObject(extractedJson);
        extractedJson = (String) obj.get(getParam);
        return extractedJson;
    }


    public static void main(String[] args) throws MalformedURLException, JSONException {
        URL url = new URL("http://10.0.0.7:8008/setup/eureka_info");
        String extractedJSON = jsonParser(jsonConverter(url), "cast_build_revision");
        System.out.println(extractedJSON);

    }
}
