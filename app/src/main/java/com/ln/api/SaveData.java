package com.ln.api;

import com.ln.model.CompanyOfCustomer;

import java.util.List;

/**
 * Created by luongnguyen on 4/2/16.
 * <></>
 */
public class SaveData {

    public static List<CompanyOfCustomer> listCompanyCustomer;
    public static boolean updateCoupon = false;


    public static String web_token = "";
    public static String USER_ID = "";

    public static CompanyOfCustomer getCompany(String idCompany) {

        for (CompanyOfCustomer company : listCompanyCustomer) {
            if (company.getCompany_id().equals(idCompany)) {
                return company;
            }
        }

        return null;
    }

}
