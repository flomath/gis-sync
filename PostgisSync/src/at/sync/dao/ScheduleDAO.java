package at.sync.dao;

import at.sync.model.POI;
import at.sync.model.POIType;
import at.sync.model.Schedule;
import at.sync.model.TransportationRoute;
import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class ScheduleDAO {

    private static final String id = "id";
    private static final String tripNo = "trip_nr";
    private static final String validFrom = "valid_from";
    private static final String validUntil = "valid_until";
    private static final String explicitDate = "explicit_date";
    private static final String arrivalTime = "arrival_time";
    private static final String departureTime = "departure_time";
    private static final String transportationRouteID = "transportation_route_id";
    private static final String scheduleDayID = "shedule_day_id";
    private static final String poiID = "poi_id";
    private static final String seqNo = "seq_no";

    /**
     * Get all DB Columns
     *
     * @return String
     */
    private String getDbColumns() {
        return String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s", id, tripNo, validFrom, validUntil, explicitDate, arrivalTime, departureTime, transportationRouteID, scheduleDayID, poiID, seqNo);
    }

    /**
     * Insert Schedules
     *
     * @param transportationRoutes
     * @throws Exception
     */
    public void insertOrUpdateSchedules(List<TransportationRoute> transportationRoutes) throws Exception {
        for(TransportationRoute transportationRoute : transportationRoutes) {
            insertSchedules(transportationRoute);
            updateSchedules(transportationRoute);
        }
    }

    /**
     * Insert Schedules
     *
     * @param transportationRoute
     * @throws Exception
     */
    public void insertSchedules(TransportationRoute transportationRoute) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String query = String.format("INSERT INTO shedule (%s) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", this.getDbColumns());
            PreparedStatement ps = conn.prepareStatement(query);

            List<Schedule> schedules = transportationRoute.getSchedules();
            for(Schedule schedule : schedules) {
                if(schedule.getId() == null) {
                    PGobject toInsertUUID = new PGobject();
                    toInsertUUID.setType("uuid");
                    schedule.setId(UUID.randomUUID());

                    toInsertUUID.setValue(String.valueOf(schedule.getId()));

                    ps.setObject(1, toInsertUUID);
                    ps.setInt(2, schedule.getTripNo());
                    ps.setTimestamp(3, schedule.getValidFrom());
                    ps.setTimestamp(4, schedule.getValidUntil());
                    ps.setTimestamp(5, schedule.getExplicitDate());
                    ps.setTime(6, schedule.getArrivalTime());
                    ps.setTime(7, schedule.getDepartureTime());
                    ps.setObject(8, transportationRoute.getId());
                    ps.setObject(9, null);//TODO
                    ps.setObject(10, schedule.getPoi().getId());
                    ps.setInt(11, schedule.getSeqNo());

                    ps.addBatch();
                }
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
     * Update Schedules
     *
     * @param transportationRoute
     * @throws Exception
     */
    public void updateSchedules(TransportationRoute transportationRoute) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String query = "UPDATE shedule SET trip_no=?, valid_from=?, valid_until=?, explicit_date=?, arrival_time=?, departure_time=?, transportation_route_id=?, shedule_day_id=?, poi_id=?, seq_no=?, ext_ref=? where id=?";
            PreparedStatement ps = conn.prepareStatement(query);

            List<Schedule> schedules = transportationRoute.getSchedules();
            for(Schedule schedule : schedules) {
                if(schedule.getId() == null)
                    continue;

                ps.setInt(1, schedule.getTripNo());
                ps.setTimestamp(2, schedule.getValidFrom());
                ps.setTimestamp(3, schedule.getValidUntil());
                ps.setTimestamp(4, schedule.getExplicitDate());
                ps.setTime(5, schedule.getArrivalTime());
                ps.setTime(6, schedule.getDepartureTime());
                ps.setObject(7, transportationRoute.getId());
                ps.setObject(8, null);//TODO
                ps.setObject(9, schedule.getPoi().getId());
                ps.setInt(10, schedule.getSeqNo());
                ps.setObject(11, schedule.getId());

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
     * Get all valid Schedules
     *
     * @return List
     */
    public List<Schedule> getValidSchedules(TransportationRoute transportationRoute) {
        List<Schedule> schedules = new ArrayList<>();
        ResultSet result = null;
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String query = "Select * from shedule where transportation_route_id=? and valid_until is null";
            result = conn.createStatement().executeQuery(query);

            while(result.next()){
                schedules.add(mapSetToObject(result));
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


        return schedules;
    }

    /**
     * Map a result set to an object (Schedule)
     *
     * @param resultSet
     * @return Schedule
     * @throws SQLException
     */
    private Schedule mapSetToObject(ResultSet resultSet) throws SQLException {
        Schedule schedule = new Schedule();

        try {
            schedule.setId(UUID.fromString(resultSet.getString(id)));
            schedule.setTripNo(resultSet.getInt(tripNo));
            schedule.setValidFrom(resultSet.getTimestamp(validFrom));
            schedule.setValidUntil(resultSet.getTimestamp(validUntil));
            schedule.setExplicitDate(resultSet.getTimestamp(explicitDate));
            schedule.setArrivalTime(resultSet.getTime(arrivalTime));
            schedule.setDepartureTime(resultSet.getTime(departureTime));
            //TODO!
            //schedule.setPoi(UUID.fromString(resultSet.getString(poiID)));
            schedule.setSeqNo(resultSet.getInt(seqNo));
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return schedule;
    }

}
