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
    private static final String averageSpeed = "average_speed";
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

            String query = "Select * from transportation_type";
            result = conn.createStatement().executeQuery(query);

            while(result.next()){
                TransportationType transportationType = new TransportationType();

                transportationType.setId(UUID.fromString(result.getString(id)));
                transportationType.setName(result.getString(name));
                transportationType.setMaxSpeed(result.getDouble(maxSpeed));
                transportationType.setAvgSpeed(result.getDouble(averageSpeed));
                transportationType.setColor(result.getString(color));

                transportationTypeList.add(transportationType);
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
}
