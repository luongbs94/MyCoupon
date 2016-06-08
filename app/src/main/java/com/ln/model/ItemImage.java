package com.ln.model;

import java.io.Serializable;

/**
 * Created by Nhahv on 5/11/2016.
 */
public class ItemImage implements Serializable {

    private String images;

    public ItemImage(){}

    public ItemImage(String images){
        this.images = images;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
