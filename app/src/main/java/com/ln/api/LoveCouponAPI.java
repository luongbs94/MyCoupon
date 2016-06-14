package com.ln.api;

/**
 * Created by luongnguyen on 4/1/16.
 */

import com.ln.model.Company;
import com.ln.model.Company1;
import com.ln.model.Coupon;
import com.ln.model.CouponTemplate;
import com.ln.model.Message;
import com.ln.model.User;

import java.util.ArrayList;
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
    Call<List<CouponTemplate>> getCouponTemplatesByCompanyId(@Query("company_id") int id);

    @GET("/get_coupon_template_by_company_id")
    Call<List<CouponTemplate>> getCouponTemplates(@Header("Authorization") String authorization, @Query("company_id") int id);

    @GET("/get_news_by_company_id")
    Call<List<Message>> getNewsByCompanyId(@Query("company_id") int id);


    @GET("/get_company_profile")
    Call<List<Company>> getCompanyProfile(@Query("user_name") String user_name, @Query("password") String password, @Query("user_id") String user_id);

    @GET("/get_company_profile")
    Call<List<Company>> getCompanyProfileSocial(@Query("user_id") String user_id);

    @POST("/addCouponTemplate")
    Call<CouponTemplate> addCouponTemplate(@Body CouponTemplate template);

    @POST("/deleteCouponTemplate")
    Call<CouponTemplate> deleteCouponTemplate(@Body CouponTemplate template);

    @POST("/addMessage")
    Call<Message> addMessage(@Body Message template);

    @POST("/deleteMessage")
    Call<Message> deleteMessage(@Body Message template);

    @POST("/addCoupon")
    Call<Coupon> addCoupon(@Body Coupon template);

    @GET("/get_companies_by_user_id")
    Call<List<Company1>> getCompaniesByUserId(@Query("user_id") String id);


    @GET("/get_news_by_user_id")
    Call<List<Message>> getNewsByUserId(@Query("user_id") String id);

    @POST("/update_user_coupon")
    Call<Company1> updateUserCoupon(@Body Coupon template);


    @GET("/getCoupon")
    Call<List<Coupon>> getCoupon(@Query("coupon_id") String id);

    @GET("/get_created_coupon_by_company_id")
    Call<ArrayList<Coupon>> getCreatedCoupon(@Query("company_id") String company_id, @Query("utc1") String utc1, @Query("utc2") String utc2);

    @GET("/get_used_coupon_by_company_id")
    Call<ArrayList<Coupon>> getUsedCoupon(@Query("company_id") String company_id, @Query("utc1") String utc1, @Query("utc2") String utc2);

    @GET("/get_user_profile")
    Call<List<User>> updateUserToken(@Query("user_id") String user_id, @Query("device_token") String device_token, @Query("device_os") String device_os);

    @POST("/useCoupon")
    Call<Coupon> useCoupon(@Body Coupon template);

    @POST("/updateCompany")
    Call<Company> updateCompany(@Body Company template);


    @GET("/get_web_token")
    Call<ResponseBody> getWebTokenUser(@Query("user_name") String user_name, @Query("password") String password);

    @GET("/get_web_token")
    Call<ResponseBody> getWebTokenSocial(@Query("user_id") String user_id, @Query("social") String social, @Query("access_token") String access_token);

    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(@Part("upload") RequestBody description,
                              @Part MultipartBody.Part file);

}
