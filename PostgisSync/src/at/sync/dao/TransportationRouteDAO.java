package at.sync.dao;

import at.sync.model.POI;
import at.sync.model.POIType;
import at.sync.model.TransportationRoute;
import at.sync.model.TransportationType;
import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Author: nschoch
 * Date: 17.12.15
 * Time: 09:27
 */
public class TransportationRouteDAO {

    private static final String table = "transportation_route";

    private static final String id = "id";
    private static final String name = "name";
    private static final String validFrom = "valid_from";
    private static final String validUntil = "valid_until";
    private static final String transportationID = "transportation_id";
    private static final String operator = "operator";
    private static final String network = "network";
    private static final String extRef = "ext_ref";
    private static final String descriptionFrom = "desc_from";
    private static final String descriptionTo = "desc_to";
    private static final String description = "desce";
    private static final String routeNo = "route_no";

    /**
     * Get all DB Columns
     *
     * @return String
     */
    private String getDbColumns() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,\"%s\",%s,%s,%s", id, name, validFrom, validUntil, transportationID, operator, network, extRef, description, descriptionFrom, descriptionTo, routeNo);
    }

    /**
     * Insert Transportation route
     *
     * @param transportationRouteList
     * @throws Exception
     */
    public void insertTransportationRoutes(List<TransportationRoute> transportationRouteList) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String query = String.format("INSERT INTO %s (%s) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", table, getDbColumns());
            PreparedStatement ps = conn.prepareStatement(query);

            for(TransportationRoute transportationRoute : transportationRouteList) {
                if(transportationRoute.getId() == null) {
                    PGobject toInsertUUID = new PGobject();
                    toInsertUUID.setType("uuid");
                    if(transportationRoute.getId() == null) {
                        transportationRoute.setId(UUID.randomUUID());
                    }

                    toInsertUUID.setValue(String.valueOf(transportationRoute.getId()));

                    ps.setObject(1, toInsertUUID);
                    ps.setString(2, transportationRoute.getName());
                    ps.setTimestamp(3, transportationRoute.getValidFrom());
                    ps.setTimestamp(4, transportationRoute.getValidUntil());
                    ps.setObject(5, transportationRoute.getType() != null ? transportationRoute.getType().getId() : null);
                    ps.setString(6, transportationRoute.getOperator());
                    ps.setString(7, transportationRoute.getNetwork());
                    ps.setString(8, transportationRoute.getExtRef());
                    ps.setString(9, transportationRoute.getDescription());
                    ps.setString(10, transportationRoute.getDescriptionFrom());
                    ps.setString(11, transportationRoute.getDescriptionTo());
                    ps.setString(12, transportationRoute.getRouteNo());

                    ps.addBatch();
                }
            }

            ps.executeBatch();
            ps.close();

            ScheduleDAO scheduleDAO = new ScheduleDAO();
            scheduleDAO.insertOrUpdateSchedules(transportationRouteList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
        }
    }

    /**
     * Update Transportation route
     *
     * @param transportationRouteList
     * @throws Exception
     */
    public void updateTransportationRoutes(List<TransportationRoute> transportationRouteList) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String query = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? where %s=?",
                    table, name, validFrom, validUntil,
                    transportationID, operator, network, extRef,
                    description, descriptionFrom, descriptionTo, routeNo, id;

            PreparedStatement ps = conn.prepareStatement(query);

            for(TransportationRoute transportationRoute : transportationRouteList) {
                if(transportationRoute.getId() == null)
                    continue;

                PGobject toInsertUUID = new PGobject();
                toInsertUUID.setType("uuid");
                if(transportationRoute.getId() == null) {
                    transportationRoute.setId(UUID.randomUUID());
                }

                toInsertUUID.setValue(String.valueOf(transportationRoute.getId()));

                ps.setString(1, transportationRoute.getName());
                ps.setTimestamp(2, transportationRoute.getValidFrom());
                ps.setTimestamp(3, transportationRoute.getValidUntil());
                ps.setObject(4, transportationRoute.getType() != null ? transportationRoute.getType().getId() : null);
                ps.setString(5, transportationRoute.getOperator());
                ps.setString(6, transportationRoute.getNetwork());
                ps.setString(7, transportationRoute.getExtRef());
                ps.setString(8, transportationRoute.getDescription());
                ps.setString(9, transportationRoute.getDescriptionFrom());
                ps.setString(10, transportationRoute.getDescriptionTo());
                ps.setString(11, transportationRoute.getRouteNo());
                ps.setObject(12, toInsertUUID);

                ps.addBatch();
            }

            ps.executeBatch();
            ps.close();

            ScheduleDAO scheduleDAO = new ScheduleDAO();
            scheduleDAO.insertOrUpdateSchedules(transportationRouteList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
        }
    }



    /**
     * Get all TransportationRoutes
     *
     * @return List
     */
    public List<TransportationRoute> getAllTransportationRoutes() {

        // Get all POI-Types
        HashMap<String, TransportationType> transportationTypes = new HashMap<>();
        TransportationTypeDAO typesDAO = new TransportationTypeDAO();
        List<TransportationType> transportationTypeList = typesDAO.getAllTransportationTypes();
        for (TransportationType i : transportationTypeList) transportationTypes.put(i.getId().toString(), i);

        ArrayList<TransportationRoute> transportationRoutes = new ArrayList<>();
        ResultSet result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();

            String query = "Select * from transportation_route";
            result = conn.createStatement().executeQuery(query);

            ScheduleDAO scheduleDAO = new ScheduleDAO();
            while (result.next()) {
                transportationRoutes.add(mapSetToObject(result, transportationTypes));
            }
            for (TransportationRoute transportationRoute : transportationRoutes) {
                transportationRoute.setSchedules(scheduleDAO.getValidSchedules(transportationRoute));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


        return transportationRoutes;
    }

    /**
     * Map a result set to an object (TransportationRoute)
     *
     * @param resultSet
     * @param transportationTypes
     * @return TransportationRoute
     */
    private TransportationRoute mapSetToObject(ResultSet resultSet, HashMap<String, TransportationType> transportationTypes) throws SQLException {
        TransportationRoute transportationRoute = new TransportationRoute();

        try {
            transportationRoute.setId(UUID.fromString(resultSet.getString(id)));
            transportationRoute.setName(resultSet.getString(name));
            transportationRoute.setValidFrom(resultSet.getTimestamp(validFrom));
            transportationRoute.setValidUntil(resultSet.getTimestamp(validUntil));
            transportationRoute.setType(transportationTypes.get(resultSet.getString(transportationID)));
            transportationRoute.setOperator(resultSet.getString(operator));
            transportationRoute.setNetwork(resultSet.getString(network));
            transportationRoute.setExtRef(resultSet.getString(extRef));
            transportationRoute.setDescriptionFrom(resultSet.getString(descriptionFrom));
            transportationRoute.setDescriptionTo(resultSet.getString(descriptionTo));
            transportationRoute.setDescription(resultSet.getString(description));
            transportationRoute.setRouteNo(resultSet.getString(routeNo));
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return transportationRoute;
    }

    /**
     * Add object (TransportationRoute) to prepared statement
     *
     * @param ps
     * @param transportationRoute
     * @throws SQLException
     */
    private void addObjectToStmt(PreparedStatement ps, TransportationRoute transportationRoute) throws SQLException {
        try {
            ps.setString(1, String.valueOf(transportationRoute.getId() != null ? transportationRoute.getId() : "uuid_generate_v4()"));
            ps.setString(2, transportationRoute.getName());
            ps.setTimestamp(3, transportationRoute.getValidFrom());
            ps.setTimestamp(4, transportationRoute.getValidUntil());
            ps.setString(5, transportationRoute.getType() != null ? String.valueOf(transportationRoute.getType().getId()) : null);
            ps.setString(6, transportationRoute.getOperator());
            ps.setString(7, transportationRoute.getNetwork());
            ps.setString(8, transportationRoute.getExtRef());
            ps.setString(9, transportationRoute.getDescriptionFrom());
            ps.setString(10, transportationRoute.getDescriptionTo());
            ps.setString(11, transportationRoute.getDescription());
            ps.setString(12, transportationRoute.getRouteNo());

            ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
