package com.ln.model;

/**
 * Created by Nhahv on 5/21/2016.
 */
public class CouponTemplate {

    private String coupon_template_id, content,
            created_date, company_id, value;

    private int duration;
    public CouponTemplate() {
    }

    public CouponTemplate(String coupon_template_id, String content,
                          int duration, String created_date, String company_id,
                          String value) {
        this.coupon_template_id = coupon_template_id;
        this.content = content;
        this.duration = duration;
        this.created_date = created_date;
        this.company_id = company_id;
        this.value = value;
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

    public void setContent(String content) {
        this.content = content;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
