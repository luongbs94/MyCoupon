package com.ln.mycoupon;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.model.Company1;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by luongnguyen on 4/1/16.
 */
public class MainApplication extends MultiDexApplication {

    public static LoveCouponAPI apiService;
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    public static LoveCouponAPI apiService1;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String BOOL_ADD_TOKEN = "addToken";
    public static final String SHAREDPRE = "sharePre";
    public static final String LISTCOMPANY = "listcompany";
    public static final String LISTCOUPON = "listCoupon";
    public static final String LOGINCOMPANY = "logincompany";
    public static final String LOGINSHOP = "loginshop";



    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    Gson gson = new Gson();


// server_api_key: AIzaSyBuchLzuoZfJ_f6Iuf145SMb9uDfNNS-mI
// Sender ID help: 87052112933


    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://103.7.40.171:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("http://103.7.40.171:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(LoveCouponAPI.class);

        apiService1 = retrofit1.create(LoveCouponAPI.class);

        sharedPreferences = getSharedPreferences(SHAREDPRE, 4);
        editor = sharedPreferences.edit();

        if(isNetworkAvailable(getApplicationContext())){
            getCompanyByUserId();
        }else{
            String jsonListCompany = sharedPreferences.getString(LISTCOMPANY, "");
            if(jsonListCompany.length() > 0) {
                SaveData.listCompany = gson.fromJson(jsonListCompany, new TypeToken<List<Company1>>(){}.getType());
            }
        }

    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static String getDeviceToken() {
        String token = sharedPreferences.getString(DEVICE_TOKEN, "a");
        return token;
    }

    public static boolean isAddToken() {
        return sharedPreferences.getBoolean(BOOL_ADD_TOKEN, false);
    }

    public static void setDeviceToken(String deviceToken) {
        editor.putString(DEVICE_TOKEN, deviceToken);
        editor.commit();
    }

    public static void setIsAddToken(boolean isAddToken) {
        editor.putBoolean(BOOL_ADD_TOKEN, isAddToken);
        editor.commit();
    }

    public void getCompanyByUserId() {

        Call<List<Company1>> call3 = apiService1.getCompaniesByUserId("10205539341392320");
        call3.enqueue(new Callback<List<Company1>>() {

            @Override
            public void onResponse(Call<List<Company1>> arg0,
                                   Response<List<Company1>> arg1) {
                List<Company1> templates = arg1.body();

                SaveData.listCompany = templates;
                String jsonListCompany = gson.toJson(templates);

                editor.putString(LISTCOMPANY, jsonListCompany);
                editor.commit();

            }

            @Override
            public void onFailure(Call<List<Company1>> arg0, Throwable arg1) {
                // TODO Auto-generated method stub

            }
        });


    }

    public static String getCompanyName(String company_id) {
        for (int i = 0; i < SaveData.listCompany.size(); i++) {

            Company1 company1 = SaveData.listCompany.get(i);

            Log.d("Coupon", company1.getCompany_id());
            if (company_id.equals(company1.getCompany_id())) {
                return company1.getName();
            }
        }

        return "No company found";

    }

    public static LoveCouponAPI getAPI() {
        return apiService;
    }

    public static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }


    public static boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
