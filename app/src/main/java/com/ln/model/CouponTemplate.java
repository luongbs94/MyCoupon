package com.ln.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Nhahv on 5/21/2016.
 * <></>
 */

@Table(name = "CouponTemplate")
public class CouponTemplate extends Model {

    @Column(name = "coupon_template_id", index = true)
    private String coupon_template_id;

    @Column(name = "content")
    private String content;

    @Column(name = "created_date")
    private String created_date;

    @Column(name = "company_id")
    private String company_id;

    @Column(name = "value")
    private String value;

    @Column(name = "duration")
    private int duration;

    public CouponTemplate() {
    }

    public CouponTemplate(String coupon_template_id) {
        this.coupon_template_id = coupon_template_id;
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
