package com.ln.mycoupon.shop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
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
        implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

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

// cap nhat vi tri cua cong ty len lam o phan setting


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        mCouponAPI = MainApplication.getAPI();
        mRealmController = MainApplication.mRealmController;

        initViews();
        addEvents();
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

                if (id == null) {
                    id = mProfile.getId();
                }

                try {
                    if (id != null) {

                        if (token != null) {
                            writeSharePreferences(MainApplication.ID_SHOP, id);
                            writeSharePreferences(MainApplication.TOKEN_SHOP, token);
                        }

                        getCompanyProfileSocial(id);
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
                getShowMessages(getString(R.string.login_google_fails));
            }
        }

    }

    private void getCompanyProfile(String user, String pass) {

        Call<List<Company>> call = mCouponAPI.getCompanyProfile(user, pass, null);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {

                if (response.body() != null) {

                    Company company = response.body().get(0);

                    String strCompany = new Gson().toJson(company);
                    writeSharePreferences(MainApplication.COMPANY_SHOP, strCompany);

                    loginSuccess(company);
                    Log.d(TAG, "getCompanyProfile " + response.body().get(0).getCompany_id());
                } else {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            getShowMessages(getString(R.string.login_fails));
                        }
                    }, 1500);

                    Log.d(TAG, "getCompanyProfile " + "null");
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
                    Company company = response.body().get(0);
                    loginSuccess(response.body().get(0));
                    String strCompany = new Gson().toJson(company);
                    writeSharePreferences(MainApplication.COMPANY_SHOP, strCompany);
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

        Log.d(TAG, "Company " + company.getCompany_id());

        SharedPreferences preferences = getSharedPreferences(
                MainApplication.SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainApplication.LOGIN_SHOP, true);
        editor.putBoolean(MainApplication.LOGIN_CLIENT, false);
        editor.putBoolean(MainApplication.OFF_LINE, false);
        editor.apply();

        getCouponTemplate(company.getCompany_id());     //  get list coupon template of company
        getNewsByCompanyId(company.getCompany_id());    // get list news of company

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
                Intent intent = new Intent(ShopLoginActivity.this, ShopMainActivity.class);
                startActivity(intent);
                finish();

            }
        }, 2000);


    }

    private void getShowMessages(String string) {
        Toast.makeText(ShopLoginActivity.this, string, Toast.LENGTH_SHORT).show();
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

        if (account.getId() != null && account.getIdToken() != null) {
            Log.d(TAG, "Login Google " + account.getId() + " - " + account.getEmail());

            writeSharePreferences(MainApplication.ID_SHOP, account.getId());
            writeSharePreferences(MainApplication.TOKEN_SHOP, account.getIdToken());

            getCompanyProfileSocial(account.getId());
            onClickLogoutGoogle();
            getShowMessages("Login Google Success ");
            MainApplication.TYPE_LOGIN_SHOP = MainApplication.TYPE_GOOGLE;
        }
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
            getShowMessages(getString(R.string.not_fill_login));
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
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void writeSharePreferences(String key, String value) {
        SharedPreferences.Editor editor =
                getSharedPreferences(MainApplication.SHARED_PREFERENCE, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }
}

