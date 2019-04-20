import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

    public static boolean successfullSocketConn(URL url) throws IOException {
        HttpURLConnection socketConn = (HttpURLConnection)url.openConnection();
        if (socketConn.getResponseCode() == 200){
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {

        URL url = new URL("http://10.0.0.7:8008/setup/eureka_info");

        if (successfullSocketConn(url) == true){
            System.out.println(jsonConverter(url));
        }
    }
}
