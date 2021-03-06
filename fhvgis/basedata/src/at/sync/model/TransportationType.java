package at.sync.model;

import java.util.UUID;

/**
 * TransportationType of each Waypoint (Walking, by Bus, by Train)
 */
public class TransportationType extends Entity {
    private UUID id;
    private String name;
    private double maxSpeed;
    private double avgSpeed;
    private String color;

    public TransportationType() {
    }

    public TransportationType(UUID id, String name, double maxSpeed) {
        this.id = id;
        this.name = name;
        this.maxSpeed = maxSpeed;
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

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
