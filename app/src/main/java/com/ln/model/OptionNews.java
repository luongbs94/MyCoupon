package com.ln.model;

import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Nhahv on 8/18/2016.
 * <></>
 */

@Table(database = DatabaseManager.class)
public class OptionNews extends BaseModel {


    @PrimaryKey(autoincrement = true)
    private long id;
    @Index
    @Column
    private String idNews;

    @Column
    private String idUser;

    @Column
    private int type = MainApplication.NEW_LIKE;

    @Column
    private int typeShopOfCustomer = MainApplication.SHOP;


    public OptionNews() {
    }

    public OptionNews(String idNews, String idUser, int type, int typeShop) {
        this.idNews = idNews;
        this.idUser = idUser;
        this.type = type;
        this.typeShopOfCustomer = typeShop;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
