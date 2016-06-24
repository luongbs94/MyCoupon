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
import com.ln.mycoupon.customer.CustomerLoginActivity;
import com.ln.mycoupon.shop.ShopLoginActivity;
import com.ln.mycoupon.shop.ShopMainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
//            finish();
        } else if (MainApplication.sharedPreferences.getBoolean(MainApplication.LOGIN_CLIENT, false)) {
            String data = MainApplication.sharedPreferences.getString(MainApplication.CLIENT_DATA, "");
            SaveData.listCompanyCustomer = gson.fromJson(data, new TypeToken<List<Company1>>() {
            }.getType());
            onClickLoginCustomer();
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
//        finish();
    }

    private void onClickLoginCustomer() {
        Intent intent = new Intent(FirstActivity.this, CustomerLoginActivity.class);
        startActivity(intent);
//        finish();
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
