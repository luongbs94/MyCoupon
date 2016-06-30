package com.ln.api;

import com.ln.model.Company;
import com.ln.model.Company1;

import java.util.List;

/**
 * Created by luongnguyen on 4/2/16.
 * <></>
 */
public class SaveData {

    public static Company company;

    public static List<Company1> listCompanyCustomer;
    public static boolean updateCoupon = false;


    public static Company getCompany() {
        return company;
    }

    public static void setCompany(Company company1) {
        company = company1;
    }

    public static String web_token = "";
    public static String USER_ID = "";

    public static Company1 getCompany(String idCompany) {

        for (Company1 company : listCompanyCustomer) {
            if (company.getCompany_id().equals(idCompany)) {
                return company;
            }
        }

        return null;
    }

}
