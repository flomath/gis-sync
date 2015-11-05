package at.sync.model;

/**
 * POI - Point of Interest
 * Represents a Building, Station, Place,..
 */
public class POI
{
    private String id;
    private String name;
    private POIType poiType;
    private double radius;
    private String extRef;

    public POI() {
    }

    public POI(String id, String name, double radius, String ext_ref, POIType poiType)
    {
        this.id = id;
        this.name = name;
        this.radius = radius;
        this.extRef = ext_ref;
        this.poiType = poiType;
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
}
