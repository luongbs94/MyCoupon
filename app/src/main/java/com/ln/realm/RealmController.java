package com.ln.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.ln.app.MainApplication;
import com.ln.model.DetailUser;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Nhahv on 6/20/2016.
 * controller realm database
 */

public class RealmController {

    private static RealmController instance;
    private Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {
        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }

        return instance;
    }

    public static RealmController with(Application application) {
        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }


    // add likeNews by id news
    public void addLikeNewsByIdNews(String idNews) {

        DetailUser detailUser = MainApplication.sDetailUser;
        realm.beginTransaction();
        LikeNews likeNews = realm.createObject(LikeNews.class);
        likeNews.setIdUser(detailUser.getId());
        likeNews.setIdNews(idNews);
        realm.commitTransaction();
    }

    // add DeleteNews by id news
    public void addDeleteNewsByIdNews(String idNews) {

        DetailUser detailUser = MainApplication.sDetailUser;
        realm.beginTransaction();
        DeleteNews likeNews = realm.createObject(DeleteNews.class);
        likeNews.setIdUser(detailUser.getId());
        likeNews.setIdNews(idNews);
        realm.commitTransaction();
    }

    // delete like news by id
    public void deleteLikeNewsById(String idNews) {
        realm.beginTransaction();
        RealmResults<LikeNews> likeNewses = findAllLikeNewsById(idNews);

        if (!likeNewses.isEmpty()) {
            for (LikeNews likeNews : likeNewses) {
                likeNews.deleteFromRealm();
            }
        }

        realm.commitTransaction();
    }

    // delete Delete news by id
    public void deleteDeleteNewsById(String idNews) {
        realm.beginTransaction();
        RealmResults<DeleteNews> listDeleteNews = findAllDeleteIdById(idNews);

        if (!listDeleteNews.isEmpty()) {
            for (DeleteNews deleteNews : listDeleteNews) {
                deleteNews.deleteFromRealm();
            }
        }

        realm.commitTransaction();
    }

    // find all like news by id
    public RealmResults<LikeNews> findAllLikeNewsById(String idNews) {

        return realm.where(LikeNews.class)
                .equalTo(MainApplication.ID_NEWS, idNews).findAll();
    }

    // find all delete news by id
    public RealmResults<DeleteNews> findAllDeleteIdById(String idNews) {

        return realm.where(DeleteNews.class)
                .equalTo(MainApplication.ID_NEWS, idNews).findAll();
    }

    // delete all like news
    public void deleteAllLikeNews() {

        realm.beginTransaction();
        RealmResults<LikeNews> listLikeNewses = getListLikeNews();
        if (!listLikeNewses.isEmpty()) {
            for (LikeNews likeNews : listLikeNewses) {
                likeNews.deleteFromRealm();
            }
        }
        realm.commitTransaction();
    }

    // find all like news
    public RealmResults<LikeNews> getListLikeNews() {
        return realm.where(LikeNews.class).findAll();
    }

    // delete all like news
    public void deleteAllDeleteNews() {

        realm.beginTransaction();
        RealmResults<DeleteNews> listDeleteNews = getListDeleteNews();
        if (!listDeleteNews.isEmpty()) {
            for (DeleteNews likeNews : listDeleteNews) {
                likeNews.deleteFromRealm();
            }
        }
        realm.commitTransaction();
    }


    // find all delete news
    public RealmResults<DeleteNews> getListDeleteNews() {
        return realm.where(DeleteNews.class).findAll();
    }


    public LikeNews getLikeNewsById(String idNews) {
        return realm.where(LikeNews.class).equalTo(MainApplication.ID_NEWS, idNews).findFirst();
    }

    public void deleteAll() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

}
