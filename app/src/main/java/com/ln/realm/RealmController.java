package com.ln.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.ln.api.SaveData;
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

    /*================= START LIKE NEWS USER ================*/

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
        RealmResults<LikeNews> likeNewses = getListLikeNewsByIdNews(idNews);

        if (!likeNewses.isEmpty()) {
            for (LikeNews likeNews : likeNewses) {
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


    // find all like news by id
    public RealmResults<LikeNews> getListLikeNewsByIdNews(String idNews) {

        return realm.where(LikeNews.class)
                .equalTo(MainApplication.ID_NEWS, idNews).findAll();
    }

    // delete Delete news by id
    public void deleteDeleteNewsById(String idNews) {
        realm.beginTransaction();
        RealmResults<DeleteNews> listDeleteNews = getDeleteNewsById(idNews);

        if (!listDeleteNews.isEmpty()) {
            for (DeleteNews deleteNews : listDeleteNews) {
                deleteNews.deleteFromRealm();
            }
        }

        realm.commitTransaction();
    }

    // find all delete news by id
    public RealmResults<DeleteNews> getDeleteNewsById(String idNews) {

        return realm.where(DeleteNews.class)
                .equalTo(MainApplication.ID_NEWS, idNews).findAll();
    }

    // delete all like news
    public void deleteAllDeleteNewsUser() {

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

    /*================== END DELETE NEWS USER ==================*/


    /*============ START SHOP LIKE NEWS ===============*/
    // add shop like
    public void addShopLikeNewsByIdNews(String idNews) {

        realm.beginTransaction();
        LikeNews likeNews = realm.createObject(LikeNews.class);
        likeNews.setIdUser(SaveData.company.getCompany_id() + "");
        likeNews.setIdNews(idNews);
        realm.commitTransaction();
    }

    // delete shop like

    public void deleteShopLikeNewsByIdNews(String idNews) {

        realm.beginTransaction();
        RealmResults<ShopLikeNews> listShopLike = getShopLikeById(idNews);
        if (!listShopLike.isEmpty()) {
            for (ShopLikeNews shopLikeNews : listShopLike) {
                shopLikeNews.deleteFromRealm();
            }
        }
        realm.commitTransaction();

    }

    public void deleteAllShopLikeNews() {
        realm.beginTransaction();

        RealmResults<ShopLikeNews> likeNewses = getListShopLiekNews();
        if (!likeNewses.isEmpty()) {
            for (ShopLikeNews shopLikeNews : likeNewses) {
                shopLikeNews.deleteFromRealm();
            }
        }
        realm.commitTransaction();
    }

    private RealmResults<ShopLikeNews> getShopLikeById(String idNews) {
        return realm.where(ShopLikeNews.class).equalTo(MainApplication.ID_NEWS, idNews).findAll();
    }

    private RealmResults<ShopLikeNews> getListShopLiekNews() {
        return realm.where(ShopLikeNews.class).findAll();
    }

    /*================== END SHOP LIKE NEWS ====================*/


    public void deleteAll() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

}
