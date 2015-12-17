package at.sync.dao;

import at.sync.model.POIType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
     * Get all POITypess
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
                POIType poiType = new POIType();

                poiType.setId(UUID.fromString(result.getString(id)));
                poiType.setName(result.getString(name));
                poiType.setIsPrivate(result.getBoolean(isPrivate));

                poiTypeList.add(poiType);
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
}
