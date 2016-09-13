package com.ln.model;

import com.ln.databases.DatabaseManager;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luongnguyen on 4/8/16.
 * <></>
 */
@Table(database = DatabaseManager.class)
public class CompanyOfCustomer extends BaseModel {

    @PrimaryKey
    @Index
    @Column
    private String company_id;

    @Column
    private String logo;

    @Column
    private String logo_link;

    @Column
    private String name;

    @Column
    private String address;

    private List<Coupon> coupon = new ArrayList<>();

    public CompanyOfCustomer() {
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo_link() {
        return logo_link;
    }

    public void setLogo_link(String logo_link) {
        this.logo_link = logo_link;
    }


    public void setCoupon(List<Coupon> coupon) {
        this.coupon = coupon;
    }

    public List<Coupon> getCoupon() {
        return coupon;
    }


}
