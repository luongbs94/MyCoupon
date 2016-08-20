package com.ln.databases;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.Coupon;
import com.ln.model.CouponTemplate;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCustomer;
import com.ln.model.OptionNews;

import java.util.List;

/**
 * Created by Nhahv on 8/17/2016.
 * <></>
 */

public class DatabaseManager {

    public static List<CouponTemplate> getListCouponTemplate() {
        return new Select()
                .from(CouponTemplate.class)
                .execute();
    }

    public static void deleteCouponTemplate(String id) {
        new Delete()
                .from(CouponTemplate.class)
                .where("coupon_template_id = ?", id)
                .execute();
    }

    public static void addListCouponTemplate(List<CouponTemplate> templates) {

        new Delete().from(CouponTemplate.class).execute();
        ActiveAndroid.beginTransaction();
        try {
            for (CouponTemplate item : templates) {
                item.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
    /* ================ END COUPON TEMPLATE OF SHOP ============*/

    /* ================ START SHOP OF CUSTOMER ==========*/


    /* ================ START NEWS OF CUSTOMER ==========*/
    public static List<NewsOfCustomer> getListNewsOfCustomer(int type) {
        return new Select()
                .from(NewsOfCustomer.class)
                .where("type = ?", type)
                .execute();
    }

    public static void deleteNewsOfCustomer(String id) {
        new Delete()
                .from(NewsOfCustomer.class)
                .where("message_id = ?", id)
                .execute();
    }

    public static void addListNewsOfCustomer(List<NewsOfCustomer> news, int type) {

        new Delete().from(NewsOfCustomer.class).where("type = ?", type).execute();
        ActiveAndroid.beginTransaction();
        try {
            for (NewsOfCustomer item : news) {
                item.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }


    /* ================ START NEWS OF SHOP ==========*/

    public static void addNewsOfCompany(NewsOfCompany news) {
        NewsOfCompany item = getNewsOfCompanyById(news.getCompany_id());
        if (item != null) {
            item.delete();
        }
        news.save();
    }

    public static List<NewsOfCompany> getListNewsOfCompany() {
        return new Select()
                .from(NewsOfCompany.class)
                .execute();
    }

    public static NewsOfCompany getNewsOfCompanyById(String id) {
        return new Select()
                .from(NewsOfCompany.class)
                .where("message_id = ?", id)
                .executeSingle();
    }

    public static void deleteNewsOfCompany(String id) {
        new Delete()
                .from(NewsOfCompany.class)
                .where("message_id = ?", id)
                .execute();
    }

    public static void addListNewsOfCompany(List<NewsOfCompany> news) {

        List<NewsOfCompany> listNews = getListNewsOfCompany();
        if (listNews != null) {
            ActiveAndroid.beginTransaction();
            try {
                for (NewsOfCompany item : listNews) {
                    item.delete();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }

        ActiveAndroid.beginTransaction();
        try {
            for (NewsOfCompany item : news) {
                item.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }


    /* START OPTION NEWS*/
    public static List<OptionNews> getListOptionNews(int type, int typeShop) {
        return new Select()
                .from(OptionNews.class)
                .where("typeShopOfCustomer = ?", typeShop)
                .where("type = ?", type)
                .execute();
    }

    public static List<OptionNews> getListOptionNews(int typeShop) {
        return new Select()
                .from(OptionNews.class)
                .where("typeShopOfCustomer = ?", typeShop)
                .execute();
    }

    public static void addOptionNews(String idNews, String idUser, int type, int typeShop) {
        OptionNews item = new Select()
                .from(OptionNews.class)
                .where("idNews = ?", idNews)
                .where("type = ?", type)
                .where("typeShopOfCustomer = ?", typeShop)
                .executeSingle();

        if (item != null) {
            item.delete();
        }

        item = new OptionNews(idNews, idUser, type, typeShop);
        item.save();
    }

    public static void addOptionNews(String idNews, String idUser, int typeShop) {
        OptionNews item = new Select()
                .from(OptionNews.class)
                .where("idNews = ?", idNews)
                .where("typeShopOfCustomer = ?", typeShop)
                .executeSingle();

        if (item != null) {
            item.delete();
        }
        item = new OptionNews(idNews, idUser, 0, typeShop);
        item.save();

    }


    public static void deleteOptionNews(String id, int type, int typeShop) {
        new Delete()
                .from(OptionNews.class)
                .where("idNews = ?", id)
                .where("type = ?", type)
                .where("typeShopOfCustomer = ?", typeShop)
                .execute();
    }

    public static void deleteOptionNews(String id, int typeShop) {
        new Delete()
                .from(OptionNews.class)
                .where("idNews = ?", id)
                .where("typeShopOfCustomer = ?", typeShop)
                .execute();
    }


    /* SAVE COMPANY OF CUSTOMER*/
    public static void addShopOfCustomer(CompanyOfCustomer shopOfCustomer) {

        CompanyOfCustomer item
                = new Select()
                .from(CompanyOfCustomer.class)
                .where("company_id = ?", shopOfCustomer.getCompany_id())
                .executeSingle();

        if (item != null) {
            item.delete();
        }

        ActiveAndroid.beginTransaction();
        try {

            shopOfCustomer.save();

            List<Coupon> coupons = shopOfCustomer.getCoupon();
            for (Coupon coupon : coupons) {
                coupon.setCompanyOfCustomer(shopOfCustomer);
                coupon.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ActiveAndroid.endTransaction();
        }

    }

    public static List<CompanyOfCustomer> getListShopOfCustomer() {
        return new Select()
                .from(CompanyOfCustomer.class)
                .execute();
    }

    public static void deleteShopOfCustomer(String id) {
        new Delete()
                .from(CompanyOfCustomer.class)
                .where("company_id = ?", id)
                .execute();
    }

    public static void addListShopOfCustomer(List<CompanyOfCustomer> shopOfCustomers) {

        new Delete().from(CompanyOfCustomer.class).execute();
        ActiveAndroid.beginTransaction();
        try {
            for (CompanyOfCustomer item : shopOfCustomers) {
                addShopOfCustomer(item);
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static CompanyOfCustomer getShopOfCustomer(String idShop) {
        return new Select()
                .from(CompanyOfCustomer.class)
                .where("company_id = ?", idShop)
                .executeSingle();
    }

    public static void deleteCouponById(String id) {
        new Delete()
                .from(Coupon.class)
                .where("coupon_id = ?", id)
                .execute();
    }
}
