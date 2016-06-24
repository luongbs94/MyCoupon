package com.ln.model;

/**
 * Created by Nhahv on 6/17/2016.
 * <></>
 */

public class DetailUser {

    private String id, name;
    private String picture;
    private String accessToken;

    public DetailUser(String id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public DetailUser(String id, String name, String picture, String accessToken) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.accessToken = accessToken;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
