package com.ln.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.CouponTemplate;
import com.ln.model.DetailUser;
import com.ln.model.Message;
import com.ln.model.NewsOfCompany;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Nhahv on 6/20/2016.
 * <></>
 */

public class RealmController {

    private static RealmController instance;
    private Realm mRealm;

    public RealmController(Application application) {
        mRealm = Realm.getDefaultInstance();
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

    public Realm getmRealm() {
        return mRealm;
    }

    /*================= START LIKE NEWS USER ================*/

    // add likeNews by id news
    public void addLikeNewsByIdNews(String idNews) {

        DetailUser detailUser = MainApplication.sDetailUser;
        mRealm.beginTransaction();
        LikeNews likeNews = mRealm.createObject(LikeNews.class);
        likeNews.setIdUser(detailUser.getId());
        likeNews.setIdNews(idNews);
        mRealm.commitTransaction();
    }


    // add DeleteNews by id news
    public void addDeleteNewsByIdNews(String idNews) {

        DetailUser detailUser = MainApplication.sDetailUser;
        mRealm.beginTransaction();
        DeleteNews likeNews = mRealm.createObject(DeleteNews.class);
        likeNews.setIdUser(detailUser.getId());
        likeNews.setIdNews(idNews);
        mRealm.commitTransaction();
    }


    // delete like news by id
    public void deleteLikeNewsById(String idNews) {
        mRealm.beginTransaction();
        RealmResults<LikeNews> likeNewses = getListLikeNewsByIdNews(idNews);

        if (!likeNewses.isEmpty()) {
            for (LikeNews likeNews : likeNewses) {
                likeNews.deleteFromRealm();
            }
        }

        mRealm.commitTransaction();
    }

    // find all like news
    public RealmResults<LikeNews> getListLikeNews() {
        return mRealm.where(LikeNews.class).findAll();
    }

    // delete all like news
    public void deleteAllLikeNews() {

        mRealm.beginTransaction();
        RealmResults<LikeNews> listLikeNewses = getListLikeNews();
        if (!listLikeNewses.isEmpty()) {
            for (LikeNews likeNews : listLikeNewses) {
                likeNews.deleteFromRealm();
            }
        }
        mRealm.commitTransaction();
    }


    // find all like news by id
    public RealmResults<LikeNews> getListLikeNewsByIdNews(String idNews) {

        return mRealm.where(LikeNews.class)
                .equalTo(MainApplication.ID_NEWS, idNews).findAll();
    }

    // delete Delete news by id
    public void deleteDeleteNewsById(String idNews) {
        mRealm.beginTransaction();
        RealmResults<DeleteNews> listDeleteNews = getDeleteNewsById(idNews);

        if (!listDeleteNews.isEmpty()) {
            for (DeleteNews deleteNews : listDeleteNews) {
                deleteNews.deleteFromRealm();
            }
        }

        mRealm.commitTransaction();
    }

    // find all delete news by id
    public RealmResults<DeleteNews> getDeleteNewsById(String idNews) {

        return mRealm.where(DeleteNews.class)
                .equalTo(MainApplication.ID_NEWS, idNews).findAll();
    }

    // delete all like news
    public void deleteAllDeleteNewsUser() {

        mRealm.beginTransaction();
        RealmResults<DeleteNews> listDeleteNews = getListDeleteNews();
        if (!listDeleteNews.isEmpty()) {
            for (DeleteNews likeNews : listDeleteNews) {
                likeNews.deleteFromRealm();
            }
        }
        mRealm.commitTransaction();
    }


    // find all delete news
    public RealmResults<DeleteNews> getListDeleteNews() {
        return mRealm.where(DeleteNews.class).findAll();
    }

    /*================== END DELETE NEWS USER ==================*/


    /*============ START SHOP LIKE NEWS ===============*/
    // add shop like
    public void addShopLikeNewsByIdNews(String idNews) {

        mRealm.beginTransaction();
        ShopLikeNews likeNews = mRealm.createObject(ShopLikeNews.class);
        likeNews.setIdCompany(SaveData.company.getCompany_id());
        likeNews.setIdNews(idNews);
        mRealm.commitTransaction();
    }

    // delete shop like

    public void deleteShopLikeNewsByIdNews(String idNews) {

        mRealm.beginTransaction();
        RealmResults<ShopLikeNews> listShopLike = getShopLikeById(idNews);
        if (!listShopLike.isEmpty()) {
            for (ShopLikeNews shopLikeNews : listShopLike) {
                shopLikeNews.deleteFromRealm();
            }
        }
        mRealm.commitTransaction();

    }

    public void deleteAllShopLikeNews() {
        mRealm.beginTransaction();

        RealmResults<ShopLikeNews> likeNewses = getListShopLikeNews();
        if (!likeNewses.isEmpty()) {
            for (ShopLikeNews shopLikeNews : likeNewses) {
                shopLikeNews.deleteFromRealm();
            }
        }
        mRealm.commitTransaction();
    }

    private RealmResults<ShopLikeNews> getShopLikeById(String idNews) {
        return mRealm.where(ShopLikeNews.class).equalTo(MainApplication.ID_NEWS, idNews).findAll();
    }

    public RealmResults<ShopLikeNews> getListShopLikeNews() {
        return mRealm.where(ShopLikeNews.class).findAll();
    }

    /*================== END SHOP LIKE NEWS ====================*/


    public void deleteAll() {
        mRealm.beginTransaction();
        mRealm.deleteAll();
        mRealm.commitTransaction();
    }


    /* ====================== START SAVE NEWS  SHOP =================*/

    public void addListNewsOfCompany(List<NewsOfCompany> listMessage) {

        mRealm.beginTransaction();
        for (NewsOfCompany message : listMessage) {
            NewsOfCompany newsOfCompany = mRealm.createObject(NewsOfCompany.class);
            newsOfCompany.setNews(message.getMessage_id(),
                    message.getContent(), message.getCreated_date(),
                    message.getCompany_id(), message.getLast_date(),
                    message.getTitle(), message.getLink(), message.getImages_link());
        }
        mRealm.commitTransaction();
    }

    public void deleteAllNewsOfCompany() {
        mRealm.beginTransaction();
        RealmResults<NewsOfCompany> listMessages = getListNewsOfCompany();
        for (NewsOfCompany message : listMessages) {
            message.deleteFromRealm();
        }

        mRealm.commitTransaction();
    }

    public RealmResults<NewsOfCompany> getListNewsOfCompany() {
        return mRealm.where(NewsOfCompany.class).findAll();
    }

       /* ====================== START SAVE NEWS  SHOP =================*/


    /* ====================== START SAVE NEWS  CUSTOMER =================*/
    public void addListNewsOfCustomer(List<Message> listMessage) {

        mRealm.beginTransaction();
        for (Message message : listMessage) {
            Message newOfCustomer = mRealm.createObject(Message.class);
            newOfCustomer.setNews(message.getMessage_id(),
                    message.getContent(), message.getCreated_date(),
                    message.getCompany_id(), message.getLast_date(),
                    message.getTitle(), message.getLink(), message.getImages_link(),
                    message.getLogo(), message.getLogo_link(), message.getName());
        }
        mRealm.commitTransaction();

    }

    //
    public void deleteAllNewsOfCustomer() {
        mRealm.beginTransaction();
        RealmResults<Message> listMessages = getListNewsOfCustomer();
        for (Message message : listMessages) {
            message.deleteFromRealm();
        }

        mRealm.commitTransaction();
    }

    public RealmResults<Message> getListNewsOfCustomer() {
        return mRealm.where(Message.class).findAll();
    }

       /* ====================== START SAVE NEWS  CUSTOMER =================*/


    /*  ================= START COUPON TEMPLATE OF COMPANY ========*/
    public void addListCouponTemplate(List<CouponTemplate> listCouponTemplate) {
        mRealm.beginTransaction();

        for (CouponTemplate template : listCouponTemplate) {
            CouponTemplate coupon = mRealm.createObject(CouponTemplate.class);
            coupon.setCouponTemplate(template.getCoupon_template_id(), template.getContent(),
                    template.getDuration(), template.getCreated_date(),
                    template.getCompany_id(), template.getValue());
        }
        mRealm.commitTransaction();
    }

    public void deleteCouponTemplate() {
        mRealm.beginTransaction();
        List<CouponTemplate> couponTemplateList = getListCouponTemplate();
        for (CouponTemplate coupon : couponTemplateList) {
            coupon.deleteFromRealm();
        }
        mRealm.commitTransaction();
    }

    public RealmResults<CouponTemplate> getListCouponTemplate() {
        return mRealm.where(CouponTemplate.class).findAll();
    }

    /* =============== END OFFLINE COUPON TEMPLATE OF COMPANY ======*/

    /* ================ START SAVE LIST COMPANY OF CUSTOMER ===========*/

    public void addListCompanyCustomer(List<CompanyOfCustomer> listCompany) {

        mRealm.beginTransaction();
        for (CompanyOfCustomer companyOfCustomer : listCompany) {
            CompanyOfCustomer company = companyOfCustomer;
            mRealm.copyToRealmOrUpdate(company);
        }
        mRealm.commitTransaction();
    }

    public void deleteListCompanyCustomer() {

        mRealm.beginTransaction();
        List<CompanyOfCustomer> mListCompany = getListCompanyCustomer();
        for (CompanyOfCustomer company : mListCompany) {
            company.deleteFromRealm();
        }

        mRealm.commitTransaction();
    }

    public RealmResults<CompanyOfCustomer> getListCompanyCustomer() {
        return mRealm.where(CompanyOfCustomer.class).findAll();
    }
}
