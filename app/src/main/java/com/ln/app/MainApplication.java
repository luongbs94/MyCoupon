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
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.broadcast.ConnectivityReceiverListener;
import com.ln.model.CityOfUser;
import com.ln.realm.RealmController;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    public static final String LIST_IMAGES = "LIST_IMAGES";
    public static final String DATA = "DATA";
    public static final String PATH = "PATH";

    public static final String FACEBOOK = "facebook";
    public static final String GOOGLE = "google";

    public static final String ID_COMPANY = "company_id";
    public static final String OFF_LINE = "OFF_LINE";

    public static final String USER_NAME = "USER_NAME";
    public static final String USER_ID = "USER_ID";
    public static final String CITY_OF_USER = "CITY_OF_USER";

    public static final String ID_SHOP = "ID_SHOP";
    public static final String TOKEN_SHOP = "TOKEN_SHOP";
    public static final String COMPANY_SHOP = "COMPANY_SHOP";
    public static final String ACCOUNT_CUSTOMER = "ACCOUNT_CUSTOMER";


    public static final int ADD_COUPON_TEMPLATE = 2;
    public static final int ADD_MESSAGES = 3;
    public static final int SUCCESS = 1;
    public static final int SIZE_ID = 20;

    public static final String LOGO = "data";
    public static final int TIME_SLEEP = 2000;
    public static final int START_QRCODE = 111;
    public static final String CONTENT_COUPON = "CONTENT_COUPON";
    public static final long TIME_SLEEP_SETTING = 500;
    public static final String CROP_IMAGES = "CROP_IMAGES";
    public static final String LINK_IMAGES = "LINK_IMAGES";

    private static MainApplication mInstances;

    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    // apis normal
    private static LoveCouponAPI apiService;
    public static LoveCouponAPI apiService1;
    public static LoveCouponAPI apiService2;
    private static LoveCouponAPI apiService3;


    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String BOOL_ADD_TOKEN = "addToken";
    public static final String SHARED_PREFERENCE = "SHARED_PREFERENCE";
    public static final String LOGIN_CLIENT = "loginclient";

    public static final String CLIENT_DATA = "client_data";
    public static final String LOGIN_SHOP = "loginshop";
    public static final String SHOP_DATA = "shop_data";
    public static SharedPreferences sharedPreferences;


    public static SharedPreferences.Editor editor;

    // size images fire base
    public static final int WIDTH_IMAGES = 150;

    // information user FACEBOOK
    public static final String FACEBOOK_PROFILE = "public_profile";


    public static final String FACEBOOK_EMAIL = "email";

    private static final String URL_GET_POST = "http://188.166.199.25:3000";
    //    public static final String URL_UPDATE_IMAGE = "http://188.166.179.187:3001";
    public static final String URL_UPDATE_IMAGE = "http://188.166.196.171:3001";
    public static final String URL_GET_CITY = "http://freegeoip.net";
    public static final String URL_GET_CITY2 = "http://ip-api.com";

//    public static AccountOflUser sShopDetail;

    public static boolean sIsAdmin = false;

    public static final String FILE_URI = "file_uri";
    public static final int GOOGLE_SIGN_IN = 100;
    public static final String FIRST_BASE64 = "data:image/jpeg;base64,";
    public static final String VALUE = "VALUE";
    public static final String DURATION = "DURATION";

    public static final String COUPON_TEMpLATE_ID = "COUPON_TEMpLATE_ID";

    public static final String ID_NEWS = "idNews";
    public static final String ID_USER = "idUser";

    public static RealmController mRealmController;


    // TYPE LOGIN SHOP OR LOGIN CUSTOMER
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FACEBOOK = 2;
    public static final int TYPE_GOOGLE = 3;
    public static int TYPE_LOGIN_SHOP = TYPE_NORMAL;
    public static int TYPE_LOGIN_CUSTOMER = TYPE_FACEBOOK;


    //    public static CityOfUser cityOfUser;
    public static CityOfUser cityOfCompany;

    public static final String FONT = "fonts/fontawesome-webfont.ttf";
    //    public static final String EMAIL_LOVE_COUPON = "support@lovecoupon.com";
    public static final String EMAIL_LOVE_COUPON = "nhahv09021995@gmail.com";
    public static final String WEB_SITE_LOVE_COUPON = "http://www.lovecoupon.com:8080";


    public static final int NEWS_CUSTOMER = 0;
    public static final int NEWS_MORE = 1;

    public static boolean isEnglish;


    @Override
    public void onCreate() {
        super.onCreate();

        mInstances = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
//
//        OkHttpClient httpClient = new OkHttpClient();
//        httpClient.networkInterceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request().newBuilder().addHeader("test", "test").build();
//                return chain.proceed(request);
//            }
//        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_GET_POST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(URL_UPDATE_IMAGE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(URL_GET_CITY)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl(URL_GET_CITY2)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(LoveCouponAPI.class);
        apiService1 = retrofit1.create(LoveCouponAPI.class);
        apiService2 = retrofit2.create(LoveCouponAPI.class);
        apiService3 = retrofit3.create(LoveCouponAPI.class);

        // setup realm database
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        mRealmController = RealmController.with(this);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();


        getEnglish();

        if (isEnglish) {
            Log.d("MyApplication", isEnglish + "");
        } else {
            Log.d("MyApplication", isEnglish + "");
        }

    }

    public static SharedPreferences getPreferences() {
        return sharedPreferences;
    }

    public static String getDeviceToken() {
        return sharedPreferences.getString(DEVICE_TOKEN, "a");
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


    public static LoveCouponAPI getAPI() {
        return apiService;
    }

    public static LoveCouponAPI getAPI1() {
        return apiService1;
    }

    public static LoveCouponAPI getAPI3() {
        return apiService3;
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

    public static byte[] convertToByte(String path) {

        return Base64.decode(path, Base64.NO_WRAP);
    }


    public static String getStringNoBase64(String path) {
        return path.substring(path.indexOf(",") + 1);
    }

    public static String convertToBitmap(ImageView imageView) {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap resize = Bitmap.createScaledBitmap(bitmap, WIDTH_IMAGES, WIDTH_IMAGES, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resize.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /* net word*/
    public static synchronized MainApplication getInstance() {
        return mInstances;
    }

    public void setConnectivityListener(ConnectivityReceiverListener listener) {
        ConnectivityReceiver.mListener = listener;
    }

    public static LoveCouponAPI getApiService2() {
        return apiService2;
    }

    public static SharedPreferences getSharePrefer() {
        return sharedPreferences;
    }

    public static long dayLeft(long created_date, int duration) {

        Date last_date = convertDate(created_date, duration);

        long diff = last_date.getTime() - new Date().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
    }

    public static long dayLeft(long lastDate) {

        Date last_date = new Date(lastDate);

        long diff = last_date.getTime() - new Date().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static Date convertDate(long created_date, int duration) {

        Date date = new Date(created_date);
        Calendar last_cal = Calendar.getInstance();
        last_cal.setTime(date);
        last_cal.add(Calendar.DAY_OF_YEAR, duration);
        return last_cal.getTime();
    }

    public static boolean getLanguage() {
        return isEnglish;
    }


    private static String getEnglish() {

        String local = Locale.getDefault().getLanguage();
        if (local.equals("en")) {
            isEnglish = true;
        } else {
            isEnglish = false;
        }
        Log.d("getEnglish", local);
        return local;
    }
}
