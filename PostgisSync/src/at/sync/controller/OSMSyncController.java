package at.sync.controller;

import at.sync.model.POI;
import at.sync.model.POIType;
import at.sync.model.TransportationRoute;
import at.sync.model.TransportationType;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        // Overpass-API query
        String query = "http://overpass-api.de/api/interpreter?data=%5Bout%3Axml%5D%5Btimeout%3A25%5D%3B%0A%2F%2F%20fetch%20area%20%E2%80%9CG%C3%B6tzis%E2%80%9D%20to%20search%20in%0Aarea%283600075231%29-%3E.searchArea%3B%0A%2F%2F%20gather%20results%0A%28%0A%20%20%2F%2F%20query%20part%20for%3A%20%E2%80%9Croute%3Dbus%E2%80%9D%0A%20%20node%5B%22route%22%3D%22bus%22%5D%28area.searchArea%29%3B%0A%20%20way%5B%22route%22%3D%22bus%22%5D%28area.searchArea%29%3B%0A%20%20relation%5B%22route%22%3D%22bus%22%5D%28area.searchArea%29%3B%0A%29%3B%0A%2F%2F%20print%20results%0Aout%20body%3B%0A%3E%3B%0Aout%20meta%20qt%3B";

        InputStream input = null;
        try {
            input = new URL(query).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OsmIterator iterator = new OsmXmlIterator(input, false);

        int numRoutes = 0;
        int numPois = 0;
        List<TransportationRoute> transportationRoutes = new ArrayList<>();

        for (EntityContainer container : iterator) {

            // fetch all relations
            if (container.getType() == EntityType.Relation) {
                OsmRelation relation = (OsmRelation) container.getEntity();
                Map<String, String> tags = OsmModelUtil.getTagsAsMap(relation);

                String route = tags.get("route");
                if (route != null) {

                    if (route.equals("bus")) {
                        numRoutes++;
                    }

                    // Map TransportationRoutes
                    TransportationRoute transportationRoute = new TransportationRoute();
                    transportationRoute.setDescription(tags.get("description"));
                    transportationRoute.setDescriptionFrom(tags.get("from"));
                    transportationRoute.setDescriptionTo(tags.get("to"));
                    transportationRoute.setExtRef(String.valueOf(relation.getId()));
                    transportationRoute.setNetwork(tags.get("network"));
                    transportationRoute.setOperator(tags.get("operator"));
                    transportationRoute.setRouteNo(tags.get("ref"));
                    //transportationRoute.setType();

                    transportationRoutes.add(transportationRoute);
                }
            }

            // fetch all nodes
            if(container.getType() == EntityType.Node) {
                OsmNode node = (OsmNode) container.getEntity();
                Map<String, String> tags = OsmModelUtil.getTagsAsMap(node);

                String busstop = tags.get("bus_stop");
                if(busstop != null) {
                    POI poi = new POI();
                    poi.setName(tags.get("name"));
                    poi.setExtRef(String.valueOf(node.getId()));
                    //poi.setPoiType();
                    //poi.setRadius();

                    // poi.setLat/long
                    numPois++;
                }
            }
        }

        System.out.println("Number of routes: " + numRoutes);
        System.out.println("Number of busstop: " + numRoutes);
    }


}
