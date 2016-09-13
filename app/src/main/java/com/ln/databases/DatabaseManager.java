package com.ln.databases;

import com.ln.model.CompanyOfCustomer;
import com.ln.model.CompanyOfCustomer_Table;
import com.ln.model.Coupon;
import com.ln.model.CouponTemplate;
import com.ln.model.CouponTemplate_Table;
import com.ln.model.Coupon_Table;
import com.ln.model.NewMore;
import com.ln.model.NewMore_Table;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCompany_Table;
import com.ln.model.NewsOfCustomer;
import com.ln.model.NewsOfCustomer_Table;
import com.ln.model.OptionNews;
import com.ln.model.OptionNews_Table;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by Nhahv on 8/17/2016.
 * <></>
 */
@Database(name = DatabaseManager.NAME, version = DatabaseManager.VERSION)
public class DatabaseManager {

    private static final String TAG = "DatabaseManager";
    static final String NAME = "MyCoupon";

    static final int VERSION = 14;


    /* [ START COUPONTEMPLATE]*/

    public static List<CouponTemplate> getListCouponTemplate() {
        return new Select()
                .from(CouponTemplate.class)
                .queryList();
    }

    public static void deleteCouponTemplate(String id) {


        new Select()
                .from(CouponTemplate.class)
                .where(CouponTemplate_Table.coupon_template_id.is(id))
                .querySingle().delete();

//        new Delete()
//                .from(CouponTemplate.class)
//                .where(CouponTemplate_Table.coupon_template_id.is(id))
//                .querySingle();
    }

    public static void addListCouponTemplate(List<CouponTemplate> templates) {

        List<CouponTemplate> listItem = new Select().from(CouponTemplate.class).queryList();

        for (CouponTemplate item : listItem) {
            if (!isExistsCouponTemple(templates, item)) {
                item.delete();
            }
        }

        for (CouponTemplate item : templates) {
            item.save();
        }
    }

    private static boolean isExistsCouponTemple(List<CouponTemplate> list, CouponTemplate template) {

        for (CouponTemplate item : list) {
            if (item.getCoupon_template_id().equals(template.getCoupon_template_id())) {
                return true;
            }
        }
        return false;
    }
    /* [ END COUPONTEMPLATE]*/


    /* [ START NEWS OF CUSTOMER ]*/
    public static List<NewsOfCustomer> getListNewsOfCustomer(String user) {
        return new Select()
                .from(NewsOfCustomer.class)
                .where(NewsOfCustomer_Table.user.is(user))
                .queryList();
    }

//    public static void deleteNewsOfCustomer(String id) {
//        new Delete()
//                .from(NewsOfCustomer.class)
////                .where("message_id = ?", id)
//                .execute();
//    }

    public static void addListNewsOfCustomer(List<NewsOfCustomer> news, String user) {

        List<NewsOfCustomer> listNews = getListNewsOfCustomer(user);

        for (NewsOfCustomer item : listNews) {
            if (!isExistsNewsOfCustomer(listNews, item)) {
                item.delete();
            }
        }

        for (NewsOfCustomer item : news) {
            item.setUser(user);
            item.save();
        }
    }

    private static boolean isExistsNewsOfCustomer(List<NewsOfCustomer> list, NewsOfCustomer news) {

        for (NewsOfCustomer item : list) {
            if (item.getMessage_id().equals(news.getMessage_id())) {
                return true;
            }
        }
        return false;
    }

    /* [ END NEWS OF CUSTOMER ]*/


    /* [ START NEWS OF MORE ]*/
    public static List<NewMore> getListNewMore(String user) {
        return new Select()
                .from(NewMore.class)
                .where(NewMore_Table.user.is(user))
                .queryList();
    }

//    public static void deleteNewsOfCustomer(String id) {
//        new Delete()
//                .from(NewsOfCustomer.class)
////                .where("message_id = ?", id)
//                .execute();
//    }

    public static void addListNewMore(List<NewMore> news, String user) {

        List<NewMore> listNews = getListNewMore(user);

        for (NewMore item : listNews) {
            if (!isExistsNewsMore(listNews, item)) {
                item.delete();
            }
        }

        for (NewMore item : news) {
            item.setUser(user);
            item.save();
        }
    }

    private static boolean isExistsNewsMore(List<NewMore> list, NewMore news) {

        for (NewMore item : list) {
            if (item.getMessage_id().equals(news.getMessage_id())) {
                return true;
            }
        }
        return false;
    }

    /* [ END NEWS OF MORE ]*/


    /*[ START NEW OF COMPANY]*/
    public static void addNewsOfCompany(NewsOfCompany news) {
        news.save();
    }


    public static List<NewsOfCompany> getListNewsOfCompany() {
        return new Select()
                .from(NewsOfCompany.class)
                .queryList();
    }

    private static List<NewsOfCompany> getListNewsOfShop(String user) {
        return new Select()
                .from(NewsOfCompany.class)
                .where(NewsOfCompany_Table.user.is(user))
                .queryList();
    }

    public static NewsOfCompany getNewsOfCompanyById(String id) {
        return new Select()
                .from(NewsOfCompany.class)
                .where(NewsOfCompany_Table.message_id.is(id))
                .querySingle();
    }

    public static void deleteNewsOfCompany(String id) {
        new Delete()
                .from(NewsOfCompany.class)
                .where(NewsOfCompany_Table.message_id.is(id))
                .execute();
    }

    public static void addListNewsOfCompany(List<NewsOfCompany> news, String user) {

        List<NewsOfCompany> listNews = getListNewsOfShop(user);

        for (NewsOfCompany item : listNews) {
            if (!isExistsNewsOfShop(listNews, item)) {
                item.delete();
            }
        }

        for (NewsOfCompany item : news) {
            item.setUser(user);
            item.save();
        }
    }

    private static boolean isExistsNewsOfShop(List<NewsOfCompany> list, NewsOfCompany news) {

        for (NewsOfCompany item : list) {
            if (item.getMessage_id().equals(news.getMessage_id())) {
                return true;
            }
        }
        return false;
    }
  /*[ END NEW OF COMPANY]*/


    /* START OPTION NEWS*/
    public static List<OptionNews> getListOptionNews(int type, int typeShop) {
        return new Select()
                .from(OptionNews.class)
                .where(OptionNews_Table.typeShopOfCustomer.is(typeShop))
                .and(OptionNews_Table.type.is(type))
                .queryList();
    }

    public static List<OptionNews> getListOptionNews(int typeShop) {
        return new Select()
                .from(OptionNews.class)
                .where(OptionNews_Table.typeShopOfCustomer.is(typeShop))
                .queryList();
    }

    public static void addOptionNews(String idNews, String idUser, int type, int typeShop) {
        OptionNews item = new Select()
                .from(OptionNews.class)
                .where(OptionNews_Table.idNews.is(idNews))
                .and(OptionNews_Table.type.is(type))
                .and(OptionNews_Table.typeShopOfCustomer.is(typeShop))
                .querySingle();

        if (item != null) {
            item.delete();
        }

        item = new OptionNews(idNews, idUser, type, typeShop);
        item.save();
    }

    public static void addOptionNews(String idNews, String idUser, int typeShop) {
        OptionNews item = new Select()
                .from(OptionNews.class)
                .where(OptionNews_Table.idNews.is(idNews))
                .and(OptionNews_Table.typeShopOfCustomer.is(typeShop))
                .querySingle();

        if (item != null) {
            item.delete();
        }
        item = new OptionNews(idNews, idUser, 0, typeShop);
        item.save();

    }


    public static void deleteOptionNews(String idNews, int type, int typeShop) {
        new Delete()
                .from(OptionNews.class)
                .where(OptionNews_Table.idNews.is(idNews))
                .and(OptionNews_Table.type.is(type))
                .and(OptionNews_Table.typeShopOfCustomer.is(typeShop))
                .execute();
    }

    public static void deleteOptionNews(String idNews, int typeShop) {
        new Delete()
                .from(OptionNews.class)
                .where(OptionNews_Table.idNews.is(idNews))
                .and(OptionNews_Table.typeShopOfCustomer.is(typeShop))
                .execute();
    }


    /* SAVE COMPANY OF CUSTOMER*/
    public static void addShopOfCustomer(CompanyOfCustomer shopOfCustomer) {

        shopOfCustomer.save();

        List<Coupon> coupons = shopOfCustomer.getCoupon();
        for (Coupon coupon : coupons) {
            coupon.setCompanyOfCustomer(shopOfCustomer);
            coupon.save();
        }
    }

    public static List<CompanyOfCustomer> getListShopOfCustomer() {
        List<CompanyOfCustomer> listCompany =
                new Select()
                        .from(CompanyOfCustomer.class)
                        .queryList();

        for (CompanyOfCustomer item : listCompany) {
            List<Coupon> listCoupon = getListCoupon(item.getCompany_id());
            item.setCoupon(listCoupon);
        }

        return listCompany;
    }

    private static List<Coupon> getListCoupon(String idShop) {
        return new Select()
                .from(Coupon.class)
                .where(CompanyOfCustomer_Table.company_id.is(idShop))
                .queryList();
    }

    public static void deleteShopOfCustomer(String id) {
        new Delete()
                .from(CompanyOfCustomer.class)
                .where(CompanyOfCustomer_Table.company_id.is(id))
                .execute();
    }

    public static void addListShopOfCustomer(List<CompanyOfCustomer> shopOfCustomers) {


        List<CompanyOfCustomer> listShop = getListShopOfCustomer();

        for (CompanyOfCustomer item : listShop) {
            if (!isExistsShopOfCustomer(listShop, item)) {
                item.delete();

                // delete coupon
                new Delete()
                        .from(Coupon.class)
                        .where(CompanyOfCustomer_Table.company_id.is(item.getCompany_id()))
                        .execute();
            }
        }

        for (CompanyOfCustomer item : shopOfCustomers) {
            addShopOfCustomer(item);
        }
    }

    private static boolean isExistsShopOfCustomer(List<CompanyOfCustomer> list, CompanyOfCustomer news) {

        for (CompanyOfCustomer item : list) {
            if (item.getCompany_id().equals(news.getCompany_id())) {
                return true;
            }
        }
        return false;
    }

    public static CompanyOfCustomer getShopOfCustomer(String idShop) {
        CompanyOfCustomer item =
                new Select()
                        .from(CompanyOfCustomer.class)
                        .where(CompanyOfCustomer_Table.company_id.is(idShop))
                        .querySingle();

        List<Coupon> listCoupon = getListCoupon(item.getCompany_id());
        item.setCoupon(listCoupon);
        return item;
    }

    public static void deleteCouponById(String id) {
        new Delete()
                .from(Coupon.class)
                .where(Coupon_Table.company_id.is(id))
                .execute();
    }
}
