package com.ln.model;

/**
 * Created by Nhahv on 6/17/2016.
 * <></>
 */

public class DetailUser {

    private String id, name;

    public DetailUser(String id) {
        this.id = id;
    }

    public DetailUser(String id, String name) {
        this.id = id;
        this.name = name;
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
}
