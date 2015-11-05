package at.sync.model;

import java.sql.Timestamp;

/**
 * Schedule
 */
public class Schedule {
    private String id;
    private int tripNo;
    private Timestamp validFrom;
    private Timestamp validUntil;
    private Timestamp explicitDate;
    private Timestamp arrivalTime;
    private Timestamp departureTime;
    private String seqNo;

    public Schedule() {
    }

    public Schedule(String id, int tripNo, Timestamp validFrom, Timestamp validUntil, Timestamp explicitDate, Timestamp arrivalTime, Timestamp departureTime, String seqNo) {
        this.id = id;
        this.tripNo = tripNo;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.explicitDate = explicitDate;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.seqNo = seqNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTripNo() {
        return tripNo;
    }

    public void setTripNo(int tripNo) {
        this.tripNo = tripNo;
    }

    public Timestamp getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Timestamp validFrom) {
        this.validFrom = validFrom;
    }

    public Timestamp getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Timestamp validUntil) {
        this.validUntil = validUntil;
    }

    public Timestamp getExplicitDate() {
        return explicitDate;
    }

    public void setExplicitDate(Timestamp explicitDate) {
        this.explicitDate = explicitDate;
    }

    public Timestamp getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Timestamp arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }
}
