package com.ln.model;

import java.util.List;

/**
 * Created by luongnguyen on 4/8/16.
 * <></>
 */
public class Company1 {

    private String name;
    private String address;
    private String logo;
    private String company_id;
    private String created_date;
    private List<Coupon> coupon;

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

    public List<Coupon> getCoupon() {
        return coupon;
    }

    public void setCoupon(List<Coupon> coupon) {
        this.coupon = coupon;
    }
}
