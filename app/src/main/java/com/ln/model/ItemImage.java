package com.ln.model;

import java.io.Serializable;

/**
 * Created by Nhahv on 5/11/2016.
 */
public class ItemImage implements Serializable {

    private String images;
    private int idNews;

    public ItemImage(){}

    public ItemImage(String images){
        this.images = images;
    }

    public ItemImage(String images, int idCompany) {
        this.images = images;
        this.idNews = idCompany;
    }

    public String getImages() {
        return images;
    }


    public int getIdCompany() {
        return idNews;
    }
}
