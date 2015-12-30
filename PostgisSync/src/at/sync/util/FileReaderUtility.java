package at.sync.util;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Author: nschoch
 * Date: 30.12.15
 * Time: 11:00
 */
public class FileReaderUtility {

    /**
     * Read JSON file from url
     * <p>
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
            reader.close();
        } catch (Exception e) {
            // no json file available
        }

        return object;
    }

    /**
     * Create new file and add whole json object to file
     *
     * @param url
     * @param jsonObject
     */
    public static void writeJSON(String url, JsonObject jsonObject) {
        try {
            String path = new File("").getAbsolutePath() + url;
            JsonWriter writer = Json.createWriter(new FileWriter(path));

            writer.writeObject(jsonObject);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
