package com.ln.app;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.model.Company1;
import com.ln.model.DetailUser;
import com.ln.realm.RealmController;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by luongnguyen on 4/1/16.
 * <></>
 */
public class MainApplication extends MultiDexApplication {

    public static final String POSITION = "POSITION";
    public static final String DATA = "DATA";
    public static final String LIST_IMAGES = "LIST_IMAGES";

    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";


    public static LoveCouponAPI apiService;
    public static LoveCouponAPI apiService1;

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String BOOL_ADD_TOKEN = "addToken";
    public static final String SHAREDPRE = "sharePre";

    public static final String LOGINCLIENT = "loginclient";
    public static final String CLIENT_DATA = "client_data";
    public static final String LOGINSHOP = "loginshop";
    public static final String SHOP_DATA = "shop_data";


    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;


    public static final String PATH = "path";

    // size images firebase
    public static final int WIDTH_IMAGES = 450;


    // information user FACEBOOK
    public static final String FACEBOOK_PROFILE = "public_profile";
    public static final String FACEBOOK_EMAIL = "email";
    public static DetailUser sDetailUser;
    public static final String IMAGE_FACEBOOK = "https://graph.facebook.com/";
    public static final String IMAGE_FACEBOOK_END = "/picture?type=large";

    public static final String URL_UPDATE_IMAGE = "http://188.166.179.187:3001";

    // login with // STOPSHIP: 6/18/2016
    public static DetailUser sShopDetail;

    public static boolean sIsAdmin = false;

    // id company when logout
    public static int sIdCompany;


    public static final String FILE_URI = "file_uri";
    public static final int GOOGLE_SIGN_IN = 100;
    public static final String FIRST_BASE64 = "data:image/jpeg;base64,";
    public static final String VALUE = "VALUE";
    public static final String DURATION = "DURATION";

    public static final String COUPON_TEMpLATE_ID = "COUPON_TEMpLATE_ID";


    // size screen

    public static float WIDTH_SCREEN;
    public static float HEIGHT_SCREEN;

    public static final String ID_NEWS = "idNews";
    public static final String ID_USER = "idUser";

    public static RealmController mRealmController;


    // login shop
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FACEBOOK = 2;
    public static final int TYPE_GOOGLE = 3;
    public static int TYPE_LOGIN_SHOP = TYPE_NORMAL;

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://188.166.179.187:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(URL_UPDATE_IMAGE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(LoveCouponAPI.class);

        apiService1 = retrofit1.create(LoveCouponAPI.class);

        sharedPreferences = getSharedPreferences(SHAREDPRE, 4);
        editor = sharedPreferences.edit();


        // setup realm database
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        mRealmController = RealmController.with(this);

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


    public static String getCompanyName(String company_id) {
        for (int i = 0; i < SaveData.listCompanyCustomer.size(); i++) {

            Company1 company1 = SaveData.listCompanyCustomer.get(i);

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

    public static LoveCouponAPI getAPI1() {
        return apiService1;
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

    public static String getStringNoBase64(String path) {
        return path.substring(path.indexOf(",") + 1);
    }

    public static String convertToBitmap(ImageView imageView) {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }


}
