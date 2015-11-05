package at.sync.util;

import at.sync.model.POI;
import at.sync.model.TransportationRoute;

import java.io.Reader;
import java.util.List;

/**
 * Created by florianmathis on 05/11/15.
 */
public interface IUtility {
    List<TransportationRoute> fetchTransportationRoutes(String url);
}
