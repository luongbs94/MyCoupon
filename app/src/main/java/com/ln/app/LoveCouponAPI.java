package com.ln.app;

/**
 * Created by luongnguyen on 4/1/16.
 * <></>
 */

import com.ln.model.CityOfUser;
import com.ln.model.Company;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.CouponTemplate;
import com.ln.model.NewMore;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCustomer;
import com.ln.mycoupon.ForgetPasswordActivity;
import com.ln.mycoupon.customer.CustomerLoginActivity;
import com.ln.mycoupon.shop.ShopLoginActivity;
import com.ln.until.Until;
import com.ln.until.UntilCoupon;
import com.ln.until.UntilCouponTemplate;
import com.ln.until.UntilNews;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface LoveCouponAPI {

    @GET("/get_coupon_template_by_company_id")
    Call<List<CouponTemplate>> getCouponTemplates(
            @Header("Authorization") String authorization,
            @Query("company_id") String id);

    @GET("/get_news_by_company_id")
    Call<List<NewsOfCompany>> getNewsByCompanyId(
            @Query("company_id") String id);


    @POST("/get_company_profile")
    Call<List<Company>> getCompanyProfile(
            @Body ShopLoginActivity.ShopProfile profile
    );

    @POST("/addCouponTemplate")
    Call<Integer> addCouponTemplate(
            @Header("Authorization") String token,
            @Body UntilCouponTemplate template);

    @POST("/deleteCouponTemplate")
    Call<Integer> deleteCouponTemplate(
            @Header("Authorization") String token,
            @Body Until template);

    @POST("/addMessage")
    Call<Integer> addMessage(
            @Header("Authorization") String token,
            @Body UntilNews news);

    @POST("/addCoupon")
    Call<Integer> addCoupon(
            @Header("Authorization") String token,
            @Body UntilCoupon coupon);

    @GET("/get_companies_by_user_id")
    Call<List<CompanyOfCustomer>> getCompaniesByUserId(
            @Query("user_id") String id);

    @GET("/get_news_by_user_id")
    Call<List<NewsOfCustomer>> getNewsByUserId(@Query("user_id") String id);

    // khong tim thay dang can hoi
    @POST("/update_user_coupon")
    Call<List<CompanyOfCustomer>> updateUserCoupon(
            @Header("city") String city,
            @Body UntilCoupon template);

    @GET("/get_created_coupon_by_company_id")
    Call<List<UntilCoupon>> getCreatedCoupon(
            @Header("Authorization") String token,
            @Query("company_id") String company_id,
            @Query("utc1") long utc1,
            @Query("utc2") long utc2);

    @GET("/get_used_coupon_by_company_id")
    Call<List<UntilCoupon>> getUsedCoupon(
            @Header("Authorization") String token,
            @Query("company_id") String company_id,
            @Query("utc1") long utc1,
            @Query("utc2") long utc2);


    @POST("/get_user_profile")
    Call<Integer> updateUserToken(@Body CustomerLoginActivity.CustomerProfile profile);

    @POST("/useCoupon")
    Call<Integer> useCoupon(@Body Until coupon);

    @POST("/updateCompany")
    Call<Integer> updateCompany(
            @Header("Authorization") String token,
            @Body Company template);


    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(@Part("upload") RequestBody description,
                              @Part MultipartBody.Part file);

    @GET("/is_username_avaiable")
    Call<Integer> isExists(@Query("company_id") String company_id,
                           @Query("username") String username);

    @POST("/deleteMessage")
    Call<Integer> deleteMessage(
            @Header("Authorization") String token,
            @Body Until news);

    @GET("/json")
    Call<CityOfUser> getCityOfUser();

    @GET("/json")
    Call<CityOfUser> getCityOfUser2();

    @GET("/get_news_more_by_user_id")
    Call<List<NewMore>> getNewsMoreByUserId(@Query("user_id") String user_id,
                                            @Query("city") String city);

    @POST("/updateMessage")
    Call<Integer> updateMessages(
            @Header("Authorization") String authorization,
            @Body UntilNews news);

    @POST("/sendPassword")
    Call<String> sendPassword(@Body ForgetPasswordActivity.SendEmail value);
}
