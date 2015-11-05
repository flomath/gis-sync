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
        jsonUtility.fetchTransportationRoutes("http://overpass-api.de/api/interpreter?data=%5Bout%3Ajson%5D%3B%0A%2F%2F%20fetch%20area%20%E2%80%9CVorarlberg%E2%80%9D%20to%20search%20in%0Aarea%283600076327%29-%3E.searchArea%3B%0A%2F%2F%20gather%20results%0A%28%0A%20%20%2F%2F%20query%20part%20for%3A%20%E2%80%9Croute%3Dtrain%22%20and%20%22route%3Dbus%22%0A%20%20relation%5B%22route%22%3D%22train%22%5D%28area.searchArea%29%3B%0A%20%20relation%5B%22route%22%3D%22bus%22%5D%28area.searchArea%29%3B%0A%29%3B%0Aforeach%28%20%20%20%20%20%20%20%20%20%20%20%0A%20%20out%3B%20%20%20%20%20%20%20%20%20%20%20%20%20%2F%2F%20output%20relations%0A%29%3B%0A%3E%3E%3B%0Aout%20meta%20qt%3B");

    }


}
