package at.sync.dao;

import at.sync.model.POI;
import at.sync.model.POIType;
import at.sync.model.TransportationRoute;
import at.sync.model.TransportationType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                TransportationRoute transportationRoute = new TransportationRoute();

                transportationRoute.setId(UUID.fromString(result.getString(id)));
                transportationRoute.setName(result.getString(name));
                transportationRoute.setExtRef(result.getString(extRef));

                transportationRoute.setType(transportationTypes.get(result.getString(transportationID)));

                transportationRoutes.add(transportationRoute);
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

}
