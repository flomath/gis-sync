package at.sync.util;

import at.sync.model.POI;
import at.sync.model.TransportationRoute;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 *
 */
public class JsonUtility implements IUtility {

    private List<POI> pois;
    private List<TransportationRoute> transportationRoutes;

    @Override
    public void fetchDataFromUrl(String url) {
        try {
            InputStream is = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            // convert whole json to objects

        } catch (Exception exc) {

        }
    }

    @Override
    public List<POI> getPOIs() {

        return null;
    }
}
