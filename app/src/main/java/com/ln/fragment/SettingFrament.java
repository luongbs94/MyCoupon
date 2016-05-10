package com.ln.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ln.api.SaveData;
import com.ln.model.Company;
import com.ln.mycoupon.R;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by luongnguyen on 4/14/16.
 */
public class SettingFrament extends Fragment {

    MaterialEditText nameCompany, adressCompany, user1, pass1, user2, pass2;
    CheckBox checkBox, checkBox1;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_setting, container, false);

        nameCompany = (MaterialEditText) v.findViewById(R.id.name_company);
        adressCompany = (MaterialEditText) v.findViewById(R.id.adress_company);
        user1 = (MaterialEditText) v.findViewById(R.id.username1);
        pass1 = (MaterialEditText) v.findViewById(R.id.password1);
        pass2 = (MaterialEditText) v.findViewById(R.id.password2);

        user2 = (MaterialEditText) v.findViewById(R.id.username2);

        checkBox = (CheckBox) v.findViewById(R.id.check_admin);
        checkBox1 = (CheckBox) v.findViewById(R.id.check_admin2);
        init();


        return v;
    }

    public void init(){
        Company company = SaveData.company;
        if(company.name != null){
            nameCompany.setText(company.name);
        }

        if(company.address != null){
            adressCompany.setText(company.address);
        }

        if(company.user1 != null){
            user1.setText(company.user1);
        }

        if(company.user2 != null){
            user2.setText(company.user2);
        }

        if(company.pass1 != null){
            pass1.setText(company.pass1);
        }

        if(company.pass2 != null){
            pass2.setText(company.pass2);
        }

        if(company.user1_admin.equals("1")){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }

        if(company.user2_admin.equals("1")){
            checkBox1.setChecked(true);
        }else{
            checkBox1.setChecked(false);
        }

    }



}
