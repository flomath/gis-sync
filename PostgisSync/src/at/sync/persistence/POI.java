package at.sync.persistence;

import java.util.UUID;

/**
 * Author: nschoch
 * Date: 03.12.15
 * Time: 09:54
 */
public class POI {
    private UUID id;
    private String extRef;

    public POI(UUID id, String extRef) {
        this.id = id;
        this.extRef = extRef;
    }

    public UUID getId() {
        return id;
    }

    public String getExtRef() {
        return extRef;
    }
}
