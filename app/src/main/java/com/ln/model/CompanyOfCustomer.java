package com.ln.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by luongnguyen on 4/8/16.
 * <></>
 */
@Table(name = "CompanyOfCustomer")
public class CompanyOfCustomer extends Model {

    @Column(name = "company_id", index = true)
    private String company_id;

    @Column(name = "logo")
    private String logo;

    @Column(name = "logo_link")
    private String logo_link;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

//    @Column(name = "images")
//    private String images;


    private List<Coupon> coupon;

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

//    public String getImages() {
//        return images;
//    }
//
//    public void setImages(String images) {
//        this.images = images;
//    }

    public void setCoupon(List<Coupon> coupon) {
        this.coupon = coupon;
    }

    public List<Coupon> getCoupon() {
        return coupon;
    }

    public List<Coupon> coupons() {
        return getMany(Coupon.class, "CompanyOfCustomer");
    }
}
