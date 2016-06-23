package com.ln.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Nhahv on 6/20/2016.
 * class save news user like
 */

public class LikeNews extends RealmObject {


    @Required
    private String idUser;

    @Required
    @PrimaryKey
    private String idNews;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdNews() {
        return idNews;
    }

    public void setIdNews(String idNews) {
        this.idNews = idNews;
    }
}
