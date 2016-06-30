package com.ln.model;

/**
 * Created by Nhahv on 6/30/2016.
 * <></>
 */
public class CompanyLocation {

    private String company_id;
    private String city;
    private String country_name;

    public CompanyLocation(String company_id, String city, String country_name) {
        this.company_id = company_id;
        this.city = city;
        this.country_name = country_name;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }
}
