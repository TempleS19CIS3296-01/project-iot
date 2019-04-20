import java.util.HashMap;
import java.util.Map;


public class hashmapTest {

    public static void main(String[] args)
    {

        HashMap<Integer, String> hmap = new HashMap<>();

        print(hmap);
        hmap.put(80, "HTTP");
        hmap.put(8080, "HTTP Proxy");
        hmap.put(8008, "google home boi");

        System.out.println("Size of map is: " + hmap.size());

        print(hmap);

        if (hmap.containsKey(80))
        {
            String pName = hmap.get(80);
            System.out.println("The value for key 80 is: " + pName);
        }
        hmap.clear();
        print(hmap);
    }

    public static void print(Map<Integer, String> hmap)
    {
        if (hmap.isEmpty())
        {
            System.out.println("map is empty");
        }

        else
        {
            System.out.println(hmap);
        }
    }
}
