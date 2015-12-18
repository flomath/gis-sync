package at.sync.dao;

import at.sync.model.POI;
import at.sync.model.POIType;
import org.postgresql.geometric.PGpoint;

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

            // TODO (?, ?) or point(?, ?)::geometry ?!
            String query = String.format("INSERT INTO poi (%s) values (?, ?, ?, ?, (?, ?), ?)", this.getDbColumns());
            PreparedStatement ps = conn.prepareStatement(query);

            for(POI p : poiList) {
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

    /**
     * Add object (POI) to prepared statement
     *
     * @param ps
     * @param poi
     * @throws SQLException
     */
    private void addObjectToStmt(PreparedStatement ps, POI poi) throws SQLException {
        try {
            ps.setString(1, String.valueOf(poi.getId() != null ? poi.getId() : "uuid_generate_v4()"));
            ps.setString(2, poi.getName());
            ps.setString(3, poi.getPoiType() != null ? String.valueOf(poi.getPoiType().getId()) : null);
            ps.setDouble(4, poi.getRadius());
            ps.setDouble(5, poi.getLatitude());
            ps.setDouble(6, poi.getLongitude());
            ps.setString(7, poi.getExtRef());

            ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
