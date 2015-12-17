package at.sync.persistence;

import java.util.List;
import java.util.UUID;

/**
 * Author: nschoch
 * Date: 03.12.15
 * Time: 09:58
 */
public class TransportationRoute {
    private UUID id;
    private String extRef;
    private List<UUID> schedules;

    public TransportationRoute(UUID id, String extRef) {
        this.id = id;
        this.extRef = extRef;
    }

    public UUID getId() {
        return id;
    }

    public String getExtRef() {
        return extRef;
    }

    public List<UUID> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<UUID> schedules) {
        this.schedules = schedules;
    }
}
