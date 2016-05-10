package com.ln.model;

import java.util.Date;

/**
 * Created by luongnguyen on 4/7/16.
 */
public class Coupon {

    public String coupon_id;
    public String coupon_template_id;
    public String company_id;
    public String value;
    public String user_id;

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
}


