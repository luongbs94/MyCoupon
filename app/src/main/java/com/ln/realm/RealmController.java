package com.ln.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.ln.app.MainApplication;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.Coupon;
import com.ln.model.CouponTemplate;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCustomer;
import com.ln.model.NewsOfLike;
import com.ln.model.NewsOfMore;

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

    public Realm getRealm() {
        return mRealm;
    }
    /*================= START LIKE NEWS USER ================*/

    // add likeNews by id news
    public void addLikeNewsCustomer(String idNews, int type, String idUser) {

        mRealm.beginTransaction();
        LikeNews likeNews = mRealm.createObject(LikeNews.class);
        likeNews.setIdUser(idUser);
        likeNews.setIdNews(idNews);
        likeNews.setType(type);
        mRealm.commitTransaction();
    }


    // add DeleteNews by id news
    public void addDeleteNewsByIdNews(String idNews, String idUser) {

        mRealm.beginTransaction();
        DeleteNews likeNews = mRealm.createObject(DeleteNews.class);
        likeNews.setIdUser(idUser);
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


    // find all like news by id
    public RealmResults<LikeNews> getListLikeNewsByIdNews(String idNews) {
        return mRealm.where(LikeNews.class)
                .equalTo(MainApplication.ID_NEWS, idNews)
                .findAll();
    }


    // find all delete news by id
    public RealmResults<DeleteNews> getDeleteNewsById(String idNews) {

        return mRealm.where(DeleteNews.class)
                .equalTo(MainApplication.ID_NEWS, idNews).findAll();
    }


    // find all delete news
    public RealmResults<DeleteNews> getListDeleteNews() {
        return mRealm.where(DeleteNews.class).findAll();
    }

    /*================== END DELETE NEWS USER ==================*/


    /*============ START SHOP LIKE NEWS ===============*/
    // add shop like
    public void addShopLikeNewsByIdNews(String idNews, String isUser) {

        mRealm.beginTransaction();
        ShopLikeNews likeNews = mRealm.createObject(ShopLikeNews.class);
        likeNews.setIdCompany(isUser);
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


    private RealmResults<ShopLikeNews> getShopLikeById(String idNews) {
        return mRealm.where(ShopLikeNews.class)
                .equalTo(MainApplication.ID_NEWS, idNews)
                .findAll();
    }

    public RealmResults<ShopLikeNews> getListShopLikeNews() {
        return mRealm.where(ShopLikeNews.class).findAll();
    }

    /*================== END SHOP LIKE NEWS ====================*/


    /* ====================== START SAVE NEWS  SHOP =================*/

    public void addNewsOfCompany(NewsOfCompany news) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(news);
        mRealm.commitTransaction();
    }

    public void deleteNewsOfCompany(String idNews) {
        mRealm.beginTransaction();
        NewsOfCompany newsOfCompany = getNewsOfCompanyById(idNews);
        if (newsOfCompany != null) {
            newsOfCompany.deleteFromRealm();
        }
        mRealm.commitTransaction();
    }


    public void addListNewsOfCompany(List<NewsOfCompany> listMessage) {

        mRealm.beginTransaction();
        for (NewsOfCompany NewsOfCompany : listMessage) {
            mRealm.copyToRealmOrUpdate(NewsOfCompany);
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

    public NewsOfCompany getNewsOfCompanyById(String idNews) {
        return mRealm.where(NewsOfCompany.class)
                .equalTo("message_id", idNews)
                .findFirst();
    }

    /* ====================== START SAVE NEWS  SHOP =================*/


    /* ====================== START SAVE NEWS  CUSTOMER =================*/
    public void addListNewsOfCustomer(List<NewsOfCustomer> listNews) {

        mRealm.beginTransaction();
        for (NewsOfCustomer news : listNews) {
//            NewsOfCustomer newOfCustomer = mRealm.createObject(NewsOfCustomer.class);
//            newOfCustomer.setNewsOfCustomer(message.getMessage_id(),
//                    news.getContent(),
//                    news.getCompany_id(),
//                    news.getTitle(), news.getLink(), news.getImages_link(),
//                    news.getLogo(), news.getLogo_link(), news.getName());

            mRealm.copyToRealmOrUpdate(news);
        }
        mRealm.commitTransaction();
    }

    //
    public void deleteListNewsOfCustomer() {
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
//            CouponTemplate coupon = mRealm.createObject(CouponTemplate.class);
//            coupon.setCouponTemplate(template.getCoupon_template_id(), template.getContent(),
//                    template.getDuration(), template.getCreated_date(),
//                    template.getCompany_id(), template.getValue());
            mRealm.copyToRealmOrUpdate(template);
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

    public void addCompanyOfCustomer(CompanyOfCustomer company) {

        mRealm.beginTransaction();
        List<Coupon> listCoupon = getListCouponByCompanyId(company.getCompany_id());
        for (Coupon coupon : listCoupon) {
            coupon.deleteFromRealm();
        }

        CompanyOfCustomer companyOfCustomer = getCompanyOfCustomer(company.getCompany_id());
        if (companyOfCustomer != null) {
            companyOfCustomer.deleteFromRealm();
        }

        CompanyOfCustomer companyOfCustomer1 = mRealm.createObject(CompanyOfCustomer.class);

        for (Coupon coupon : company.getCoupon()) {
            mRealm.copyToRealmOrUpdate(coupon);
            companyOfCustomer1.getCoupon().add(coupon);
        }

        companyOfCustomer1.setCompanyCustomer(company.getCompany_id(),
                company.getLogo(), company.getLogo_link(), company.getName(),
                company.getAddress());

        mRealm.commitTransaction();

    }


    public void addListCompanyCustomer(List<CompanyOfCustomer> listCompany) {

        mRealm.beginTransaction();

        List<CompanyOfCustomer> mListCompany = getListCompanyCustomer();
        for (CompanyOfCustomer company : mListCompany) {
            company.deleteFromRealm();
        }

        List<Coupon> listCoupon = getListCoupon();
        for (Coupon coupon : listCoupon) {
            coupon.deleteFromRealm();
        }


        for (CompanyOfCustomer companyOfCustomer : listCompany) {
            mRealm.copyToRealmOrUpdate(companyOfCustomer);
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

    private RealmResults<Coupon> getListCouponByCompanyId(String id) {
        return mRealm.where(Coupon.class)
                .equalTo("company_id", id)
                .findAll();
    }

    public CompanyOfCustomer getCompanyOfCustomer(String idCompany) {
        return mRealm
                .where(CompanyOfCustomer.class)
                .equalTo(MainApplication.ID_COMPANY, idCompany)
                .findFirst();
    }

    /* =================== END SAVE COMPANY OF CUSTOMER =============*/


    public void addNewsCustomerLike(NewsOfLike newsOfLike) {

        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(newsOfLike);
        mRealm.commitTransaction();
    }

    public void deleteNewsCustomerLike(String id) {
        NewsOfLike news = getNewsCustomerLike(id);
        if (news != null) {
            mRealm.beginTransaction();
            news.deleteFromRealm();
            mRealm.commitTransaction();
        }
    }

    public NewsOfLike getNewsCustomerLike(String id) {
        return mRealm.where(NewsOfLike.class)
                .equalTo("message_id", id)
                .findFirst();
    }

    public RealmResults<NewsOfLike> getListNewsCustomerLike() {
        return mRealm.where(NewsOfLike.class).findAll();
    }
    /* ================== END NEWS CUSTOMER LIKE ============*/


    /* ================== START NEWS MORE ==============*/

    public void addListNewsOfMore(List<NewsOfMore> listNews) {
        mRealm.beginTransaction();
        for (NewsOfMore news : listNews) {
            mRealm.copyToRealmOrUpdate(news);
        }
        mRealm.commitTransaction();
    }

    public void deleteListNewsOfMore() {
        mRealm.beginTransaction();
        RealmResults<NewsOfMore> listNews = getListNewsOfMore();
        for (NewsOfMore news : listNews) {
            news.deleteFromRealm();
        }
        mRealm.commitTransaction();
    }

    public RealmResults<NewsOfMore> getListNewsOfMore() {
        return mRealm.where(NewsOfMore.class).findAll();
    }
    /* ================== END NEWS MORE   ==============*/
}
