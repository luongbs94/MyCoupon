package com.ln.mycoupon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.model.User;
import com.ln.mycoupon.customer.CustomerLoginActivity;
import com.ln.mycoupon.customer.CustomerMainActivity;
import com.ln.mycoupon.shop.ShopLoginActivity;
import com.ln.mycoupon.shop.ShopMainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();
    private Gson gson = new Gson();

    private Button mBtnShop, mBtnCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        initViews();
        addEvents();

        SharedPreferences preferences = getSharedPreferences(
                MainApplication.SHARED_PREFERENCE, MODE_PRIVATE);


        boolean isShop = preferences.getBoolean(MainApplication.LOGIN_SHOP, false);
        boolean isCustomer = preferences.getBoolean(MainApplication.LOGIN_CLIENT, false);

        Log.d(TAG, "isShop " + isShop);
        Log.d(TAG, "isCustomer " + isCustomer);

        if (isShop && !isCustomer) {

//            String data = MainApplication.sharedPreferences.getString(MainApplication.SHOP_DATA, "");
//            SaveData.company = gson.fromJson(data, Company.class);

            startActivity(new Intent(FirstActivity.this, ShopMainActivity.class));
            finish();
        } else if (isCustomer && !isShop) {
//            String data = MainApplication.sharedPreferences.getString(MainApplication.CLIENT_DATA, "");
//            SaveData.listCompanyCustomer = gson.fromJson(data, new TypeToken<List<CompanyOfCustomer>>() {
//            }.getType());
//
//            if (MainApplication.sDetailUser != null) {
//                getCompanyByUserId(MainApplication.sDetailUser.getId());
//                updateUserToken(MainApplication.sDetailUser.getAccessToken(), MainApplication.getDeviceToken(), "android");
//            }

            Intent intent = new Intent(FirstActivity.this, CustomerMainActivity.class);
            startActivity(intent);
            finish();
        }

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z'('Z')'", Locale.getDefault());
        //Convert the date from the local timezone to UTC timezone
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatInUTC = formatter.format(now);

        // Date now = new Date();
        Log.d(TAG, dateFormatInUTC);
    }


    private void initViews() {

        setTitle(R.string.banla);
        mBtnShop = (Button) findViewById(R.id.shop);
        mBtnCustomer = (Button) findViewById(R.id.customer);
    }

    private void addEvents() {
        mBtnShop.setOnClickListener(new Events());
        mBtnCustomer.setOnClickListener(new Events());
    }

    private void onClickLoginShop() {
        Intent intent = new Intent(FirstActivity.this, ShopLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void onClickLoginCustomer() {
        Intent intent = new Intent(FirstActivity.this, CustomerLoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void getCompanyByUserId(final String userId) {

//        Call<List<CompanyOfCustomer>> call3 = MainApplication.apiService.getCompaniesByUserId(userId);
//        call3.enqueue(new Callback<List<CompanyOfCustomer>>() {
//
//            @Override
//            public void onResponse(Call<List<CompanyOfCustomer>> arg0, Response<List<CompanyOfCustomer>> arg1) {
//
//                List<CompanyOfCustomer> templates = arg1.body();
//                if (templates == null) {
//                    SaveData.listCompanyCustomer = new ArrayList<>();
//                } else {
//                    SaveData.listCompanyCustomer = templates;
//                }
//
////                SaveData.USER_ID = userId;
////
////                String data = gson.toJson(SaveData.listCompanyCustomer);
////                MainApplication.editor.putBoolean(MainApplication.LOGIN_SHOP, false);
////                MainApplication.editor.putBoolean(MainApplication.LOGIN_CLIENT, true);
////                MainApplication.editor.putString(MainApplication.CLIENT_DATA, data);
////                MainApplication.editor.commit();
//
//                SharedPreferences preferences =
//                        getSharedPreferences(MainApplication.SHARED_PREFERENCE, MODE_PRIVATE);
//
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean(MainApplication.LOGIN_SHOP, false);
//                editor.putBoolean(MainApplication.LOGIN_CLIENT, true);
//                editor.apply();
//
//                Intent intent = new Intent(FirstActivity.this, CustomerMainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onFailure(Call<List<CompanyOfCustomer>> arg0, Throwable arg1) {
//            }
//        });
        SharedPreferences preferences =
                getSharedPreferences(MainApplication.SHARED_PREFERENCE, MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainApplication.LOGIN_SHOP, false);
        editor.putBoolean(MainApplication.LOGIN_CLIENT, true);
        editor.apply();

        Intent intent = new Intent(FirstActivity.this, CustomerMainActivity.class);
        startActivity(intent);
        finish();

    }

    private void updateUserToken(String userId, String token, String device_os) {

        Call<List<User>> call = MainApplication.apiService.updateUserToken(userId, token, device_os);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> arg0, Response<List<User>> arg1) {

                MainApplication.setIsAddToken(true);
            }

            @Override
            public void onFailure(Call<List<User>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }


    private class Events implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.shop:
                    onClickLoginShop();
                    break;
                case R.id.customer:
                    onClickLoginCustomer();
                    break;
                default:
                    break;
            }
        }
    }
}
