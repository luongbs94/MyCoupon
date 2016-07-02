package com.ln.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by luongnguyen on 4/8/16.
 * <></>
 */
public class CompanyOfCustomer extends RealmObject {

    @PrimaryKey
    private String company_id;
    private String logo;
    private String logo_link;
    private String name;
    private String address;
    private RealmList<Coupon> coupon;

    public CompanyOfCustomer() {
    }

    public CompanyOfCustomer(String company_id, String logo,
                             String logo_link, String name,
                             String address, RealmList<Coupon> coupon) {
        this.company_id = company_id;
        this.logo = logo;
        this.logo_link = logo_link;
        this.name = name;
        this.address = address;
        this.coupon = coupon;
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

    public RealmList<Coupon> getCoupon() {
        return coupon;
    }

    public void setCoupon(RealmList<Coupon> coupon) {
        this.coupon = coupon;
    }

    public String getLogo_link() {
        return logo_link;
    }

    public void setLogo_link(String logo_link) {
        this.logo_link = logo_link;
    }

    public void setCompanyCustomer(String company_id, String logo,
                                   String logo_link, String name,
                                   String address) {
        this.company_id = company_id;
        this.logo = logo;
        this.logo_link = logo_link;
        this.name = name;
        this.address = address;
    }
}
