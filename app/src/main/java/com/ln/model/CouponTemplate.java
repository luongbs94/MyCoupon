package com.ln.model;

import java.util.Date;

/**
 * Created by luongnguyen on 4/1/16.
 */
public class CouponTemplate  {

    String coupon_template_id;
    String content;
    String value;
    int duration;
    Date created_date;
    String company_id;

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCoupon_template_id() {
        return coupon_template_id;
    }

    public void setCoupon_template_id(String coupon_template_id) {
        this.coupon_template_id = coupon_template_id;
    }

    public String getContent() {
        return content;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public void setContent(String content) {
        this.content = content;

    }
}
