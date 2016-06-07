package com.ln.mycoupon;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.gcm.GcmIntentService;
import com.ln.model.Company;
import com.ln.model.Company1;
import com.ln.model.User;
import com.ln.mycoupon.customer.CustomerMainActivity;
import com.ln.mycoupon.shop.ShopMainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstActivity extends AppCompatActivity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    LoveCouponAPI apiService;
    String TAG = "Coupon";

    Gson gson = new Gson();
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_first);
        setTitle(R.string.banla);

        apiService = MainApplication.getAPI();


        Button shop = (Button) findViewById(R.id.shop);
        Button custom = (Button) findViewById(R.id.customer);

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLogin();
            }
        });

        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getCompanyByUserId();
                progressDialog = ProgressDialog.show(FirstActivity.this, "Please wait ...",  "Task in progress ...", true);
                progressDialog.setCancelable(true);

             //   if(MainApplication.isAddToken() == false && MainApplication.getDeviceToken().length() > 5){
                    updateUserToken("10205539341392320", MainApplication.getDeviceToken(), "android");
             //   }
            }
        });

        if(MainApplication.sharedPreferences.getBoolean(MainApplication.LOGINSHOP, false)){

            String data = MainApplication.sharedPreferences.getString(MainApplication.SHOP_DATA, "");
            SaveData.company = gson.fromJson(data, Company.class);
            Intent intent = new Intent(FirstActivity.this, ShopMainActivity.class);
            startActivity(intent);
        }else if(MainApplication.sharedPreferences.getBoolean(MainApplication.LOGINCLIENT, false)){
            String data = MainApplication.sharedPreferences.getString(MainApplication.CLIENT_DATA, "");
            SaveData.listCompany = gson.fromJson(data, new TypeToken<List<Company1>>(){}.getType());
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
        Log.d("Coupon", dateFormateInUTC);
    }

    public void startLogin(){
        Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void start(){
        Intent intent = new Intent(FirstActivity.this, CustomerMainActivity.class);
        startActivity(intent);
    }

    public void getCompanyByUserId() {

        Call<List<Company1>> call3 = MainApplication.apiService1.getCompaniesByUserId("10205539341392320");
        call3.enqueue(new Callback<List<Company1>>() {

            @Override
            public void onResponse(Call<List<Company1>> arg0,
                                   Response<List<Company1>> arg1) {
                List<Company1> templates = arg1.body();
                System.out.println(templates.size());
                SaveData.listCompany = templates;

                String data = gson.toJson(SaveData.listCompany);
                MainApplication.editor.putBoolean(MainApplication.LOGINSHOP, false);
                MainApplication.editor.putBoolean(MainApplication.LOGINCLIENT, true);
                MainApplication.editor.putString(MainApplication.CLIENT_DATA, data);
                MainApplication.editor.commit();

                progressDialog.dismiss();

                start();
            }

            @Override
            public void onFailure(Call<List<Company1>> arg0, Throwable arg1) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
            }
        });

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

    public void updateUserToken(String userId,String token, String device_os){
        Call<List<User>> call = apiService.updateUserToken(userId, token, device_os);

        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> arg0,
                                   Response<List<User>> arg1) {

                MainApplication.setIsAddToken(true);
            }

            @Override
            public void onFailure(Call<List<User>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");

            }
        });
    }
}
