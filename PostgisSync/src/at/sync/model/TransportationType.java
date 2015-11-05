package at.sync.model;

/**
 * TransportationType of each Waypoint (Walking, by Bus, by Train)
 */
public class TransportationType {
    private String id;
    private String name;
    private double maxSpeed;

    public TransportationType() {
    }

    public TransportationType(String id, String name, double maxSpeed) {
        this.id = id;
        this.name = name;
        this.maxSpeed = maxSpeed;
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

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
