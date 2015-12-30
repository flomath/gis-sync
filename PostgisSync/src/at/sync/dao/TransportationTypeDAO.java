package at.sync.dao;

import at.sync.model.POIType;
import at.sync.model.TransportationType;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    private static final String avgSpeed = "avarage_speed";
    private static final String color = "color";

    /**
     * Get all DB Columns
     *
     * @return String
     */
    private String getDbColumns() {
        return String.format("%s, %s, %s, %s, %s", id, name, maxSpeed, avgSpeed, color);
    }

    /**
     * Insert TransportationTypes
     *
     * @param transportationTypeList
     * @throws Exception
     */
    public void insertTransportationTypes(List<TransportationType> transportationTypeList) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String query = String.format("INSERT INTO transportation (%s) values (?, ?, ?, ?, ?)", this.getDbColumns());
            PreparedStatement ps = conn.prepareStatement(query);

            for(TransportationType transportationType : transportationTypeList) {
                addObjectToStmt(ps, transportationType);
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

    /**
     * Add object (TransportationType) to prepared statement
     *
     * @param ps
     * @param transportationType
     * @throws SQLException
     */
    private void addObjectToStmt(PreparedStatement ps, TransportationType transportationType) throws SQLException {
        try {
            PGobject toInsertUUID = new PGobject();
            toInsertUUID.setType("uuid");
            if(transportationType.getId() == null) {
                transportationType.setId(UUID.randomUUID());
            }

            toInsertUUID.setValue(String.valueOf(transportationType.getId()));

            ps.setObject(1, toInsertUUID);
            ps.setString(2, transportationType.getName());
            ps.setDouble(3, transportationType.getMaxSpeed());
            ps.setDouble(4, transportationType.getAvgSpeed());
            ps.setString(5, transportationType.getColor());

            ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
