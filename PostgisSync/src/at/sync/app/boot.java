package at.sync.app;

import at.sync.controller.OSMSyncController;

/**
 * PostgisSync application
 */
public class boot {
    public static void main(String [] args)
    {

        OSMSyncController syncController = new OSMSyncController();
        syncController.startSync();

    }
}
