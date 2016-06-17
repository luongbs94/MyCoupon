package com.ln.mycoupon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.gcm.GcmIntentService;
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

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private String TAG = getClass().getSimpleName();
    private Gson gson = new Gson();

    private Button mBtnShop, mBtnCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_first);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        MainApplication.HEIGHT = displaymetrics.heightPixels;
        MainApplication.WIDTH = displaymetrics.widthPixels;


        initViews();

        addEvents();

        if (MainApplication.sharedPreferences.getBoolean(MainApplication.LOGINSHOP, false)) {

            String data = MainApplication.sharedPreferences.getString(MainApplication.SHOP_DATA, "");
            SaveData.company = gson.fromJson(data, Company.class);
            Intent intent = new Intent(FirstActivity.this, ShopMainActivity.class);
            startActivity(intent);
        } else if (MainApplication.sharedPreferences.getBoolean(MainApplication.LOGINCLIENT, false)) {
            String data = MainApplication.sharedPreferences.getString(MainApplication.CLIENT_DATA, "");
            SaveData.listCompany = gson.fromJson(data, new TypeToken<List<Company1>>() {
            }.getType());
            onClickLoginCustomer();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(MainApplication.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra("token");
                    Log.d("register token", token);

                    //   Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(MainApplication.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(MainApplication.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };

        registerGCM();

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z'('Z')'", Locale.getDefault());
//Convert the date from the local timezone to UTC timezone
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormateInUTC = formatter.format(now);


        // Date now = new Date();
        Log.d(TAG, dateFormateInUTC);
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
    }

    private void onClickLoginCustomer() {
        Intent intent = new Intent(FirstActivity.this, CustomerLoginActivity.class);
        startActivity(intent);
    }


    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MainApplication.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MainApplication.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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
