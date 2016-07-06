package com.ln.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.Company;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.Coupon;
import com.ln.model.CouponTemplate;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCustomer;

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

    /*================= START LIKE NEWS USER ================*/

    // add likeNews by id news
    public void addLikeNewsByIdNews(String idNews) {

        AccountOflUser accountOflUser = MainApplication.sDetailUser;
        mRealm.beginTransaction();
        LikeNews likeNews = mRealm.createObject(LikeNews.class);
        likeNews.setIdUser(accountOflUser.getId());
        likeNews.setIdNews(idNews);
        mRealm.commitTransaction();
    }


    // add DeleteNews by id news
    public void addDeleteNewsByIdNews(String idNews) {

        AccountOflUser accountOflUser = MainApplication.sDetailUser;
        mRealm.beginTransaction();
        DeleteNews likeNews = mRealm.createObject(DeleteNews.class);
        likeNews.setIdUser(accountOflUser.getId());
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
        likeNews.setIdCompany(getAccountShop().getCompany_id());
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


    /* ====================== START SAVE NEWS  SHOP =================*/

    public void addListNewsOfCompany(List<NewsOfCompany> listMessage) {

        mRealm.beginTransaction();
        for (NewsOfCompany message : listMessage) {
            NewsOfCompany newsOfCompany = mRealm.createObject(NewsOfCompany.class);
            newsOfCompany.setNews(message.getMessage_id(),
                    message.getContent(),
                    message.getCompany_id(),
                    message.getTitle(), message.getLink(), message.getImages_link());
        }
        mRealm.commitTransaction();
    }

    public void deleteListNewsOfCompany() {
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
    public void addListNewsOfCustomer(List<NewsOfCustomer> listNews) {

        mRealm.beginTransaction();
        for (NewsOfCustomer message : listNews) {
            NewsOfCustomer newOfCustomer = mRealm.createObject(NewsOfCustomer.class);
            newOfCustomer.setNewsOfCustomer(message.getMessage_id(),
                    message.getContent(),
                    message.getCompany_id(),
                    message.getTitle(), message.getLink(), message.getImages_link(),
                    message.getLogo(), message.getLogo_link(), message.getName());
        }
        mRealm.commitTransaction();
    }

    //
    public void deleteAllNewsOfCustomer() {
        mRealm.beginTransaction();
        RealmResults<NewsOfCustomer> listMessages = getListNewsOfCustomer();
        for (NewsOfCustomer message : listMessages) {
            message.deleteFromRealm();
        }

        mRealm.commitTransaction();
    }

    public RealmResults<NewsOfCustomer> getListNewsOfCustomer() {
        return mRealm.where(NewsOfCustomer.class).findAll();
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
        if (couponTemplateList != null) {
            for (CouponTemplate coupon : couponTemplateList) {
                coupon.deleteFromRealm();
            }
        }
        mRealm.commitTransaction();
    }

    public void deleteCouponTemplateById(String id) {
        mRealm.beginTransaction();
        CouponTemplate couponTemplate = getCouponTemplateById(id);
        if (couponTemplate != null) {
            couponTemplate.deleteFromRealm();
        }
        mRealm.commitTransaction();
    }

    public RealmResults<CouponTemplate> getListCouponTemplate() {
        return mRealm.where(CouponTemplate.class).findAll();
    }

    private CouponTemplate getCouponTemplateById(String id) {
        return mRealm.where(CouponTemplate.class).equalTo("coupon_template_id", id).findFirst();
    }


    /* =============== END OFFLINE COUPON TEMPLATE OF COMPANY ======*/

    /* ================ START SAVE LIST COMPANY OF CUSTOMER ===========*/

    public void addListCompanyCustomer(List<CompanyOfCustomer> listCompany) {

        mRealm.beginTransaction();
        for (CompanyOfCustomer companyOfCustomer : listCompany) {
            CompanyOfCustomer company = mRealm.createObject(CompanyOfCustomer.class);

            for (Coupon coupon : companyOfCustomer.getCoupon()) {
                Coupon coupon1 = mRealm.createObject(Coupon.class);
                coupon1.setCoupon(coupon.getCoupon_id(), coupon.getUser_id(),
                        coupon.getCoupon_template_id(), coupon.getUsed_date(),
                        coupon.getCompany_id(), coupon.getValue(), coupon.getDuration(), coupon.getCode(),
                        coupon.getUser_name(), coupon.getUser_social(), coupon.getUser_image_link());

                company.getCoupon().add(coupon1);
            }

            company.setCompanyCustomer(companyOfCustomer.getCompany_id(),
                    companyOfCustomer.getLogo(),
                    companyOfCustomer.getLogo_link(),
                    companyOfCustomer.getName(),
                    companyOfCustomer.getAddress());
        }
        mRealm.commitTransaction();
    }

    public void deleteListCompanyCustomer() {

        mRealm.beginTransaction();
        List<CompanyOfCustomer> mListCompany = getListCompanyCustomer();
        for (CompanyOfCustomer company : mListCompany) {
            company.deleteFromRealm();
        }

        List<Coupon> listCoupon = getListCoupon();
        for (Coupon coupon : listCoupon) {
            coupon.deleteFromRealm();
        }
        mRealm.commitTransaction();
    }

    public RealmResults<CompanyOfCustomer> getListCompanyCustomer() {
        return mRealm.where(CompanyOfCustomer.class).findAll();
    }

    public RealmResults<Coupon> getListCoupon() {
        return mRealm.where(Coupon.class).findAll();
    }


    public CompanyOfCustomer getCompanyOfCustomer(String idCompany) {
        return mRealm.where(CompanyOfCustomer.class)
                .equalTo(MainApplication.ID_COMPANY, idCompany).findFirst();
    }

    /* =================== END SAVE COMPANY OF CUSTOMER =============*/

    /* =============== START ACCOUNT LOGIN SHOP =================*/

    public void saveAccountShop(Company company) {

        mRealm.beginTransaction();
        Company company1 = getAccountShop();
        if (company1 == null) {
            Company company2 = mRealm.createObject(Company.class);
            company2.setCompany(company.getCompany_id(), company.getName(), company.getAddress(),
                    company.getLogo(), company.getCreated_date(), company.getUser_id(), company.getUser1(),
                    company.getPass1(), company.getUser1_admin(), company.getUser2(),
                    company.getPass2(), company.getUser2_admin(), company.getIp(),
                    company.getLogo_link(), company.getCity(), company.getCountry_name());
        } else {
            company1.setCompany(company.getCompany_id(), company.getName(), company.getAddress(),
                    company.getLogo(), company.getCreated_date(), company.getUser_id(), company.getUser1(),
                    company.getPass1(), company.getUser1_admin(), company.getUser2(),
                    company.getPass2(), company.getUser2_admin(), company.getIp(),
                    company.getLogo_link(), company.getCity(), company.getCountry_name());
        }

        mRealm.commitTransaction();
    }

    public Company getAccountShop() {
        return mRealm.where(Company.class).findFirst();
    }

    /* =============== END SAVE ACCOUNT SHOP    ==================*/


     /* =============== START ACCOUNT LOGIN CUSTOMER =================*/

    public void saveAccountCustomer(AccountOflUser account) {

        mRealm.beginTransaction();
        AccountOflUser accountOflUser = getAccountCustomer();
        if (accountOflUser == null) {
            AccountOflUser accountOflUser1 = mRealm.createObject(AccountOflUser.class);
            accountOflUser1.setAccountOfUser(account.getId(), account.getName(),
                    account.getAccessToken(), account.getAccessToken());
        } else {
            accountOflUser.setAccountOfUser(account.getId(), account.getName(),
                    account.getAccessToken(), account.getAccessToken());
        }

        mRealm.commitTransaction();
    }

    public AccountOflUser getAccountCustomer() {
        return mRealm.where(AccountOflUser.class).findFirst();
    }

    /* =============== END SAVE ACCOUNT CUSTOMER    ==================*/


}
