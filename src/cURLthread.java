import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import org.json.JSONException;
import org.json.JSONObject;

public class cURLthread implements Runnable {
    private int port;
    private String IP;
    private int upToDate = 0;

    public cURLthread(int port, String IP){
        this.port = port;
        this.IP = IP;
    }


    @Override
    public void run() {
        //do matts curl here
        URL socketURL = null;
        try {
            socketURL = new URL("http://" + IP + ":" + port + "/setup/eureka_info");
        } catch (MalformedURLException e){
            return;// We can't do anything if the URL doesn't work.
        }
        try {
            if (successfulSocketConn(socketURL)){
                GoogleHome g = new GoogleHome();
                System.out.println("A successful socket connection was established at: " + socketURL);
                System.out.println("*****************************************************************");
                System.out.println(jsonConverter(socketURL));
                System.out.println("*****************************************************************");
                try {
                    String localFirmVersion = jsonParser(jsonConverter(socketURL), "cast_build_revision");
                    System.out.println("The most up to date firmware version to be on is " + g.getRecentFirmWareVersion() + " and you are on version " + localFirmVersion + ".");
                    if (g.checkFirmware(localFirmVersion)){
                        upToDate = 1;
                    } else {
                        upToDate = -1;
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //function takes in a url and returns a string of the formatted json data
    public String jsonConverter(URL url) {
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
    public String jsonParser(String fullJSON, String getParam) throws JSONException {
        String extractedJson = fullJSON;
        JSONObject obj = new JSONObject(extractedJson);
        extractedJson = (String) obj.get(getParam);
        return extractedJson;
    }

    //method to check is socket creates a successful connection
    public boolean successfulSocketConn(URL url) throws IOException {
        HttpURLConnection socketConn = (HttpURLConnection)url.openConnection();
        try {
            if (socketConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (UnknownHostException | SocketException expected){// This will happen.
        }
        return false;
    }

    public int getUpToDate(){
        return upToDate;
    }
}
