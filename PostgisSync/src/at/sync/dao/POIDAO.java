package at.sync.dao;

import at.sync.model.POI;
import at.sync.model.POIType;
import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
     * Get all DB Columns
     *
     * @return String
     */
    private String getDbColumns() {
        return String.format("%s, %s, %s, %s, %s, %s", id, name, poiTypeID, radius, position, extRef);
    }

    /**
     * Insert POIs
     *
     * @param poiList
     * @throws Exception
     */
    public void insertPOIs(List<POI> poiList) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String query = String.format("INSERT INTO poi (%s) values (?, ?, ?, ?, point(?, ?), ?)", this.getDbColumns());
            PreparedStatement ps = conn.prepareStatement(query);

            for(POI poi : poiList) {
                if(poi.getId() == null) {
                    PGobject toInsertUUID = new PGobject();
                    toInsertUUID.setType("uuid");
                    if(poi.getId() == null) {
                        poi.setId(UUID.randomUUID());
                    }

                    toInsertUUID.setValue(String.valueOf(poi.getId()));

                    ps.setObject(1, toInsertUUID);
                    ps.setString(2, poi.getName());
                    ps.setObject(3, poi.getPoiType() != null ? poi.getPoiType().getId() : null);
                    ps.setDouble(4, poi.getRadius());
                    ps.setDouble(5, poi.getLatitude());
                    ps.setDouble(6, poi.getLongitude());
                    ps.setString(7, poi.getExtRef());

                    ps.addBatch();
                }
            }

            ps.executeBatch();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
        }
    }

    /**
     * Update POIs
     *
     * @param poiList
     * @throws Exception
     */
    public void updatePOIs(List<POI> poiList) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String query = "UPDATE poi SET name=?, poi_type_id=?, radius=?, position=point(?,?), ext_ref=? where id=?";
            PreparedStatement ps = conn.prepareStatement(query);

            for(POI poi : poiList) {
                if(poi.getId() == null)
                    continue;

                ps.setString(1, poi.getName());
                ps.setObject(2, poi.getPoiType() != null ? poi.getPoiType().getId() : null);
                ps.setDouble(3, poi.getRadius());
                ps.setDouble(4, poi.getLatitude());
                ps.setDouble(5, poi.getLongitude());
                ps.setString(6, poi.getExtRef());
                ps.setObject(7, poi.getId());

                ps.addBatch();
            }

            ps.executeBatch();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
        }
    }

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
     * @throws SQLException
     */
    private POI mapSetToObject(ResultSet resultSet, HashMap<String, POIType> poiTypes) throws SQLException {
        POI poi = new POI();

        try {
            poi.setId(UUID.fromString(resultSet.getString(id)));
            poi.setName(resultSet.getString(name));
            poi.setPoiType(poiTypes.get(resultSet.getString(poiTypeID)));
            poi.setRadius(resultSet.getDouble(radius));
            poi.setExtRef(resultSet.getString(extRef));
            PGpoint point = (PGpoint)resultSet.getObject(position);
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
