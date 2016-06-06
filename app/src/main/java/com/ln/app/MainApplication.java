package com.ln.app;

import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.model.Company1;
import com.ln.model.Coupon;

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

    public static final String POSITION = "POSITION";
    public static final String DATA = "DATA";
    public static final String LIST_IMAGES = "LIST_IMAGES";

    public static final String URL_FIRE_BASE = "https://nhahv-fire-chat.firebaseio.com/users";

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


    public static final String PATH = "path";

    // size images firebase
    public static final int WIDTH_IMAGES = 200;

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Retrofit retrofit = new Retrofit.Builder()
                //      .baseUrl("http://192.168.1.6:3000")
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

        getCompanyByUserId();
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
                System.out.println(templates.size());

                SaveData.listCompany = templates;


                Company1 company1 = templates.get(0);
                Coupon coupon = company1.getCoupon().get(0);
                String value = coupon.getValue();
                String company_id = coupon.getCompany_id();
//                Date date = coupon.getCreated_date();

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

    public static byte[] convertToBytes(String path) {
        path = path.substring(path.indexOf(",") + 1);
        return Base64.decode(path, Base64.NO_WRAP);
    }
}
