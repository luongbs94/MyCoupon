package com.ln.model;


import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Coupon extends RealmObject implements Comparable<Coupon> {

    @PrimaryKey
    private String coupon_id;
    private String user_id;
    private String coupon_template_id;
    private long created_date;
    private long used_date;
    private String company_id;
    private String value;
    private int duration;
    private String code;
    private String user_name;
    private String user_social;
    private String user_image_link;

    public Coupon() {
    }

    public Coupon(String coupon_id, String user_id, String coupon_template_id,
                  long used_date, String company_id, String value, int duration, String code, String user_name, String user_social, String user_image_link) {
        this.coupon_id = coupon_id;
        this.user_id = user_id;
        this.coupon_template_id = coupon_template_id;
//        this.created_date = created_date;
        this.used_date = used_date;
        this.company_id = company_id;
        this.value = value;
        this.duration = duration;
        this.code = code;
        this.user_name = user_name;
        this.user_social = user_social;
        this.user_image_link = user_image_link;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCoupon_template_id() {
        return coupon_template_id;
    }

    public void setCoupon_template_id(String coupon_template_id) {
        this.coupon_template_id = coupon_template_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_social() {
        return user_social;
    }

    public void setUser_social(String user_social) {
        this.user_social = user_social;
    }

    public String getUser_image_link() {
        return user_image_link;
    }

    public void setUser_image_link(String user_image_link) {
        this.user_image_link = user_image_link;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getUsed_date() {
        return used_date;
    }

    public void setUsed_date(long used_date) {
        this.used_date = used_date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

//    public void setCoupon(String coupon_id, String user_id, String coupon_template_id,
//                          Date created_date, String used_date, String company_id, String value, int duration, String code, String user_name, String user_social, String user_image_link) {

    public void setCoupon(String coupon_id, String user_id, String coupon_template_id,
                          long used_date, String company_id, String value,
                          int duration, String code, String user_name, String user_social, String user_image_link) {

        this.coupon_id = coupon_id;
        this.user_id = user_id;
        this.coupon_template_id = coupon_template_id;
//        this.created_date = created_date;
        this.used_date = used_date;
        this.company_id = company_id;
        this.value = value;
        this.duration = duration;
        this.code = code;
        this.user_name = user_name;
        this.user_social = user_social;
        this.user_image_link = user_image_link;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    @Override
    public int compareTo(@NonNull Coupon another) {
        return (created_date > another.getCreated_date()) ? -1 : 1;
    }
}


