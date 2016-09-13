package com.ln.model;

import com.ln.databases.DatabaseManager;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Nhahv on 5/21/2016.
 * <></>
 */

@Table(database = DatabaseManager.class)
public class CouponTemplate extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private String coupon_template_id;

    @Column
    private String content;

    @Column
    private String created_date;

    @Column
    private String company_id;

    @Column
    private String value;

    @Column
    private int duration;


    public CouponTemplate() {
    }

    public CouponTemplate(String coupon_template_id) {
        this.coupon_template_id = coupon_template_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
