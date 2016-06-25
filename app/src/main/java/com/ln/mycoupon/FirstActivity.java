package com.ln.mycoupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.Company1;
import com.ln.model.User;
import com.ln.mycoupon.customer.CustomerLoginActivity;
import com.ln.mycoupon.customer.CustomerMainActivity;
import com.ln.mycoupon.shop.ShopLoginActivity;
import com.ln.mycoupon.shop.ShopMainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        getSizeScreen();
        initViews();
        addEvents();

        if (MainApplication.sharedPreferences.getBoolean(MainApplication.LOGIN_SHOP, false)) {

            String data = MainApplication.sharedPreferences.getString(MainApplication.SHOP_DATA, "");
            SaveData.company = gson.fromJson(data, Company.class);

            startActivity(new Intent(FirstActivity.this, ShopMainActivity.class));
            finish();
        } else if (MainApplication.sharedPreferences.getBoolean(MainApplication.LOGIN_CLIENT, false)) {
            String data = MainApplication.sharedPreferences.getString(MainApplication.CLIENT_DATA, "");
            SaveData.listCompanyCustomer = gson.fromJson(data, new TypeToken<List<Company1>>() {
            }.getType());

            if (MainApplication.sDetailUser != null) {
                getCompanyByUserId(MainApplication.sDetailUser.getId());
                updateUserToken(MainApplication.sDetailUser.getAccessToken(), MainApplication.getDeviceToken(), "android");
            }
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

    private void getSizeScreen() {

        // get size screen android
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        MainApplication.HEIGHT_SCREEN = outMetrics.heightPixels / density;
        MainApplication.WIDTH_SCREEN = outMetrics.widthPixels / density;
    }


    private void getCompanyByUserId(final String userId) {

        Call<List<Company1>> call3 = MainApplication.apiService.getCompaniesByUserId(userId);
        call3.enqueue(new Callback<List<Company1>>() {

            @Override
            public void onResponse(Call<List<Company1>> arg0, Response<List<Company1>> arg1) {

                List<Company1> templates = arg1.body();
                if (templates == null) {
                    SaveData.listCompanyCustomer = new ArrayList<>();
                } else {
                    SaveData.listCompanyCustomer = templates;
                }

                SaveData.USER_ID = userId;

                String data = gson.toJson(SaveData.listCompanyCustomer);
                MainApplication.editor.putBoolean(MainApplication.LOGIN_SHOP, false);
                MainApplication.editor.putBoolean(MainApplication.LOGIN_CLIENT, true);
                MainApplication.editor.putString(MainApplication.CLIENT_DATA, data);
                MainApplication.editor.commit();

                Intent intent = new Intent(FirstActivity.this, CustomerMainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<List<Company1>> arg0, Throwable arg1) {
            }
        });
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
