package at.sync.dao;

import at.sync.model.TransportationType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by florianmathis on 17/12/15.
 */
public class TransportationTypeDAO {

    private static final String id = "id";
    private static final String name = "name";
    private static final String maxSpeed = "max_speed";
    private static final String avgSpeed = "average_speed";
    private static final String color = "color";

    /**
     * Get all TransportationTypes
     *
     * @return List
     */
    public List<TransportationType> getAllTransportationTypes() {
        ArrayList<TransportationType> transportationTypeList = new ArrayList<>();
        ResultSet result = null;

        try {
            Connection conn = ConnectionManager.getInstance().getConnection();

            String query = "Select * from transportation";
            result = conn.createStatement().executeQuery(query);

            while(result.next()){
                transportationTypeList.add(mapSetToObject(result));
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

        return transportationTypeList;
    }

    /**
     * Map a result set to an object (TransportationType)
     *
     * @param resultSet
     * @return TransportationType
     */
    private TransportationType mapSetToObject(ResultSet resultSet) throws SQLException {
        TransportationType transportationType = new TransportationType();

        try {
            transportationType.setId(UUID.fromString(resultSet.getString(id)));
            transportationType.setName(resultSet.getString(name));
            transportationType.setMaxSpeed(resultSet.getDouble(maxSpeed));
            transportationType.setAvgSpeed(resultSet.getDouble(avgSpeed));
            transportationType.setColor(resultSet.getString(color));
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return transportationType;
    }
}
