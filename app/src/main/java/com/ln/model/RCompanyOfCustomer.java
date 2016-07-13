package com.ln.model;

import java.util.List;

/**
 * Created by Nhahv on 7/13/2016.
 * <></>
 */
public class RCompanyOfCustomer {

    private String company_id;
    private List<CompanyOfCustomer> companies;

    public RCompanyOfCustomer() {
    }

    public String getCompany_id() {
        return company_id;
    }

    public List<CompanyOfCustomer> getCompanies() {
        return companies;
    }
}
