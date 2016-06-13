package com.ln.mycoupon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import com.ln.mycoupon.customer.CustomerMainActivity;
import com.ln.mycoupon.shop.ShopLoginActivity;
import com.ln.mycoupon.shop.ShopMainActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ln.mycoupon",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        setTitle(R.string.banla);

        mBtnShop = (Button) findViewById(R.id.shop);
        mBtnCustomer = (Button) findViewById(R.id.customer);

        mBtnShop.setOnClickListener(new Events());
        mBtnCustomer.setOnClickListener(new Events());

        if (MainApplication.sharedPreferences.getBoolean(MainApplication.LOGINSHOP, false)) {

            String data = MainApplication.sharedPreferences.getString(MainApplication.SHOP_DATA, "");
            SaveData.company = gson.fromJson(data, Company.class);
            Intent intent = new Intent(FirstActivity.this, ShopMainActivity.class);
            startActivity(intent);
        } else if (MainApplication.sharedPreferences.getBoolean(MainApplication.LOGINCLIENT, false)) {
            String data = MainApplication.sharedPreferences.getString(MainApplication.CLIENT_DATA, "");
            SaveData.listCompany = gson.fromJson(data, new TypeToken<List<Company1>>() {
            }.getType());
            start();
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z'('Z')'");
//Convert the date from the local timezone to UTC timezone
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormateInUTC = formatter.format(now);


        // Date now = new Date();
        Log.d(TAG, dateFormateInUTC);
    }

    private void startLogin() {
        Intent intent = new Intent(FirstActivity.this, ShopLoginActivity.class);
        startActivity(intent);
    }

    private void start() {
        Intent intent = new Intent(FirstActivity.this, CustomerMainActivity.class);
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
                    startLogin();
                    break;
                case R.id.customer:
                    onClickCustomer();
                    break;
                default:
                    break;
            }
        }

        private void onClickCustomer() {
            startActivity(new Intent(FirstActivity.this, CustomerLoginActivity.class));
        }
    }
}
