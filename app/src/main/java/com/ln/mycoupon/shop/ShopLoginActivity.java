package com.ln.mycoupon.shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.CityOfUser;
import com.ln.model.Company;
import com.ln.model.CompanyLocation;
import com.ln.model.CouponTemplate;
import com.ln.model.NewsOfCompany;
import com.ln.mycoupon.R;
import com.ln.realm.RealmController;

import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 3/30/16.
 * login shop
 */

public class ShopLoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private String TAG = getClass().getSimpleName();


    private Button mBtnLogin;
    private Button mBtnLoginFacebook;
    private EditText mEdtUsername, mEdtPassword;
    private LoveCouponAPI apiService;
    private GoogleApiClient mGoogleApiClient;

    private Button mBtnGooglePlus;
    private CallbackManager mCallbackManager;

    private LinearLayout mLinearLayout;
    private LoveCouponAPI mCouponAPI;
    private LoveCouponAPI mCouponAPI2;

    private CompanyLocation mCompanyLocation;
    private RealmController mRealmController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        mCouponAPI = MainApplication.getAPI();
        mCouponAPI2 = MainApplication.getApiService2();
        mRealmController = MainApplication.mRealmController;

        setContentView(R.layout.activity_shop_login);


        initViews();
        addEvents();
    }

    private void initViews() {

        apiService = MainApplication.getAPI();

        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEdtUsername = (EditText) findViewById(R.id.username);
        mEdtPassword = (EditText) findViewById(R.id.password);

        /* ================== START FACEBOOK ==================*/

        mBtnLoginFacebook = (Button) findViewById(R.id.btn_login_facebook);
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_login_shop);

        /* ===================== END FACEBOOK ====================*/

        /*=============== START GOOGLE ===========*/

        GoogleSignInOptions mInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mInOptions)
                .build();

        mBtnGooglePlus = (Button) findViewById(R.id.btn_google);
        /* ===================== END GOOGLE ==================*/

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                Profile mProfile = Profile.getCurrentProfile();

                String id = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();

                Log.d(TAG, "mProfile " + id + " - " + token);

                AccountOflUser accountOflUser = new AccountOflUser();
                if (id != null) {
                    String url = getString(R.string.face_image) + id + getString(R.string.face_image_end);
                    accountOflUser.setId(id);
                    accountOflUser.setPicture(url);

                }
                if (token != null) {
                    accountOflUser.setAccessToken(token);
                }
                if (mProfile != null) {
                    if (mProfile.getName() != null) {
                        accountOflUser.setName(mProfile.getName());
                    }
                }


                try {
                    if (accountOflUser.getId() != null) {
                        MainApplication.sShopDetail = accountOflUser;
                        getCompanyProfileSocial(accountOflUser.getId());
                        LoginManager.getInstance().logOut();
                    }

                } catch (NullPointerException e) {
                    Log.d(TAG, "Login Facebook  error");
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "FACEBOOK - onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "FACEBOOK - onError");
            }
        });
    }


    private void addEvents() {

        mBtnLogin.setOnClickListener(new Events());
        mBtnGooglePlus.setOnClickListener(new Events());
        mBtnLoginFacebook.setOnClickListener(new Events());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == MainApplication.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                loginGoogleSuccess(account);
            } else {
                getSnackBar(getString(R.string.login_google_fails));
            }
        }

    }

    private void getCompanyProfile(final String user, final String pass) {

        Call<List<Company>> call = apiService.getCompanyProfile(user, pass, null);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {

                List<Company> templates = response.body();
                SaveData.company = templates.get(0);
                loginSuccess(templates.get(0));
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {
                getSnackBar(getString(R.string.login_fails));
            }
        });
    }


    private void getCompanyProfileSocial(String user_id) {


        Call<List<Company>> call = apiService.getCompanyProfileSocial(user_id);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {

                List<Company> templates = response.body();
                SaveData.company = templates.get(0);
                loginSuccess(templates.get(0));
            }

            @Override
            public void onFailure(Call<List<Company>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }

    private void loginSuccess(Company company) {
        mRealmController.saveAccountShop(company);

        MainApplication.sIdCompany = SaveData.company.getCompany_id();
//
//                Gson gson = new Gson();
//
//                String data = gson.toJson(SaveData.company);
//                MainApplication.editor.putBoolean(MainApplication.LOGIN_SHOP, true);
//                MainApplication.editor.putBoolean(MainApplication.LOGIN_CLIENT, false);
//                MainApplication.editor.putString(MainApplication.SHOP_DATA, data);
//                MainApplication.editor.commit();


        SharedPreferences preferences = getSharedPreferences(
                MainApplication.SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainApplication.LOGIN_SHOP, true);
        editor.putBoolean(MainApplication.LOGIN_CLIENT, false);
        editor.putBoolean(MainApplication.OFF_LINE, true);
        editor.apply();

        getCityOfUser();        // get address of company
        getNewsByCompanyId();   // get list news of company
        getCouponTemplate();    //  get list coupon template of company

        Intent intent = new Intent(ShopLoginActivity.this, ShopMainActivity.class);
        startActivity(intent);

        finish();
    }

    private void getSnackBar(String string) {
        Snackbar.make(mLinearLayout, string, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // integrator login google save state google login
    private void loginGoogleSuccess(GoogleSignInAccount account) {

        MainApplication.sShopDetail = new AccountOflUser(account.getId(), account.getEmail(), "", account.getIdToken());
        if (account.getPhotoUrl() != null) {
            MainApplication.sShopDetail.setPicture(account.getPhotoUrl().toString());
        }
        getSnackBar("Login Google Success ");
        Log.d(TAG, "Login Google " + account.getId() + " - " + account.getEmail());
        MainApplication.TYPE_LOGIN_SHOP = MainApplication.TYPE_GOOGLE;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionListener + " + connectionResult);
    }

    public void onClickLogoutGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {

                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.d(TAG, "Logout Google ");
                    }
                });
    }

    private class Events implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_login:
                    onClickLogin();
                    break;
                case R.id.btn_login_facebook:
                    onClickLoginFacebook();
                    break;
                case R.id.btn_google:
                    onClickGooglePlus();
                    break;
                default:
                    break;
            }
        }

        private void onClickLogin() {

            String str_user = mEdtUsername.getText().toString();
            String str_password = mEdtPassword.getText().toString();

            if (str_user.length() > 0 && str_password.length() > 0) {
                getCompanyProfile(str_user, str_password);
                MainApplication.TYPE_LOGIN_SHOP = MainApplication.TYPE_NORMAL;
            } else {
                getSnackBar(getString(R.string.not_fill_login));
            }
        }

        private void onClickGooglePlus() {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(intent, MainApplication.GOOGLE_SIGN_IN);
        }

        private void onClickLoginFacebook() {
            LoginManager.getInstance().logInWithReadPermissions(ShopLoginActivity.this,
                    Arrays.asList(MainApplication.FACEBOOK_PROFILE, MainApplication.FACEBOOK_EMAIL));
        }

    }

    private void updateCompanyLocation() {
        Call<ResponseBody> call = mCouponAPI.updateCompanyLocation(mCompanyLocation);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "updateCompanyLocation Success : ");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "updateCompanyLocation Error : " + t.toString());
            }
        });

    }


    private void getCityOfUser() {
        Call<CityOfUser> call = mCouponAPI2.getCityOfUser();
        call.enqueue(new Callback<CityOfUser>() {
            @Override
            public void onResponse(Call<CityOfUser> call, Response<CityOfUser> response) {
                if (response.body() != null) {
                    MainApplication.cityOfCompany = response.body();

                    Log.d(TAG, "City : " + MainApplication.cityOfCompany.getCity());
                    if (SaveData.company != null) {
                        mCompanyLocation = new CompanyLocation(SaveData.company.getCompany_id(),
                                MainApplication.cityOfCompany.getCity(),
                                MainApplication.cityOfCompany.getCountry_name());

                        updateCompanyLocation();
                    }

                }
                Log.d(TAG, "City : " + "Khong co du lieu");
            }

            @Override
            public void onFailure(Call<CityOfUser> call, Throwable t) {

                Log.d(TAG, "City Error : " + t.toString());
            }
        });
    }

    /* =============== Get list coupon of company ==============*/
    private void getNewsByCompanyId() {

        String idCompany;
        if (SaveData.company == null) {
            idCompany = MainApplication.sIdCompany;
        } else {
            idCompany = SaveData.company.getCompany_id();
        }

        Call<List<NewsOfCompany>> call = mCouponAPI.getNewsByCompanyId(idCompany);
        call.enqueue(new Callback<List<NewsOfCompany>>() {
            @Override
            public void onResponse(Call<List<NewsOfCompany>> call, Response<List<NewsOfCompany>> response) {
                List<NewsOfCompany> mListNews = response.body();

                mRealmController.deleteAllNewsOfCompany();
                mRealmController.addListNewsOfCompany(mListNews);
            }

            @Override
            public void onFailure(Call<List<NewsOfCompany>> call, Throwable t) {

            }
        });
    }


    /* ============= GET LIST COUPON TEMPLATE =================*/
    private void getCouponTemplate() {

        String idCompany;
        if (SaveData.company == null) {
            idCompany = MainApplication.sIdCompany;
        } else {
            idCompany = SaveData.company.getCompany_id();
        }

        //  Call<List<CouponTemplate>> call = mApiServices.getCouponTemplates(SaveData.web_token, SaveData.company.getCompany_id());
        Call<List<CouponTemplate>> call = mCouponAPI.getCouponTemplates("abc", idCompany);
        call.enqueue(new Callback<List<CouponTemplate>>() {

            @Override
            public void onResponse(Call<List<CouponTemplate>> arg0,
                                   Response<List<CouponTemplate>> arg1) {
                List<CouponTemplate> listCouponTemplate = arg1.body();
                if (listCouponTemplate != null) {
                    mRealmController.deleteCouponTemplate();
                    mRealmController.addListCouponTemplate(listCouponTemplate);
                    Log.d(TAG, "CouponTemplate  " + listCouponTemplate.size());

                } else {
                    Log.d(TAG, "CouponTemplate  null");
                }

            }

            @Override
            public void onFailure(Call<List<CouponTemplate>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }
}

