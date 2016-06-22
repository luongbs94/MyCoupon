package com.ln.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Nhahv on 6/22/2016.
 * <></>
 */
public class ShopLikeNews extends RealmObject {

    @PrimaryKey
    private String idNews;
    @Required
    private String idCompany;

    public ShopLikeNews() {
    }

    public ShopLikeNews(String idCompany, String idNews) {
        this.idNews = idNews;
        this.idCompany = idCompany;
    }

    public String getIdNews() {
        return idNews;
    }

    public void setIdNews(String idNews) {
        this.idNews = idNews;
    }

    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        this.idCompany = idCompany;
    }
}
