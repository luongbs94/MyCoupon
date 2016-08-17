package com.ln.mycoupon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.CityOfUser;
import com.ln.model.Company;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.CouponTemplate;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCustomer;
import com.ln.model.NewsOfMore;
import com.ln.mycoupon.customer.CustomerLoginActivity;
import com.ln.mycoupon.customer.CustomerMainActivity;
import com.ln.mycoupon.shop.ShopLoginActivity;
import com.ln.mycoupon.shop.ShopMainActivity;
import com.ln.realm.RealmController;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private LoveCouponAPI mCouponAPI;
    private RealmController mRealmController;
    private int mStartNotification = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mCouponAPI = MainApplication.getAPI();
        mRealmController = RealmController.with(this);

        getCityOfAccount();
        getDataFromIntent();
        initViews();
        setLogin();
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
            AccountOflUser account = new Gson().fromJson(strAccount, AccountOflUser.class);
            if (account != null) {
                startCustomer();
                getCompanyOfCustomer(account.getId());
                getNewsOfCustomer(account.getId());
                String city = preferences.getString(MainApplication.CITY_OF_USER, "");
                getNewsMore(account.getId(), city);
            }
        }
    }

    /* ============= START CUSTOMER =============*/
    private void getCompanyOfCustomer(final String userId) {

        Call<List<CompanyOfCustomer>> customerLogin
                = mCouponAPI.getCompaniesByUserId(userId);
        customerLogin.enqueue(new Callback<List<CompanyOfCustomer>>() {
            @Override
            public void onResponse(Call<List<CompanyOfCustomer>> call,
                                   Response<List<CompanyOfCustomer>> response) {

                if (response.body() != null) {
                    mRealmController.addListCompanyCustomer(response.body());
                    preLoadImageShopOfCustomer();
                    Log.d(TAG, "getCompanyOfCustomer + " + response.body().size());
                } else {
                    Log.d(TAG, "getCompanyOfCustomer + " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<CompanyOfCustomer>> call, Throwable t) {
                Logger.d("getCompanyOfCustomer + " + t.toString());
            }
        });
    }

    private void getNewsOfCustomer(String id) {

        Call<List<NewsOfCustomer>> newsCustomer = mCouponAPI.getNewsByUserId(id);
        newsCustomer.enqueue(new Callback<List<NewsOfCustomer>>() {
            @Override
            public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {
                if (response.body() != null) {
                    mRealmController.addListNewsOfCustomer(response.body());
                    previousLoadImagesCustomer();
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
        Call<List<NewsOfMore>> newsMore = mCouponAPI.getNewsMoreByUserId(id, city);
        newsMore.enqueue(new Callback<List<NewsOfMore>>() {
            @Override
            public void onResponse(Call<List<NewsOfMore>> call, Response<List<NewsOfMore>> response) {
                if (response.body() != null) {
                    mRealmController.addListNewsOfMore(response.body());
                    Log.d(TAG, " getNewsMore " + response.body().size());
                } else {
                    Log.d(TAG, " getNewsMore " + " null");
                }
            }

            @Override
            public void onFailure(Call<List<NewsOfMore>> call, Throwable t) {
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
                    mRealmController.addListCouponTemplate(response.body());
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

    private void getNewsOfShop(String idCompany) {

        Call<List<NewsOfCompany>> newsCompany = mCouponAPI.getNewsByCompanyId(idCompany);
        newsCompany.enqueue(new Callback<List<NewsOfCompany>>() {
            @Override
            public void onResponse(Call<List<NewsOfCompany>> call, Response<List<NewsOfCompany>> response) {
                if (response.body() != null) {
                    mRealmController.addListNewsOfCompany(response.body());
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
        Call<CityOfUser> call = MainApplication.getApiService2().getCityOfUser();
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

    private void previousLoadImagesCustomer() {
        List<NewsOfCustomer> listNews = new ArrayList<>();
        listNews.addAll(RealmController.with(MainApplication.getInstance()).getListNewsOfCustomer());
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
        listNews.addAll(RealmController.with(MainApplication.getInstance()).getListNewsOfCompany());
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

    private void preLoadImageShopOfCustomer() {
        List<CompanyOfCustomer> listShop = new ArrayList<>();
        listShop.addAll(RealmController.with(MainApplication.getInstance()).getListCompanyCustomer());
        for (CompanyOfCustomer company : listShop) {
            if (company.getLogo_link() != null
                    && company.getLogo_link().contains("http")) {
                Glide.with(MainApplication.getInstance())
                        .load(company.getLogo_link())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .preload();
            }
        }
    }
}
