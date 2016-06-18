package com.ln.model;

import java.io.Serializable;

/**
 * Created by Nhahv on 5/11/2016.
 *<></>
 */
public class ItemImage implements Serializable {

    private String images;
    private String idNews;

    public ItemImage() {
    }


    public ItemImage(String images, String idNews) {
        this.images = images;
        this.idNews = idNews;
    }

    public String getImages() {
        return images;
    }

    public String getIdNews() {
        return idNews;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
