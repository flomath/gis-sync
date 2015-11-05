package at.sync.util;

import at.sync.model.POI;

import java.io.Reader;
import java.util.List;

/**
 * Created by florianmathis on 05/11/15.
 */
public interface IUtility {
    void fetchDataFromUrl(String url);
    List<POI> getPOIs();
}
