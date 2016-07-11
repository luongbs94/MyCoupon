package com.ln.mycoupon.shop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.CityOfUser;
import com.ln.model.Company;
import com.ln.model.CompanyLocation;
import com.ln.model.CouponTemplate;
import com.ln.model.NewsOfCompany;
import com.ln.mycoupon.FirstActivity;
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
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
        , Runnable {

    private static final int LOGIN_SHOP = 1;
    private String TAG = getClass().getSimpleName();

    private LoveCouponAPI mCouponAPI;
    private LoveCouponAPI mCouponAPI2;

    private RealmController mRealmController;

    private Button mBtnLogin;
    private Button mBtnLoginFacebook;
    private Button mBtnGooglePlus;

    private EditText mEdtUsername, mEdtPassword;

    private GoogleApiClient mGoogleApiClient;

    private CallbackManager mCallbackManager;

    private CompanyLocation mCompanyLocation;

    private ProgressDialog mProgressDialog;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        mCouponAPI = MainApplication.getAPI();
        mCouponAPI2 = MainApplication.getApiService2();
        mRealmController = MainApplication.mRealmController;

        initViews();
        addEvents();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == LOGIN_SHOP) {
                    SystemClock.sleep(MainApplication.TIME_SLEEP);
                    hideProgressDialog();
                }
            }
        };
    }

    private void initViews() {


        setTitle(R.string.login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEdtUsername = (EditText) findViewById(R.id.username);
        mEdtPassword = (EditText) findViewById(R.id.password);

        /* ================== START FACEBOOK ==================*/

        mBtnLoginFacebook = (Button) findViewById(R.id.btn_login_facebook);

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
                if (mProfile != null && mProfile.getName() != null) {
                    accountOflUser.setName(mProfile.getName());
                }

                try {
                    if (accountOflUser.getId() != null) {
                        MainApplication.sShopDetail = accountOflUser;
                        getCompanyProfileSocial(accountOflUser.getId());
                        MainApplication.TYPE_LOGIN_SHOP = MainApplication.TYPE_FACEBOOK;
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

        /* ===================== END FACEBOOK ====================*/

        /*=============== START GOOGLE ===========*/
        mBtnGooglePlus = (Button) findViewById(R.id.btn_google);

        GoogleSignInOptions mInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mInOptions)
                .build();

        /* ===================== END GOOGLE ==================*/
    }


    private void addEvents() {
        mBtnLogin.setOnClickListener(this);
        mBtnGooglePlus.setOnClickListener(this);
        mBtnLoginFacebook.setOnClickListener(this);
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
                signInGoogleSuccess(account);
            } else {
                getSnackBar(getString(R.string.login_google_fails));
            }
        }

    }

    private void getCompanyProfile(String user, String pass) {

        Call<List<Company>> call = mCouponAPI.getCompanyProfile(user, pass, null);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {

                if (response.body() != null) {
                    loginSuccess(response.body().get(0));
                    mRealmController.saveAccountShop(response.body().get(0));

                    new Thread(ShopLoginActivity.this).start();

                    Log.d(TAG, "getCompanyProfile " + response.body().get(0).getCompany_id());
                } else {
                    new Thread(ShopLoginActivity.this).start();
                    Log.d(TAG, "getCompanyProfile " + "null");
                    getSnackBar(getString(R.string.login_fails));
                }
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {
                Log.d(TAG, "getCompanyProfile " + "onFailure " + t.toString());
            }
        });
    }

    private void getCompanyProfileSocial(String user_id) {

        Call<List<Company>> call = mCouponAPI.getCompanyProfileSocial(user_id);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {

                if (response.body() != null) {
                    loginSuccess(response.body().get(0));
                    mRealmController.saveAccountShop(response.body().get(0));
                } else {
                    Log.d(TAG, "getCompanyProfileSocial " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<Company>> arg0, Throwable arg1) {
                Log.d(TAG, "getCompanyProfileSocial " + "Failure");
            }
        });
    }

    private void loginSuccess(Company company) {

        mRealmController.saveAccountShop(company);
        Log.d(TAG, "Company " + company.getCompany_id());


        SharedPreferences preferences = getSharedPreferences(
                MainApplication.SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainApplication.LOGIN_SHOP, true);
        editor.putBoolean(MainApplication.LOGIN_CLIENT, false);
        editor.putBoolean(MainApplication.OFF_LINE, false);
        editor.apply();

        getCityOfUser();                                 // get address of company
        getCouponTemplate(company.getCompany_id());     //  get list coupon template of company
        getNewsByCompanyId(company.getCompany_id());    // get list news of company

        Intent intent = new Intent(ShopLoginActivity.this, ShopMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getSnackBar(String string) {
        Snackbar.make(mBtnLogin, string, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, FirstActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // integrator login google save state google login
    private void signInGoogleSuccess(GoogleSignInAccount account) {

        MainApplication.sShopDetail = new AccountOflUser(account.getId(), account.getEmail(), "", account.getIdToken());
        if (account.getPhotoUrl() != null) {
            MainApplication.sShopDetail.setPicture(account.getPhotoUrl().toString());
        }
        getCompanyProfileSocial(account.getId());
        onClickLogoutGoogle();
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
            showProgressDialog();
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
                    if (MainApplication.mRealmController.getAccountShop() != null) {
                        mCompanyLocation = new CompanyLocation(MainApplication.mRealmController.getAccountShop().getCompany_id(),
                                MainApplication.cityOfCompany.getCity(),
                                MainApplication.cityOfCompany.getCountry_name());

                        updateCompanyLocation();
                    }

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

    /* =============== Get list coupon of company ==============*/
    private void getNewsByCompanyId(String idCompany) {

        Call<List<NewsOfCompany>> call = MainApplication.getAPI().getNewsByCompanyId(idCompany);
        call.enqueue(new Callback<List<NewsOfCompany>>() {
            @Override
            public void onResponse(Call<List<NewsOfCompany>> call, Response<List<NewsOfCompany>> response) {
                if (response.body() != null) {
                    mRealmController.deleteListNewsOfCompany();
                    mRealmController.addListNewsOfCompany(response.body());
                    Log.d(TAG, "getNewsByCompanyId " + response.body().size());
                } else {
                    Log.d(TAG, "getNewsByCompanyId " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<NewsOfCompany>> call, Throwable t) {
                Log.d(TAG, "getNewsByCompanyId " + "onFailure");
            }
        });
    }

    /* ============= GET LIST COUPON TEMPLATE =================*/
    private void getCouponTemplate(String idCompany) {

        //  Call<List<CouponTemplate>> call = mApiServices.getCouponTemplates(SaveData.web_token, SaveData.company.getCompany_id());
        Call<List<CouponTemplate>> couponShop = mCouponAPI.getCouponTemplates("abc", idCompany);
        couponShop.enqueue(new Callback<List<CouponTemplate>>() {
            @Override
            public void onResponse(Call<List<CouponTemplate>> call, Response<List<CouponTemplate>> response) {
                if (response.body() != null) {
                    mRealmController.deleteCouponTemplate();
                    mRealmController.addListCouponTemplate(response.body());
                    Log.d(TAG, "getCouponTemplate  " + response.body().size());
                } else {
                    Log.d(TAG, "getCouponTemplate  " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<CouponTemplate>> call, Throwable t) {
                Log.d(TAG, "getCouponTemplate  " + "onFailure");
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.com_facebook_loading));
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void run() {

        Message message = new Message();
        message.what = LOGIN_SHOP;
        message.setTarget(mHandler);
        message.sendToTarget();
    }
}

