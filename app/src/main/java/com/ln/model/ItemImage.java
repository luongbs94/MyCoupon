package com.ln.model;

import java.io.Serializable;

/**
 * Created by Nhahv on 5/11/2016.
 * <></>
 */
public class ItemImage implements Serializable {

    private String path;

    public ItemImage() {
    }

    public ItemImage(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }
}
