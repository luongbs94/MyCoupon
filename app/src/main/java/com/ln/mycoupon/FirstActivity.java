package com.ln.mycoupon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.ln.model.User;
import com.ln.mycoupon.customer.CustomerLoginActivity;
import com.ln.mycoupon.customer.CustomerMainActivity;
import com.ln.mycoupon.shop.ShopLoginActivity;
import com.ln.mycoupon.shop.ShopMainActivity;
import com.ln.realm.RealmController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private LoveCouponAPI mCouponAPI;
    private RealmController mRealmController;
    private Button mBtnShop, mBtnCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mCouponAPI = MainApplication.getAPI();
        mRealmController = MainApplication.mRealmController;

        getCityOfAccount();
        initViews();
        addEvents();
        setLogin();
    }


    private void initViews() {

        setTitle(R.string.banla);
        mBtnShop = (Button) findViewById(R.id.shop);
        mBtnCustomer = (Button) findViewById(R.id.customer);
    }

    private void addEvents() {
        mBtnShop.setOnClickListener(this);
        mBtnCustomer.setOnClickListener(this);
    }

    private void onClickLoginShop() {
        Intent intent = new Intent(FirstActivity.this, ShopLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void onClickLoginCustomer() {
        Intent intent = new Intent(FirstActivity.this, CustomerLoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void startShop() {
        Intent intent = new Intent(FirstActivity.this, ShopMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startCustomer() {
        Intent intent = new Intent(FirstActivity.this, CustomerMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setLogin() {

        SharedPreferences preferences = getSharedPreferences(
                MainApplication.SHARED_PREFERENCE, MODE_PRIVATE);

        boolean isShop = preferences.getBoolean(MainApplication.LOGIN_SHOP, false);
        boolean isCustomer = preferences.getBoolean(MainApplication.LOGIN_CLIENT, false);

        Log.d(TAG, "isShop " + isShop);
        Log.d(TAG, "isCustomer " + isCustomer);

        if (isShop && !isCustomer) {

            String strCompany = MainApplication.getSharedPreferences().getString(MainApplication.COMPANY_SHOP, "");
            Company company = new Gson().fromJson(strCompany, Company.class);

            if (company != null && company.getCompany_id() != null) {

                getCouponTemplateOfShop(company.getCompany_id());
                getNewsOfShop(company.getCompany_id());
                startShop();
            }
        } else if (isCustomer && !isShop) {

            String strCompany = MainApplication.getSharedPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
            AccountOflUser account = new Gson().fromJson(strCompany, AccountOflUser.class);
            if (account != null) {
                getCompanyOfCustomer(account.getId());
                getNewsOfCustomer(account.getId());
                String city = preferences.getString(MainApplication.CITY_OF_USER, "");
                getNewsMore(account.getId(), city);
                updateUserToken(account.getAccessToken(), MainApplication.getDeviceToken(), "android");

                startCustomer();
            }
        }

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z'('Z')'", Locale.getDefault());
        //Convert the date from the local timezone to UTC timezone
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatInUTC = formatter.format(now);

        // Date now = new Date();
        Log.d(TAG, dateFormatInUTC);
    }


    /* ============= START CUSTOMER =============*/
    private void getCompanyOfCustomer(final String userId) {

        Call<List<CompanyOfCustomer>> customerLogin = mCouponAPI.getCompaniesByUserId(userId);
        customerLogin.enqueue(new Callback<List<CompanyOfCustomer>>() {
            @Override
            public void onResponse(Call<List<CompanyOfCustomer>> call, Response<List<CompanyOfCustomer>> response) {

                if (response.body() != null) {
//                    mRealmController.deleteListCompanyCustomer();
                    mRealmController.addListCompanyCustomer(response.body());

                    Log.d(TAG, "getCompanyOfCustomer + " + response.body().size());
                } else {
                    Log.d(TAG, "getCompanyOfCustomer + " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<CompanyOfCustomer>> call, Throwable t) {
                Log.d(TAG, "getCompanyOfCustomer + " + t.toString());
            }
        });
    }

    private void getNewsOfCustomer(String id) {

        Call<List<NewsOfCustomer>> newsCustomer = mCouponAPI.getNewsByUserId(id);
        newsCustomer.enqueue(new Callback<List<NewsOfCustomer>>() {
            @Override
            public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {
                if (response.body() != null) {
                    mRealmController.deleteListNewsOfCustomer();
                    mRealmController.addListNewsOfCustomer(response.body());
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

    private void updateUserToken(String userId, String token, String device_os) {

        Call<List<User>> call = MainApplication.apiService.updateUserToken(userId, token, device_os);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> arg0, Response<List<User>> arg1) {
                MainApplication.setIsAddToken(true);
            }

            @Override
            public void onFailure(Call<List<User>> arg0, Throwable arg1) {
                Log.d(TAG, "updateUserToken " + "Failure");
            }
        });
    }

    private void getNewsMore(String id, String city) {
        Call<List<NewsOfMore>> newsMore = mCouponAPI.getNewsMoreByUserId(id, city);
        newsMore.enqueue(new Callback<List<NewsOfMore>>() {
            @Override
            public void onResponse(Call<List<NewsOfMore>> call, Response<List<NewsOfMore>> response) {
                if (response.body() != null) {
                    mRealmController.deleteListNewsOfMore();
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


    /* ================ START CUSTOMER ===========*/

    private void getCouponTemplateOfShop(String idCompany) {

        //  Call<List<CouponTemplate>> call = mApiServices.getCouponTemplates(SaveData.web_token, SaveData.company.getCompany_id());
        Call<List<CouponTemplate>> couponShop = mCouponAPI.getCouponTemplates("abc", idCompany);

        couponShop.enqueue(new Callback<List<CouponTemplate>>() {
            @Override
            public void onResponse(Call<List<CouponTemplate>> call, Response<List<CouponTemplate>> response) {
                if (response.body() != null) {
                    mRealmController.deleteCouponTemplate();
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
                    mRealmController.deleteListNewsOfCompany();
                    mRealmController.addListNewsOfCompany(response.body());
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
    /* ================ END CUSTOMER   ===========*/

    private void getCityOfAccount() {
        Call<CityOfUser> call = MainApplication.getApiService2().getCityOfUser();
        call.enqueue(new Callback<CityOfUser>() {
            @Override
            public void onResponse(Call<CityOfUser> call, Response<CityOfUser> response) {

                if (response.body() != null) {

                    String cityOfUser = new Gson().toJson(response.body());
                    SharedPreferences preferences =
                            getSharedPreferences(MainApplication.SHARED_PREFERENCE, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(MainApplication.CITY_OF_USER, cityOfUser);
                    editor.apply();

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
