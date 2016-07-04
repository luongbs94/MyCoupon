package com.ln.model;

import java.io.Serializable;

/**
 * Created by Nhahv on 7/4/2016.
 * <></>
 */
public class ItemImage  implements Serializable{

    private String path;

    public ItemImage(String path) {
        this.path = path;
    }

    public ItemImage() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
