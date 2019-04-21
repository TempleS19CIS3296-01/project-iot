import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class cURLthread implements Runnable {
    private int port;
    private String IP;

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
                System.out.println("A successful socket connection was established at: " + socketURL);
                System.out.println("*****************************************************************");
                System.out.println(jsonConverter(socketURL));
                System.out.println("*****************************************************************");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
