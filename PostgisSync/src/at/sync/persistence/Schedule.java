package at.sync.persistence;

import java.util.UUID;

/**
 * Author: nschoch
 * Date: 03.12.15
 * Time: 09:56
 */
public class Schedule {
    private UUID id;
    private UUID poi;

    public Schedule(UUID id, UUID poi) {
        this.id = id;
        this.poi = poi;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPoi() {
        return poi;
    }
}
