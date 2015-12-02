package at.sync.util;

import at.sync.model.TransportationRoute;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class JsonUtility implements IUtility {
    private static final String OSM_ROOT = "elements";

    @Override
    public List<TransportationRoute> fetchTransportationRoutes(String url) {
        List<TransportationRoute> result = new ArrayList<TransportationRoute>();

        try {
            InputStream is = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder jsonDataString = new StringBuilder();
            int cp;
            while ( (cp = reader.read()) != -1 ) {
                jsonDataString.append((char) cp);
            }

            JSONObject root = new JSONObject(jsonDataString.toString());
            JSONArray arr = root.getJSONArray(OSM_ROOT);
            for ( int i = 0; i < arr.length(); i++ ) {
                String post_id = arr.getJSONObject(i).getString("id");
                System.out.println(post_id);
            }


        } catch (Exception exc) {

        }

        return result;
    }
}
