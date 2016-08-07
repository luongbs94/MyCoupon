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
import com.google.gson.Gson;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.model.NewsOfCompany;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.R;
import com.ln.realm.RealmController;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.List;

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

    private final String TAG = getClass().getSimpleName();

    private LoveCouponAPI mCouponAPI;
    private RealmController mRealmController;

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;

    private ProgressDialog mProgressDialog;

    private int mStartNotification = 1;
    private boolean isGoogle, isFacebook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        getDataFromIntent();
        initViews();
        addEvents();
    }

    private void getDataFromIntent() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        mCouponAPI = MainApplication.getAPI();
        mRealmController = RealmController.with(this);

        try {
            Intent intent = getIntent();
            mStartNotification = intent.getIntExtra(MainApplication.PUSH_NOTIFICATION, 1);

        } catch (NullPointerException e) {
            Logger.d("Intent null " + e.toString());
        }
    }

    private void initViews() {

        setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Profile mProfile = Profile.getCurrentProfile();
                        String id = loginResult.getAccessToken().getUserId();
                        String token = loginResult.getAccessToken().getToken();

                        Log.d(TAG, "mProfile " + id + " - " + token);

                        if (id == null) {
                            id = mProfile.getId();
                        }

                        if (token != null) {
                            writeSharePreferences(MainApplication.TOKEN_SHOP, token);
                        }

                        if (id != null) {
                            isFacebook = true;
                            writeSharePreferences(MainApplication.ID_SHOP, id);
                            getCompanyProfileSocial(id);
                            getToken(MainApplication.SOCIAL, id, token, MainApplication.FACEBOOK);
                            Logger.d(TAG, "user:" + id + " -token:" + token);
                        }
                    }

                    @Override
                    public void onCancel() {
                        Logger.d("FACEBOOK - onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Logger.d("FACEBOOK - onError" + error.toString());
                    }
                });


        GoogleSignInOptions mInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mInOptions)
                .build();
    }


    private void addEvents() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_google).setOnClickListener(this);
        findViewById(R.id.btn_login_facebook).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == MainApplication.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth
                    .GoogleSignInApi
                    .getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                signInGoogleSuccess(result.getSignInAccount());
            } else {
                getShowMessages(getString(R.string.login_google_fails));
            }
        }
    }

    private void getCompanyProfile(final String user, final String pass) {

        Call<List<Company>> call = mCouponAPI.getCompanyProfile(user, pass, null);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call,
                                   Response<List<Company>> response) {

                if (response.body() != null) {

                    Company company = response.body().get(0);

                    String strCompany = new Gson().toJson(company);
                    writeSharePreferences(MainApplication.COMPANY_SHOP, strCompany);

                    signInSuccess(company);
                    getToken(MainApplication.NORMAL, user, pass, "");
                    Log.d(TAG, "getCompanyProfile " + company.getCompany_id());
                } else {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            getShowMessages(getString(R.string.login_fails));
                        }
                    }, MainApplication.TIME_SLEEP_SETTING);

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
                    signInSuccess(response.body().get(0));
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

    private void signInSuccess(Company company) {

        writeSharePreferences(MainApplication.LOGIN_SHOP, true);
        writeSharePreferences(MainApplication.LOGIN_CLIENT, false);
        writeSharePreferences(MainApplication.OFF_LINE, false);

        getCouponTemplate(company.getCompany_id());
        getNewsByCompanyId(company.getCompany_id());

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

    private void signInGoogleSuccess(GoogleSignInAccount account) {


//        account.getServerAuthCode();
        if (account.getId() != null && account.getIdToken() != null) {
            Log.d(TAG, "Login Google " + account.getId() + " - " + account.getIdToken());

            isGoogle = true;
            writeSharePreferences(MainApplication.ID_SHOP, account.getId());
            writeSharePreferences(MainApplication.TOKEN_SHOP, account.getIdToken());

            getCompanyProfileSocial(account.getId());
            getToken(MainApplication.SOCIAL, account.getId(), account.getServerAuthCode(), MainApplication.GOOGLE);
            getToken(MainApplication.SOCIAL, account.getId(), account.getIdToken(), MainApplication.GOOGLE);

            getShowMessages(getString(R.string.login_success));


        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionListener + " + connectionResult);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (ConnectivityReceiver.isConnect()) {
                    onClickLogin();
                } else {
                    getShowMessages(getString(R.string.check_network));
                }
                break;
            case R.id.btn_login_facebook:
                if (ConnectivityReceiver.isConnect()) {
                    onClickLoginFacebook();
                } else {
                    getShowMessages(getString(R.string.check_network));
                }
                break;
            case R.id.btn_google:
                if (ConnectivityReceiver.isConnect()) {
                    onClickGooglePlus();
                } else {
                    getShowMessages(getString(R.string.check_network));
                }
                break;
            default:
                break;
        }
    }

    private void onClickLogin() {

        String user = ((EditText) findViewById(R.id.username))
                .getText().toString().trim();

        String password = ((EditText) findViewById(R.id.password))
                .getText().toString().trim();

        if (user.length() > 0 && password.length() > 0) {
            showProgressDialog();
            getCompanyProfile(user, password);
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

    /* =============== Get list coupon of company ==============*/
    private void getNewsByCompanyId(String idCompany) {

        Call<List<NewsOfCompany>> call = MainApplication.getAPI().getNewsByCompanyId(idCompany);
        call.enqueue(new Callback<List<NewsOfCompany>>() {
            @Override
            public void onResponse(Call<List<NewsOfCompany>> call, Response<List<NewsOfCompany>> response) {
                if (response.body() != null) {
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

        Call<List<CouponTemplate>> couponShop = mCouponAPI.getCouponTemplates("abc", idCompany);
        couponShop.enqueue(new Callback<List<CouponTemplate>>() {
            @Override
            public void onResponse(Call<List<CouponTemplate>> call,
                                   Response<List<CouponTemplate>> response) {
                if (response.body() != null) {
                    mRealmController.addListCouponTemplate(response.body());

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            Intent intent = new Intent(ShopLoginActivity.this, ShopMainActivity.class);
                            intent.putExtra(MainApplication.PUSH_NOTIFICATION, mStartNotification);
                            startActivity(intent);
                            finish();
                        }
                    }, MainApplication.TIME_SLEEP_SETTING);
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
            mProgressDialog.setMessage(getString(R.string.login));
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void writeSharePreferences(String key, String value) {
        SharedPreferences.Editor editor = MainApplication.getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void writeSharePreferences(String key, boolean value) {
        SharedPreferences.Editor editor = MainApplication.getPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void getToken(int type, String user, String password, String social) {

        if (type == MainApplication.NORMAL) {
            Call<String> getToken = MainApplication.getAPI().getWebTokenUser(user, password);
            getToken.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.body() != null) {
                        writeSharePreferences(MainApplication.TOKEN_SHOP, response.body());
                    }

                    Logger.d(response.body() + "");
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Logger.d(t.toString());
                }
            });
        } else if (type == MainApplication.SOCIAL) {
            Call<String> getToken = MainApplication.getAPI().getWebTokenSocial(user, social, password);
            getToken.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.body() != null) {
                        writeSharePreferences(MainApplication.TOKEN_SHOP, response.body());
                    }
                    Logger.d(response.body());

                    if (isFacebook) {
                        LoginManager.getInstance().logOut();

                        isFacebook = false;
                    }

                    if (isGoogle) {

//                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                                new ResultCallback<Status>() {
//
//                                    @Override
//                                    public void onResult(@NonNull Status status) {
//                                        Log.d(TAG, "Logout Google ");
//                                    }
//                                });
                        isGoogle = false;
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Logger.d(t.toString());
                }
            });
        }
    }
}

