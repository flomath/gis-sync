package at.sync.dao;

import at.sync.model.POI;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: nschoch
 * Date: 17.12.15
 * Time: 09:27
 */
public class POIDAO {

    /**
     * Get all POIs
     *
     * @return List
     */
    public List<POI> getAllPOIs() {
        ArrayList<POI> pois = new ArrayList<>();

        try {
            Connection conn = ConnectionManager.getInstance().getConnection();

            String query = "Select * from poi";
            ResultSet result = conn.createStatement().executeQuery(query);

            while(result.next()){
                POI poi = new POI();

                poi.setId(result.getObject("id", UUID.class));
                poi.setExtRef(result.getString("extRef"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pois;
    }

}
