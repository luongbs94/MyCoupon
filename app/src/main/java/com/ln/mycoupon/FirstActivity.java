package com.ln.mycoupon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.images.models.ImagesManager;
import com.ln.model.AccountOfUser;
import com.ln.model.CityOfUser;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.model.NewMore;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCustomer;
import com.ln.mycoupon.customer.CustomerLoginActivity;
import com.ln.mycoupon.customer.CustomerMainActivity;
import com.ln.mycoupon.shop.ShopLoginActivity;
import com.ln.mycoupon.shop.ShopMainActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private LoveCouponAPI mCouponAPI;
    private int mStartNotification = 1;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int ZXING_CAMERA_PERMISSION = 2;
    private static final int GET_ACCOUNTS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ln.mycoupon",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.d(TAG, e.toString());
        }

        mCouponAPI = MainApplication.getAPI();

//        getNewsOfShop("1k2Bqu67UvqDon1");

        getCityOfAccount();
        getDataFromIntent();
        initViews();
        setLogin();


        String token = MainApplication.getPreferences().getString(MainApplication.DEVICE_TOKEN, "");
        Log.d(TAG, token);

        String permissions[] = new String[]{};

        if (!checkPermission()) {
            requestPermission();
        } else {
            ImagesManager.getInstances(this);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.GET_ACCOUNTS}, GET_ACCOUNTS);
        }
    }


    private void initViews() {
        findViewById(R.id.shop).setOnClickListener(this);
        findViewById(R.id.customer).setOnClickListener(this);
    }

    private void onClickLoginShop() {
        Intent intent = new Intent(FirstActivity.this, ShopLoginActivity.class);
        intent.putExtra(MainApplication.PUSH_NOTIFICATION, mStartNotification);
        startActivity(intent);
        finish();
    }

    private void onClickLoginCustomer() {
        Intent intent = new Intent(FirstActivity.this, CustomerLoginActivity.class);
        intent.putExtra(MainApplication.PUSH_NOTIFICATION, mStartNotification);
        startActivity(intent);
        finish();
    }

    private void startShop() {
        Intent intent = new Intent(FirstActivity.this, ShopMainActivity.class);
        intent.putExtra(MainApplication.PUSH_NOTIFICATION, mStartNotification);
        startActivity(intent);
        finish();
    }

    private void startCustomer() {
        Intent intent = new Intent(FirstActivity.this, CustomerMainActivity.class);
        intent.putExtra(MainApplication.PUSH_NOTIFICATION, mStartNotification);
        startActivity(intent);
        finish();
    }

    private void setLogin() {

        SharedPreferences preferences = MainApplication.getPreferences();

        boolean isShop = preferences.getBoolean(MainApplication.LOGIN_SHOP, false);
        boolean isCustomer = preferences.getBoolean(MainApplication.LOGIN_CLIENT, false);

        Log.d(TAG, "isShop " + isShop);
        Log.d(TAG, "isCustomer " + isCustomer);

        if (isShop && !isCustomer) {

            String strCompany = preferences.getString(MainApplication.COMPANY_SHOP, "");
            Company company = new Gson().fromJson(strCompany, Company.class);

            if (company != null && company.getCompany_id() != null) {

                startShop();
                getCouponTemplateOfShop(company.getCompany_id());
                getNewsOfShop(company.getCompany_id());
            }
        } else if (isCustomer && !isShop) {

            String strAccount = preferences.getString(MainApplication.ACCOUNT_CUSTOMER, "");
            AccountOfUser account = new Gson().fromJson(strAccount, AccountOfUser.class);
            if (account != null) {
                startCustomer();
//                getCompanyOfCustomer(account.getId());
                getNewsOfCustomer(account.getId());
                String city = preferences.getString(MainApplication.CITY_OF_USER, "");
                getNewsMore(account.getId(), city);
            }
        }
    }

    private void getNewsOfCustomer(String id) {

        Call<List<NewsOfCustomer>> newsCustomer = mCouponAPI.getNewsByUserId(id);
        newsCustomer.enqueue(new Callback<List<NewsOfCustomer>>() {
            @Override
            public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {
                if (response.body() != null) {

                    String account = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
                    String user = new Gson().fromJson(account, AccountOfUser.class).getId();
                    DatabaseManager.addListNewsOfCustomer(response.body(), user);
                    preLoadImagesCustomer();
                    Log.d(TAG, "List NewsOfCustomer " + response.body().size());
                } else {
                    Log.d(TAG, "List NewsOfCustomer " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<NewsOfCustomer>> call, Throwable t) {
                Log.d(TAG, "getNewsOfCustomer" + "onFailure " + t.toString());
            }
        });
    }

    private void getNewsMore(String id, String city) {
        Call<List<NewMore>> newsMore = mCouponAPI.getNewsMoreByUserId(id, city);

        newsMore.enqueue(new Callback<List<NewMore>>() {
            @Override
            public void onResponse(Call<List<NewMore>> call, Response<List<NewMore>> response) {
                if (response.body() != null) {
                    preLoadImageNewMore(response.body());
                    String account = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
                    String user = new Gson().fromJson(account, AccountOfUser.class).getId();
                    DatabaseManager.addListNewMore(response.body(), user);
                    Log.d(TAG, " getNewsMore " + response.body().size());
                } else {
                    Log.d(TAG, " getNewsMore " + " null");
                }
            }

            @Override
            public void onFailure(Call<List<NewMore>> call, Throwable t) {
                Log.d(TAG, "getNewsMore " + " onFailure " + t.toString());

            }
        });

    }
    /* ============================ END CUSTOMER ==============*/


    /* ================ START SHOP ===========*/
    private void getCouponTemplateOfShop(String idCompany) {

        Call<List<CouponTemplate>> couponShop = mCouponAPI.getCouponTemplates("abc", idCompany);

        couponShop.enqueue(new Callback<List<CouponTemplate>>() {
            @Override
            public void onResponse(Call<List<CouponTemplate>> call, Response<List<CouponTemplate>> response) {
                if (response.body() != null) {
                    DatabaseManager.addListCouponTemplate(response.body());
                    Log.d(TAG, "getCouponTemplateOfShop " + response.body().size());
                } else {
                    Log.d(TAG, "getCouponTemplateOfShop " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<CouponTemplate>> call, Throwable t) {
                Log.d(TAG, "getCouponTemplateOfShop " + "onFailure " + t.toString());
            }
        });
    }

    private void getNewsOfShop(final String idCompany) {

        Call<List<NewsOfCompany>> newsCompany = mCouponAPI.getNewsByCompanyId(idCompany);
        newsCompany.enqueue(new Callback<List<NewsOfCompany>>() {
            @Override
            public void onResponse(Call<List<NewsOfCompany>> call, Response<List<NewsOfCompany>> response) {
                if (response.body() != null) {
                    DatabaseManager.addListNewsOfCompany(response.body(), idCompany);
                    preLoadImageShop();
                    Log.d(TAG, "getNewsOfShop " + response.body().size());
                } else {
                    Log.d(TAG, "getNewsOfShop " + "null ");
                }
            }

            @Override
            public void onFailure(Call<List<NewsOfCompany>> call, Throwable t) {
                Log.d(TAG, "getNewsOfShop " + "onFailure " + t.toString());
            }
        });
    }
    /* ================ END SHOP   ===========*/

    private void getCityOfAccount() {
        Call<CityOfUser> call = MainApplication.getAPI2().getCityOfUser();
        call.enqueue(new Callback<CityOfUser>() {
            @Override
            public void onResponse(Call<CityOfUser> call, Response<CityOfUser> response) {

                if (response.body() != null) {

                    String cityOfUser = response.body().getCity();
                    if (!cityOfUser.isEmpty()) {
                        writeSharePreferences(MainApplication.CITY_OF_USER, cityOfUser);
                    } else {
                        getCityOfAccount1();
                    }
                    Log.d(TAG, "City : " + response.body().getCity());
                } else {
                    Log.d(TAG, "City : " + "Khong co du lieu");
                }
            }

            @Override
            public void onFailure(Call<CityOfUser> call, Throwable t) {
                Log.d(TAG, "City Error : " + t.toString());
            }
        });
    }

    private void getCityOfAccount1() {
        Call<CityOfUser> call = MainApplication.getAPI3().getCityOfUser2();
        call.enqueue(new Callback<CityOfUser>() {
            @Override
            public void onResponse(Call<CityOfUser> call, Response<CityOfUser> response) {

                if (response.body() != null) {

                    String cityOfUser = response.body().getCity();
                    if (!cityOfUser.isEmpty()) {
                        writeSharePreferences(MainApplication.CITY_OF_USER, cityOfUser);
                    }
                    Log.d(TAG, "City : " + response.body().getCity());
                } else {
                    Log.d(TAG, "City : " + "Khong co du lieu");
                }
            }

            @Override
            public void onFailure(Call<CityOfUser> call, Throwable t) {
                Log.d(TAG, "City Error : " + t.toString());
            }
        });
    }

    private void writeSharePreferences(String key, String value) {
        SharedPreferences preferences = MainApplication.getPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


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

    public void getDataFromIntent() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                mStartNotification = intent.getIntExtra(MainApplication.PUSH_NOTIFICATION, 1);
            }

        } catch (NullPointerException e) {
            Log.d(TAG, "intent null" + e.toString());
        }
    }

    private void preLoadImagesCustomer() {

        String str = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
        AccountOfUser account = new Gson().fromJson(str, AccountOfUser.class);
        List<NewsOfCustomer> listNews = new ArrayList<>();
        listNews.addAll(DatabaseManager.getListNewsOfCustomer(account.getId()));
        for (NewsOfCustomer news : listNews) {
            if (news.getLogo_link() != null && news.getLogo_link().contains("http")) {
                Glide.with(MainApplication.getInstance())
                        .load(news.getLogo_link())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .preload();

                if (news.getImages_link() != null) {
                    String strImages = news.getImages_link();
                    String[] listStrImages = strImages.split(";");
                    for (String path : listStrImages) {
                        Glide.with(MainApplication.getInstance())
                                .load(path)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .preload();
                    }
                }
            }
        }
    }

    private void preLoadImageShop() {
        List<NewsOfCompany> listNews = new ArrayList<>();
        listNews.addAll(DatabaseManager.getListNewsOfCompany());
        for (NewsOfCompany news : listNews) {
            if (news.getImages_link() != null) {
                String strImages = news.getImages_link();
                String[] listStrImages = strImages.split(";");
                for (String path : listStrImages) {
                    Glide.with(MainApplication.getInstance())
                            .load(path)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .preload();
                }
            }
        }
    }

    private void preLoadImageNewMore(List<NewMore> news) {
        for (NewMore item : news) {
            if (item.getLogo_link() != null && item.getLogo_link().contains("http")) {
                Glide.with(MainApplication.getInstance())
                        .load(item.getLogo_link())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .preload();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(getApplicationContext(), "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void showMessage(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "Permission Granted, Now you can access storage data.");
                } else {
                    showMessage(R.string.grant_read_storage);
                }
                break;
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission Granted, Now you can access location data.");
                } else {
                    showMessage(R.string.grant_camera);
                }
                break;
            case GET_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission Granted, Now you can access location data.");
                } else {
                    showMessage(R.string.grant_get_account);
                }
                break;
            default:
                break;
        }
    }
}
