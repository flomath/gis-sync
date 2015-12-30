package at.sync.util;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Author: nschoch
 * Date: 30.12.15
 * Time: 11:10
 */
public class SyncConfigReader {
    private static final String CONFIG_SYNC_FILE = "/src/config_sync.json";

    /**
     * Get all poi types from specified config sync file
     *
     * @return
     */
    public static HashMap<String, String> getPOITypes() {
        HashMap<String, String> poiTypes = new HashMap<>();
        JsonObject json = FileReaderUtility.readJson(CONFIG_SYNC_FILE);

        if ( json != null ) {
            try {
                JsonObject poi_types = json.getJsonObject("poi_types");
                Set<String> keys = poi_types.keySet();

                for (String key : keys) {
                    JsonString idValue = (JsonString) poi_types.get(key);
                    poiTypes.put(key, idValue.getString());
                }
            } catch (Exception e) {
                // no poi_types available
            }
        }

        return poiTypes;
    }

    /**
     * Get all transportation types from specified config sync file
     *
     * @return
     */
    public static HashMap<String, String> getTransportationTypes() {
        HashMap<String, String> transportationTypes = new HashMap<>();
        JsonObject json = FileReaderUtility.readJson(CONFIG_SYNC_FILE);

        if ( json != null ) {
            try {
                JsonObject transportation_types = json.getJsonObject("transportation_types");
                Set<String> keys = transportation_types.keySet();

                for (String key : keys) {
                    JsonString idValue = (JsonString) transportation_types.get(key);
                    transportationTypes.put(key, idValue.getString());
                }
            } catch (Exception e) {
                // no transportation_types available
            }
        }

        return transportationTypes;
    }

}
