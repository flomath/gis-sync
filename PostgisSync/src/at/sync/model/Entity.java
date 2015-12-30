package at.sync.model;

/**
 * Created by florianmathis on 30/12/15.
 */
public abstract class Entity {
    private boolean isDirty;

    public boolean isDirty() {
        return isDirty;
    }

    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }
}
