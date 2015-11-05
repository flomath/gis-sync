package at.sync.model;

import java.sql.Timestamp;

/**
 * Public transportation route
 */
public class TransportationRoute {
    private String id;
    private String name;
    private Timestamp validFrom;
    private Timestamp validUntil;
    private TransportationType type;
    private String operator;
    private String network;
    private String extRef;
    private String descriptionFrom;
    private String descriptionTo;
    private String description;
    private String routeNo;

    public TransportationRoute() {
    }

    public TransportationRoute(String id, String name, Timestamp validFrom, Timestamp validUntil, TransportationType type, String operator, String network, String extRef, String descriptionFrom, String descriptionTo, String description, String routeNo) {
        this.id = id;
        this.name = name;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.type = type;
        this.operator = operator;
        this.network = network;
        this.extRef = extRef;
        this.descriptionFrom = descriptionFrom;
        this.descriptionTo = descriptionTo;
        this.description = description;
        this.routeNo = routeNo;
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

    public TransportationType getType() {
        return type;
    }

    public void setType(TransportationType type) {
        this.type = type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getExtRef() {
        return extRef;
    }

    public void setExtRef(String extRef) {
        this.extRef = extRef;
    }

    public String getDescriptionFrom() {
        return descriptionFrom;
    }

    public void setDescriptionFrom(String descriptionFrom) {
        this.descriptionFrom = descriptionFrom;
    }

    public String getDescriptionTo() {
        return descriptionTo;
    }

    public void setDescriptionTo(String descriptionTo) {
        this.descriptionTo = descriptionTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }
}
