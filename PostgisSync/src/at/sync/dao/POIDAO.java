package at.sync.dao;

import at.sync.model.POI;
import at.sync.model.POIType;
import org.postgresql.geometric.PGpoint;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Author: nschoch
 * Date: 17.12.15
 * Time: 09:27
 */
public class POIDAO {

    private static final String id = "id";
    private static final String name = "name";
    private static final String poiTypeID = "poi_type_id";
    private static final String radius = "radius";
    private static final String position = "position";
    private static final String extRef = "ext_ref";

    /**
     * Get all POIs
     *
     * @return List
     */
    public List<POI> getAllPOIs() {

        // Get all POI-Types
        HashMap<String, POIType> poiTypes = new HashMap<>();
        POITypeDAO typesDAO = new POITypeDAO();
        List<POIType> poiTypeList = typesDAO.getAllPOITypes();
        for (POIType i : poiTypeList) poiTypes.put(i.getId().toString(),i);

        ArrayList<POI> pois = new ArrayList<>();
        ResultSet result = null;
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String query = "Select * from poi";
            result = conn.createStatement().executeQuery(query);

            while(result.next()){
                pois.add(mapSetToObject(result, poiTypes));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return pois;
    }

    /**
     * Map a result set to an object (POI)
     *
     * @param resultSet
     * @param poiTypes
     * @return POI
     */
    private POI mapSetToObject(ResultSet resultSet, HashMap<String, POIType> poiTypes) throws SQLException {
        POI poi = new POI();

        try {
            poi.setId(UUID.fromString(resultSet.getString(id)));
            poi.setName(resultSet.getString(name));
            poi.setPoiType(poiTypes.get(resultSet.getString(poiTypeID)));
            poi.setRadius(resultSet.getDouble(radius));
            poi.setExtRef(resultSet.getString(extRef));
            //TODO: check if this is working
            PGpoint point = resultSet.getObject(position, PGpoint.class);
            if (point != null) {
                poi.setLatitude(point.x);
                poi.setLongitude(point.y);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return poi;
    }

}
