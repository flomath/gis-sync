package at.sync.dao;

import at.sync.model.POI;
import at.sync.model.POIType;
import org.postgresql.geometric.PGpoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by florianmathis on 17/12/15.
 */
public class POITypeDAO {


    private static final String id = "id";
    private static final String name = "name";
    private static final String isPrivate = "private";

    /**
     * Get all POITypes
     *
     * @return List
     */
    public List<POIType> getAllPOITypes() {
        ArrayList<POIType> poiTypeList = new ArrayList<>();
        ResultSet result = null;

        try {
            Connection conn = ConnectionManager.getInstance().getConnection();

            String query = "Select * from poi_type";
            result = conn.createStatement().executeQuery(query);

            while(result.next()){
                poiTypeList.add(mapSetToObject(result));
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

        return poiTypeList;
    }

    /**
     * Map a result set to an object (POIType)
     *
     * @param resultSet
     * @return POIType
     */
    private POIType mapSetToObject(ResultSet resultSet) throws SQLException {
        POIType poiType = new POIType();

        try {
            poiType.setId(UUID.fromString(resultSet.getString(id)));
            poiType.setName(resultSet.getString(name));
            poiType.setIsPrivate(resultSet.getBoolean(isPrivate));
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return poiType;
    }

    /**
     * Add object (POIType) to prepared statement
     *
     * @param ps
     * @param poiType
     * @throws SQLException
     */
    private void addObjectToStmt(PreparedStatement ps, POIType poiType) throws SQLException {
        try {
            ps.setString(1, String.valueOf(poiType.getId() != null ? poiType.getId() : "uuid_generate_v4()"));
            ps.setString(2, poiType.getName());
            ps.setBoolean(3, poiType.isPrivate());

            ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
