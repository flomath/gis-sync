package at.sync.util;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Author: nschoch
 * Date: 30.12.15
 * Time: 11:00
 */
public class FileReaderUtility {

    /**
     * Read JSON file from url
     *
     * Returns null if JSON file is not available
     *
     * @param url
     * @return
     */
    public static JsonObject readJson(String url) {
        JsonObject object = null;
        try {
            String path = new File("").getAbsolutePath() + url;
            JsonReader reader = Json.createReader(new FileReader(path));

            object = reader.readObject();
        } catch (Exception e) {
            // no json file available
        }

        return object;
    }

    public static void writeJSON() {
        //TODO
    }
}
