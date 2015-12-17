package at.sync.model;

import java.util.UUID;

/**
 * OpenStreetMap types like bus_stop, station,..
 */
public class POIType {
    private UUID id;
    private String name;
    private boolean isPrivate;

    public POIType() {
    }

    public POIType(UUID id, String name, boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
