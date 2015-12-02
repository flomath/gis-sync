package at.sync.controller;

import at.sync.model.POI;
import at.sync.model.Schedule;
import at.sync.model.TransportationRoute;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.*;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by florianmathis on 05/11/15.
 */
public class OSMSyncController implements ISyncController {

    /**
     * TODO: not tested yet!
     * not fully implemented!
     */
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


        // TODO check if it is current timestamp
        Timestamp now = new Timestamp(new Date().getTime());

        // "global" hashMap for all POIs
        // needed to access each POI later on for setting attributes
        HashMap<String, POI> poisHashMap = new HashMap<>();


        // fetch all routes with POIs from osm
        // Overpass-API query
        String query = "http://overpass-api.de/api/interpreter?data=%5Bout%3Axml%5D%5Btimeout%3A25%5D%3B%0A%2F%2F%20fetch%20area%20%E2%80%9CG%C3%B6tzis%E2%80%9D%20to%20search%20in%0Aarea%283600075231%29-%3E.searchArea%3B%0A%2F%2F%20gather%20results%0A%28%0A%20%20%2F%2F%20query%20part%20for%3A%20%E2%80%9Croute%3Dbus%E2%80%9D%0A%20%20node%5B%22route%22%3D%22bus%22%5D%28area.searchArea%29%3B%0A%20%20way%5B%22route%22%3D%22bus%22%5D%28area.searchArea%29%3B%0A%20%20relation%5B%22route%22%3D%22bus%22%5D%28area.searchArea%29%3B%0A%29%3B%0A%2F%2F%20print%20results%0Aout%20body%3B%0A%3E%3B%0Aout%20meta%20qt%3B";

        InputStream input = null;
        try {
            input = new URL(query).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OsmIterator iterator = new OsmXmlIterator(input, false);


        // TODO
        // fetch all routes with schedules and their POIs from db
        // where each schedule validUntil = null
        // create hash map with refId, Route
        HashMap<String, TransportationRoute> dbRoutes = new HashMap<>();


        // TODO
        // fetch all POIs from db (needed, otherwise we cannot determine, if osm POI is already in db)
        // create hash map with refId, POI
        HashMap<String, POI> dbPois = new HashMap<>();


        // loop through data(osm)
        ArrayList<TransportationRoute> transportationRoutes = new ArrayList<>();
        for ( EntityContainer container : iterator ) {
            // Relations (osm) -> Routes
            if ( container.getType() == EntityType.Relation ) {
                OsmRelation relation = (OsmRelation) container.getEntity();
                Map<String, String> tags = OsmModelUtil.getTagsAsMap(relation);

                String route = tags.get("route");
                String routeRef = String.valueOf(relation.getId());
                if ( route != null ) {
                    TransportationRoute transportationRoute;
                    // if route already exists in db -> set id from db, delete route from hashMap(db)
                    if ( dbRoutes.containsKey(routeRef) ) {
                        transportationRoute = dbRoutes.get(routeRef);
                        dbRoutes.remove(routeRef);
                    } else {
                        transportationRoute = new TransportationRoute();
                        transportationRoute.setExtRef(routeRef);

                        // set current timestamp as valid from
                        transportationRoute.setValidFrom(now);
                    }

                    // set general data
                    transportationRoute.setName(tags.get("name"));
                    transportationRoute.setDescription(tags.get("description"));
                    transportationRoute.setDescriptionFrom(tags.get("from"));
                    transportationRoute.setDescriptionTo(tags.get("to"));
                    transportationRoute.setNetwork(tags.get("network"));
                    transportationRoute.setOperator(tags.get("operator"));
                    transportationRoute.setRouteNo(tags.get("ref"));
                    // TODO set route type
                    // transportationRoute.setType();

                    // loop through members (osm) -> POIs
                    // save all POIs in temporary list
                    ArrayList<Schedule> routeSchedules = new ArrayList<>();
                    int seqNo = 0;
                    for ( OsmRelationMember member : OsmModelUtil.membersAsList(relation) ) {
                        if ( member.getType() == EntityType.Node ) {
                            POI poi;
                            String nodeRef = String.valueOf(member.getId());
                            if ( dbPois.containsKey(nodeRef) ) {
                                poi = dbPois.get(nodeRef);
                            } else {
                                poi = new POI();
                                poi.setExtRef(nodeRef);
                            }

                            Schedule schedule = new Schedule();
                            schedule.setSeqNo(seqNo);
                            schedule.setPoi(poi);

                            routeSchedules.add(schedule);
                            poisHashMap.put(nodeRef, poi);

                            seqNo++;
                        }
                    }
                    // if route already exists in db -> check if difference between schedules(db) - nodes (osm)
                    boolean schedulesDiffer = false;
                    int transportationRouteSchedulesSize = transportationRoute.getSchedules().size();
                    int routeSchedulesSize = routeSchedules.size();

                    if ( transportationRouteSchedulesSize != routeSchedulesSize ) {
                        schedulesDiffer = true;
                    }
                    if ( !schedulesDiffer ) {
                        for ( int i = 0; i < routeSchedulesSize; i++ ) {
                            String routePoiRef = String.valueOf(routeSchedules.get(i).getPoi().getExtRef());
                            String transportationPoiRef = transportationRoute.getSchedules().get(i).getPoi().getExtRef();
                            if ( !routePoiRef.equals(transportationPoiRef) ) {
                                // sth different
                                schedulesDiffer = true;
                                break;
                            }
                        }
                    }
                    // schedule is different
                    // loop through all schedules and set inactive
                    // all new schedules get a new tripNo
                    if ( schedulesDiffer ) {
                        for ( Schedule schedule : transportationRoute.getSchedules() ) {
                            schedule.setValidUntil(now);
                        }

                        // get max tripNo from schedules
                        transportationRoute.getSchedules().sort(new Comparator<Schedule>() {
                            @Override
                            public int compare(Schedule o1, Schedule o2) {
                                if ( o1.getTripNo() <= o2.getTripNo() ) {
                                    return -1;
                                }

                                return 1;
                            }
                        });
                        int maxTripNo = 1;
                        if ( transportationRoute.getSchedules().size() > 0 ) {
                            maxTripNo = transportationRoute.getSchedules().get(0).getTripNo() + 1;
                        }

                        // set new tripNo to routeSchedules
                        for ( Schedule schedule : routeSchedules ) {
                            schedule.setTripNo(maxTripNo);
                            schedule.setValidFrom(now);
                        }

                        transportationRoute.getSchedules().addAll(routeSchedules);
                    }

                    // add to "global" routes list
                    transportationRoutes.add(transportationRoute);
                }
            }

            // Ways (osm) (not needed yet)

            // Nodes (osm) -> POIs
            if ( container.getType() == EntityType.Node ) {
                OsmNode node = (OsmNode) container.getEntity();
                String nodeRef = String.valueOf(node.getId());

                // if poi already exists in db -> set id from db
                POI poi;
                if ( poisHashMap.containsKey(nodeRef) ) {
                    poi = poisHashMap.get(nodeRef);
                } else {
                    // poi does not exist in db, create new one
                    poi = new POI();
                    poi.setExtRef(nodeRef);
                }

                // set general data
                Map<String, String> tags = OsmModelUtil.getTagsAsMap(node);
                poi.setName(tags.get("name"));
                poi.setLatitude(node.getLatitude());
                poi.setLongitude(node.getLongitude());
                //TODO set poi type
                //poi.setPoiType();
                //poi.setRadius();
            }
        }


        // rest of db hashMap
        for ( TransportationRoute transportationRoute : dbRoutes.values() ) {
            // set inactive
            transportationRoute.setValidUntil(now);
        }

    }

}
