package at.sync.model;
import java.util.UUID;

/**
 * POI - Point of Interest
 * Represents a Building, Station, Place,..
 */
public class POI extends Entity {
    private UUID id;
    private String name;
    private POIType poiType;
    private double radius;
    private String extRef;
    private double latitude;
    private double longitude;

    public POI() {
    }

    public POI(UUID id, String name, double radius, String ext_ref, POIType poiType) {
        this.id = id;
        this.name = name;
        this.radius = radius;
        this.extRef = ext_ref;
        this.poiType = poiType;
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getExtRef() {
        return extRef;
    }

    public void setExtRef(String extRef) {
        this.extRef = extRef;
    }

    public POIType getPoiType() {
        return poiType;
    }

    public void setPoiType(POIType poiType) {
        this.poiType = poiType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
