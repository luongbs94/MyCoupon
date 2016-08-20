package com.ln.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.ln.app.MainApplication;

/**
 * Created by Nhahv on 8/18/2016.
 * <></>
 */

@Table(name = "OptionNews")
public class OptionNews extends Model {

    @Column(name = "idNews", index = true)
    private String idNews;

    @Column(name = "idUser")
    private String idUser;

    @Column(name = "type")
    private int type = MainApplication.NEW_LIKE;

    @Column(name = "typeShopOfCustomer")
    private int typeShopOfCustomer = MainApplication.SHOP;


    public OptionNews() {
    }

    public OptionNews(String idNews, String idUser, int type, int typeShop) {
        this.idNews = idNews;
        this.idUser = idUser;
        this.type = type;
        this.typeShopOfCustomer = typeShop;
    }

    public String getIdNews() {
        return idNews;
    }

    public void setIdNews(String idNews) {
        this.idNews = idNews;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeShopOfCustomer() {
        return typeShopOfCustomer;
    }

    public void setTypeShopOfCustomer(int typeShopOfCustomer) {
        this.typeShopOfCustomer = typeShopOfCustomer;
    }
}
