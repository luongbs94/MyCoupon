package com.ln.model;

import java.io.Serializable;

/**
 * Created by Nhahv on 28/5/2016.
 */
public class LocalMediaStore implements Serializable {

    private String path;
    private long duration, lastUpdateAt;

    public LocalMediaStore() {
    }

    public LocalMediaStore(String path, long duration, long lastUpdateAt) {
        this.path = path;
        this.duration = duration;
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }
}
