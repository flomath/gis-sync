package at.sync.controller;

import at.sync.dao.*;
import at.sync.model.*;
import at.sync.util.SyncConfigReader;
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
 * OSM Sync Controller
 */
public class OSMSyncController implements ISyncController {

    /**
     * TODO: not tested yet!
     * not fully implemented!
     */
    public void startSync() throws Exception {
        this.DebugOut("Begin Synchronization");

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
        String query = "http://overpass-api.de/api/interpreter?data=%5Bout%3Axml%5D%3B%2F%2F%5Btimeout%3A25%5D%3B%0A%2F%2F%20fetch%20area%20%E2%80%9CG%C3%B6tzis%E2%80%9D%20to%20search%20in%0Aarea%283600074942%29-%3E.searchArea%3B%0A%2F%2F%20gather%20results%0A%28%0A%20%20%2F%2F%20query%20part%20for%3A%20%E2%80%9Croute%3Dbus%E2%80%9D%0A%20%20relation%5B%22route%22%3D%22bus%22%5D%28area.searchArea%29%3B%0A%29%3B%0A%2F%2F%20print%20results%0Aout%20body%3B%0A%3E%3B%0Aout%20meta%20qt%3B";

        InputStream input = null;
        try {
            this.DebugOut("Fetch OSM data");
            input = new URL(query).openStream();
            this.DebugOut("OSM data fetched");
        } catch (IOException e) {
            e.printStackTrace();
        }
        OsmIterator iterator = new OsmXmlIterator(input, false);

        // TODO: at this point lock tables in database?!

        // Load all POI-Types from db
        // Load all POI-Types from config file
        // loop through config poi types and check if id is in db -> if not remove from list/config file

        // XX93 - "Bushaltestelle"
        POITypeDAO poiTypeDAO = new POITypeDAO();
        HashMap<String, POIType> dbPoiTypes = new HashMap<>();
        for (POIType p : poiTypeDAO.getAllPOITypes()) {
            dbPoiTypes.put(p.getId().toString(), p);
        }

        // "bus_stop" - "XX93"
        HashMap<String, String> poiTypesFile = SyncConfigReader.getPOITypes();
        Iterator it = poiTypesFile.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if(!dbPoiTypes.containsKey(pair.getValue())) {
                it.remove();
            }
        }

        // Sync TransportationTypes with File-Mapping
        TransportationTypeDAO transportationTypeDAO = new TransportationTypeDAO();

        HashMap<String, TransportationType> dbTransportationTypes = new HashMap<>();
        for(TransportationType t : transportationTypeDAO.getAllTransportationTypes()) {
            dbTransportationTypes.put(t.getId().toString(), t);
        }

        HashMap<String, String> transportationTypesFile = SyncConfigReader.getTransportationTypes();
        Iterator itTransp = transportationTypesFile.entrySet().iterator();
        while (itTransp.hasNext()) {
            Map.Entry pair = (Map.Entry) itTransp.next();

            if(!dbTransportationTypes.containsKey(pair.getValue())) {
                itTransp.remove();
            }
        }


        // TODO only get id and extRef?
        // fetch all routes with schedules and their POIs from db
        // where each schedule validUntil = null and no departure/arrival time
        // create hash map with refId, Route
        this.DebugOut("Load existing TransportationRoutes from database");
        TransportationRouteDAO transportationRouteDAO = new TransportationRouteDAO();
        List<TransportationRoute> transportationRouteList = transportationRouteDAO.getAllTransportationRoutes();

        HashMap<String, TransportationRoute> dbRoutes = new HashMap<>();
        for (TransportationRoute i : transportationRouteList) {
            dbRoutes.put(i.getExtRef().toString(), i);
        }

        // TODO only get id and extRef?
        // fetch all POIs from db (needed, otherwise we cannot determine, if osm POI is already in db)
        // create hash map with refId, POI
        this.DebugOut("Load existing POIs from database");
        POIDAO poiDAO = new POIDAO();
        List<POI> poiList = poiDAO.getAllPOIs();

        HashMap<String, POI> dbPois = new HashMap<>();
        for (POI i : poiList) {
            if(i.getExtRef() != null)
                dbPois.put(i.getExtRef().toString(), i);
        }

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

                    // Set TransportationRoute-Type
                    String osmTransportationType = tags.get("route");
                    if (transportationTypesFile.containsKey(osmTransportationType)) {
                        String id = transportationTypesFile.get(osmTransportationType);
                        transportationRoute.setType(dbTransportationTypes.get(id));
                    } else {
                        TransportationType transportationType = new TransportationType();
                        transportationType.setId(UUID.randomUUID());
                        transportationType.setIsDirty(true);
                        transportationType.setName(osmTransportationType);
                        transportationTypesFile.put(transportationType.getName(), transportationType.getId().toString());
                        dbTransportationTypes.put(transportationType.getId().toString(), transportationType);
                        transportationRoute.setType(transportationType);
                    }

                    // loop through members (osm) -> POIs
                    // save all POIs in temporary list
                    ArrayList<Schedule> routeSchedules = new ArrayList<>();
                    int seqNo = 0;
                    for ( OsmRelationMember member : OsmModelUtil.membersAsList(relation) ) {
                        if ( member.getType() == EntityType.Node ) {
                            POI poi;
                            String nodeRef = String.valueOf(member.getId());
                            if ( poisHashMap.containsKey(nodeRef) ) {
                                poi = poisHashMap.get(nodeRef);
                            } else if ( dbPois.containsKey(nodeRef) ) {
                                poi = dbPois.get(nodeRef);
                                //poisHashMap.put(nodeRef, poi);
                            } else {
                                poi = new POI();
                                poi.setExtRef(nodeRef);
                                poisHashMap.put(nodeRef, poi);
                            }

                            Schedule schedule = new Schedule();
                            schedule.setSeqNo(seqNo);
                            schedule.setPoi(poi);

                            routeSchedules.add(schedule);

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
                        transportationRoute.getSchedules().sort((o1, o2) -> {
                            if ( o1.getTripNo() <= o2.getTripNo() ) {
                                return -1;
                            }
                            return 1;
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

                POI poi = null;

                // if poi already exists in db -> set id from db
                if(dbPois.containsKey(nodeRef)) {
                    poi = dbPois.get(nodeRef);
                } else if(poisHashMap.containsKey(nodeRef)) {
                    poi = poisHashMap.get(nodeRef);
                } else {
                    continue;
                }

                // set general data
                Map<String, String> tags = OsmModelUtil.getTagsAsMap(node);
                poi.setName(tags.get("name"));
                poi.setLatitude(node.getLatitude());
                poi.setLongitude(node.getLongitude());
                //poi.setRadius();

                // Get POIType basend on various decisions
                String osmPoiType = "";
                if(tags.containsKey("amenity") && tags.get("amenity").equals("bus_station")) {
                    osmPoiType = "bus_station";
                } else if(tags.containsKey("highway") && tags.get("highway").equals("bus_stop")) {
                    osmPoiType = "bus_stop";
                } else {
                    if(tags.containsKey("public_transport") && tags.get("public_transport").equals("stop_position")) {
                        if(tags.containsKey("train") && tags.get("train").equals("yes")) {
                            osmPoiType = "halt"; // train halt
                        } else if(tags.containsKey("railway")) {
                            osmPoiType = "halt"; // train halt
                        } else if(tags.containsKey("bus") && tags.get("bus").equals("yes")) {
                            osmPoiType = "bus_stop";
                        }
                    } else if(tags.containsKey("public_transport") && tags.get("public_transport").equals("platform")) {
                        if(tags.containsKey("railway") && tags.get("railway").equals("platform")) {
                            osmPoiType = "train_station";
                        }
                    }
                }

                if(osmPoiType == "") {
                    osmPoiType = "undef_stop";
                    this.DebugOut("Could not specify POI-Type for ext_ref = " + nodeRef);
                    // Couldn't find the proper POI-Type!!
                }

                // Set POIType
                if (poiTypesFile.containsKey(osmPoiType)) {
                    String id = poiTypesFile.get(osmPoiType);
                    poi.setPoiType(dbPoiTypes.get(id));
                } else {
                    POIType poiType = new POIType();
                    poiType.setId(UUID.randomUUID());
                    poiType.setIsDirty(true);
                    poiType.setName(osmPoiType);
                    poiTypesFile.put(poiType.getName(), poiType.getId().toString());
                    dbPoiTypes.put(poiType.getId().toString(), poiType);
                    poi.setPoiType(poiType);
                }
            }
        }


        // rest of db hashMap
        for ( TransportationRoute transportationRoute : dbRoutes.values() ) {
            // set inactive
            transportationRoute.setValidUntil(now);

            for ( Schedule schedule : transportationRoute.getSchedules() ) {
                schedule.setValidUntil(now);
            }

            // also update inactive transportation routes
            transportationRoutes.add(transportationRoute);
        }

        // Start Synchronization Step
        // --------------------------

        ConnectionManager conManager = null;
        try {
            conManager = ConnectionManager.getInstance();
            conManager.BeginTransaction();

            // Insert all PoiTypes with IsDirtyFlag
            List<POIType> poiTypesToAdd = new ArrayList<>();
            for(POIType p : dbPoiTypes.values()) {
                if(p.isDirty()) {
                    poiTypesToAdd.add(p);
                }
            }
            poiTypeDAO.insertPoiTypes(poiTypesToAdd);

            // Insert new POIs into database
            List<POI> insertPoiList = new ArrayList<>(poisHashMap.values());
            poiDAO.insertPOIs(insertPoiList);

            // Update existing POIs
            poiDAO.updatePOIs(insertPoiList);


            // Insert TransportationTypes with IsDirtyFlag
            List<TransportationType> transportationTypesToAdd = new ArrayList<>();
            for(TransportationType transportationType : dbTransportationTypes.values()) {
                if(transportationType.isDirty()) {
                    transportationTypesToAdd.add(transportationType);
                }
            }
            transportationTypeDAO.insertTransportationTypes(transportationTypesToAdd);

            // Add Transportation Routes
            transportationRouteDAO.insertTransportationRoutes(transportationRoutes);
            transportationRouteDAO.updateTransportationRoutes(transportationRoutes);


            // Write PoiType-Mapping to File
            SyncConfigReader.addPOITypes(poiTypesFile);

            // Write TransportationType-Mapping to File
            SyncConfigReader.addTransportationTypes(transportationTypesFile);

            // Commit
            conManager.getConnection().commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if(conManager != null) conManager.getConnection().rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            if(conManager != null) {
                conManager.EndTransaction();
            }
        }

        //trans open
        try {
            // setAutocommit=false

            // transportationRoutes -> insert/update (active)
            // schedules -> insert/update (active/inactive)
            // dbRoutes -> update (inactive)
            // schedules -> update (inactive)
            // poisHashMap -> insert
            // dbPois -> update
        } catch(Exception ex) {
            // rollback
        }

        //trans commit

        this.DebugOut("Synchronization ended successfully");
    }

    private long logStartTime = 0;

    /**
     * Print debug information
     * @param message
     */
    private void DebugOut(String message) {
        if(logStartTime == 0)
            logStartTime = System.currentTimeMillis();

        long currentLogTime = System.currentTimeMillis();
        System.out.println(new Date(currentLogTime) + " Info " + message + ", elapsed time: " + (currentLogTime - logStartTime) + "ms");
    }
}
