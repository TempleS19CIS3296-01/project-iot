import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class curlTest {
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

    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("https://support.google.com/googlehome/answer/7365257?hl=en");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null;) {
                System.out.println(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
