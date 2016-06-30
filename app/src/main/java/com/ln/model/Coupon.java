package com.ln.model;


import java.util.Date;

public class Coupon {

    private String coupon_id, user_id, coupon_template_id,
             company_id, value, user_name,
            user_social, user_image_link;
    int duration;
    Date created_date;



    public Date getCreated_date() {
        return created_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
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


}


