package at.sync.app;

import at.sync.controller.OSMSyncController;
import at.sync.dao.POIDAO;
import at.sync.model.POI;

import java.util.List;

/**
 * PostgisSync application
 */
public class boot {
    public static void main(String[] args) {

        // TODO: database (schedule.shedule_day_id nullable) (schedule.arrival_time nullable)

        OSMSyncController syncController = new OSMSyncController();
        try {
            syncController.startSync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // first step: get all types > show mapping view (combobox)

        return;
    }
}
