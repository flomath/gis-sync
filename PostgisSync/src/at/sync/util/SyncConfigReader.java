package at.sync.util;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import java.util.HashMap;
import java.util.Set;

/**
 * Author: nschoch
 * Date: 30.12.15
 * Time: 11:10
 */
public class SyncConfigReader {
    private static final String CONFIG_FILE_POI_TYPES = "/src/config_poi_types.json";
    private static final String CONFIG_FILE_TRANSPORTATION_TYPES = "/src/config_transportation_types.json";

    /**
     * Get all poi types from specified config file
     * <p>
     * Key          : Value
     * OSM string   : UUID database
     *
     * @return
     */
    public static HashMap<String, String> getPOITypes() {
        HashMap<String, String> poiTypes = new HashMap<>();
        JsonObject json = FileReaderUtility.readJson(CONFIG_FILE_POI_TYPES);

        if ( json != null ) {
            try {
                JsonObject poi_types = json.getJsonObject("poi_types");
                Set<String> keys = poi_types.keySet();

                for ( String key : keys ) {
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
     * Get all transportation types from specified config file
     * <p>
     * Key          : Value
     * OSM string   : UUID database
     *
     * @return
     */
    public static HashMap<String, String> getTransportationTypes() {
        HashMap<String, String> transportationTypes = new HashMap<>();
        JsonObject json = FileReaderUtility.readJson(CONFIG_FILE_TRANSPORTATION_TYPES);

        if ( json != null ) {
            try {
                JsonObject transportation_types = json.getJsonObject("transportation_types");
                Set<String> keys = transportation_types.keySet();

                for ( String key : keys ) {
                    JsonString idValue = (JsonString) transportation_types.get(key);
                    transportationTypes.put(key, idValue.getString());
                }
            } catch (Exception e) {
                // no transportation_types available
            }
        }

        return transportationTypes;
    }

    /**
     * Write given POI types to config file
     *
     * @param poiTypes
     */
    public static void addPOITypes(HashMap<String, String> poiTypes) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        for ( String key : poiTypes.keySet() ) {
            objectBuilder.add(key, poiTypes.get(key));
        }

        JsonObject object = Json.createObjectBuilder()
                .add("poi_types", objectBuilder).build();

        FileReaderUtility.writeJSON(CONFIG_FILE_POI_TYPES, object);
    }

    /**
     * Write given transportation types to config file
     *
     * @param transportationTypes
     */
    public static void addTransportationTypes(HashMap<String, String> transportationTypes) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        for ( String key : transportationTypes.keySet() ) {
            objectBuilder.add(key, transportationTypes.get(key));
        }

        JsonObject object = Json.createObjectBuilder()
                .add("transportation_types", objectBuilder).build();

        FileReaderUtility.writeJSON(CONFIG_FILE_TRANSPORTATION_TYPES, object);
    }
}
