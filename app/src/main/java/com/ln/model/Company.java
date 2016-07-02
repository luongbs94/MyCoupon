package com.ln.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by luongnguyen on 4/1/16.
 * <></>
 */
public class Company extends RealmObject {

    @PrimaryKey
    private String company_id;
    private String name;
    private String address;
    private String logo;
    private String created_date;
    private String user_id;
    private String user1;
    private String pass1;
    private String user1_admin;
    private String user2;
    private String pass2;
    private String user2_admin;
    private String ip;
    private String logo_link;
    private String city;
    private String country_name;

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

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getPass1() {
        return pass1;
    }

    public void setPass1(String pass1) {
        this.pass1 = pass1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getPass2() {
        return pass2;
    }

    public void setPass2(String pass2) {
        this.pass2 = pass2;
    }

    public String getUser2_admin() {
        return user2_admin;
    }

    public void setUser2_admin(String user2_admin) {
        this.user2_admin = user2_admin;
    }

    public String getUser1_admin() {
        return user1_admin;
    }

    public void setUser1_admin(String user1_admin) {
        this.user1_admin = user1_admin;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getLogo() {
        return logo;
    }

    public String getLogo_link() {
        return logo_link;
    }

    public void setLogo_link(String logo_link) {
        this.logo_link = logo_link;
    }

    public String getIp() {
        return ip;
    }

    public String getCity() {
        return city;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCompany(String company_id, String name, String address,
                           String logo, String created_date, String user_id,
                           String user1, String pass1, String user1_admin,
                           String user2, String pass2, String user2_admin,
                           String ip, String logo_link, String city,
                           String country_name) {
        this.company_id = company_id;
        this.name = name;
        this.address = address;
        this.logo = logo;
        this.created_date = created_date;
        this.user_id = user_id;
        this.user1 = user1;
        this.pass1 = pass1;
        this.user1_admin = user1_admin;
        this.user2 = user2;
        this.pass2 = pass2;
        this.user2_admin = user2_admin;
        this.ip = ip;
        this.logo_link = logo_link;
        this.city = city;
        this.country_name = country_name;
    }

}


