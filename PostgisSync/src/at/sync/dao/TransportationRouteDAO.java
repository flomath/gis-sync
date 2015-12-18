package at.sync.dao;

import at.sync.model.POI;
import at.sync.model.POIType;
import at.sync.model.TransportationRoute;
import at.sync.model.TransportationType;
import org.postgresql.geometric.PGpoint;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
    private static final String description = "desc";
    private static final String routeNo = "route_no";

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

            while (result.next()) {
                transportationRoutes.add(mapSetToObject(result, transportationTypes));
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

}
