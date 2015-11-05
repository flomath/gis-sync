package at.sync.model;

/**
 * OpenStreetMap types like bus_stop, station,..
 */
public class POIType
{
    private String id;
    private String name;
    private boolean isPrivate;

    public POIType() {
    }

    public POIType(String id, String name, boolean isPrivate)
    {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
