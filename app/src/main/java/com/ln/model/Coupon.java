package com.ln.model;


import android.support.annotation.NonNull;

import com.ln.databases.DatabaseManager;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = DatabaseManager.class)
public class Coupon extends BaseModel implements Comparable<Coupon> {

    @PrimaryKey
    @Index
    @Column
    private String coupon_id;

    @Column
    private String user_id;

    @Column
    private String coupon_template_id;

    @Column
    private long created_date;

    @Column
    private long used_date;

    @Column
    private String company_id;

    @Column
    private String value;

    @Column
    private int duration;

    @Column
    private String user_name;

    @Column
    private String user_social;

    @Column
    private String user_image_link;

    @Column
    private String content;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    private CompanyOfCustomer companyOfCustomer;

    public Coupon() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public CompanyOfCustomer getCompanyOfCustomer() {
        return companyOfCustomer;
    }

    public void setCompanyOfCustomer(CompanyOfCustomer companyOfCustomer) {
        this.companyOfCustomer = companyOfCustomer;
    }

    @Override
    public int compareTo(@NonNull Coupon another) {
        return (created_date > another.getCreated_date()) ? -1 : 1;
    }
}


