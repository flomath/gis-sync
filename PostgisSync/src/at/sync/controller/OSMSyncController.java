package at.sync.controller;

import at.sync.util.JsonUtility;

import java.io.Reader;

/**
 * Created by florianmathis on 05/11/15.
 */
public class OSMSyncController implements ISyncController {

    public void startSync() {
        // Sync
        // 1. fetch json data from api
        // 2. json to sync.models
        // 3. fetch data from database
        // 4. map to sync.models

        // example: POI
        // 5. compare source (api data) to destination (db)
        //      a) if dest.extRef exist in source: source.ID = dest.ID (delete from dest list, update)
        //      b) else: save source object (insert)
        //      c) remaing objects can exist

        // example: Route
        // 6. route > same as 5.
        //      a) deactivate all schedules (set until to route-timestamp - 1day) //TODO: is it correct to decrease 1 day?
        //      b) create new schedule (set from to route-timestamp)


        JsonUtility jsonUtility = new JsonUtility();
        Reader osmReader = jsonUtility.fetchDataFromUrl("");

    }


}
