package com.ln.mycoupon.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.databases.DatabaseManager;
import com.ln.model.AccountOfUser;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.NewsOfCustomer;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.ForgetPasswordActivity;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CustomerLoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;

    private LoveCouponAPI mCouponAPI;
    private int mStartNotification = 1;


    private EditText mEdtUser, mEdtPassword;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCouponAPI = MainApplication.getAPI();

        getDataFromIntent();
        initViews();
        addEvents();
    }

    private void getDataFromIntent() {
        try {
            Intent intent = getIntent();
            mStartNotification = intent.getIntExtra(MainApplication.PUSH_NOTIFICATION, 1);
        } catch (NullPointerException e) {
            Log.d(TAG, "Intent null");
        }

    }

    private void initViews() {

        setTitle(R.string.login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mEdtUser = (EditText) findViewById(R.id.username);
        mEdtPassword = (EditText) findViewById(R.id.password);


        String user = MainApplication.getPreferences().getString(MainApplication.USER_CUSTOMER, "");
        String pass = MainApplication.getPreferences().getString(MainApplication.PASSWORD_CUSTOMER, "");
        mEdtUser.setText(user);
        mEdtUser.setSelection(mEdtUser.length());
        mEdtPassword.setText(pass);
        mEdtPassword.setSelection(mEdtPassword.length());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Profile mProfile = Profile.getCurrentProfile();
                        String id = loginResult.getAccessToken().getUserId();
                        String token = loginResult.getAccessToken().getToken();

                        if (id == null && mProfile != null && mProfile.getId() != null) {
                            id = mProfile.getId();
                        }

                        AccountOfUser accountOflUser = new AccountOfUser();
                        if (id != null) {
                            accountOflUser.setId(id);
                            accountOflUser.setPicture(getString(R.string.face_image, id));
                        }

                        if (token != null) {
                            accountOflUser.setAccessToken(token);
                        }

                        SharedPreferences preferences = MainApplication.getPreferences();

                        String name = null;
                        if (mProfile != null && mProfile.getName() != null) {
                            name = mProfile.getName();
                            if (id != null) {

                                writeSharePreferences(MainApplication.USER_NAME, mProfile.getName());
                                writeSharePreferences(MainApplication.USER_ID, id);
                            }
                        } else {
                            String idPreference = preferences.getString(MainApplication.USER_ID, "");
                            if (idPreference.equals(id)) {
                                name = preferences.getString(MainApplication.USER_NAME, "");
                            }
                        }

                        accountOflUser.setName(name);

                        if (accountOflUser.getId() != null) {

                            login(accountOflUser.getId(), accountOflUser.getAccessToken(), "android", null);

                            String account = new Gson().toJson(accountOflUser);
                            writeSharePreferences(MainApplication.ACCOUNT_CUSTOMER, account);
                            Glide.with(MainApplication.getInstance())
                                    .load(accountOflUser.getPicture())
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .preload();
                            LoginManager.getInstance().logOut();
                            Log.d(TAG, "mProfile1 " + accountOflUser.getId() + " - " + token);
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "FACEBOOK - onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "FACEBOOK - onError" + error.toString());
                    }
                }

        );

     /* ============== START GOOGLE ===============*/

        GoogleSignInOptions mInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mInOptions)
                .build();

        /* ================ END GOOGLE ================*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, FirstActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addEvents() {
        findViewById(R.id.btn_facebook).setOnClickListener(this);
        findViewById(R.id.btn_google).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.txt_forgot_password).setOnClickListener(this);
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
                if (account != null) {
                    signInGoogleSuccess(account);
                }

            } else {
                getShowMessages(getString(R.string.login_google_fails));
            }
        }
    }

    private void signInGoogleSuccess(GoogleSignInAccount account) {

        AccountOfUser accountOflUser = new AccountOfUser(account.getId(), account.getDisplayName(), "", account.getIdToken());
        if (account.getPhotoUrl() != null) {
            accountOflUser.setPicture(account.getPhotoUrl().toString());

            Glide.with(MainApplication.getInstance())
                    .load(accountOflUser.getPicture())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .preload();
        }

        String strUser = new Gson().toJson(accountOflUser);
        writeSharePreferences(MainApplication.ACCOUNT_CUSTOMER, strUser);

        Log.d(TAG, "Login Google Success " + account.getId() + " - " + account.getIdToken());
        login(accountOflUser.getId(), account.getIdToken(), "android", null);
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.d(TAG, "Logout Google " + status.toString());
                    }
                });
    }

    private void getShowMessages(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void getCompanyByUserId(final String id) {

        Call<List<CompanyOfCustomer>> companyOfCustomer = mCouponAPI.getCompaniesByUserId(id);
        companyOfCustomer.enqueue(new Callback<List<CompanyOfCustomer>>() {
            @Override
            public void onResponse(Call<List<CompanyOfCustomer>> call,
                                   Response<List<CompanyOfCustomer>> response) {
                if (response.body() != null) {
                    preImageShop(response.body());
                    DatabaseManager.addListShopOfCustomer(response.body());
                    Log.d(TAG, "getCompanyByUserId " + response.body().size());

                    writeSharePreferences(MainApplication.LOGIN_SHOP, false);
                    writeSharePreferences(MainApplication.LOGIN_CLIENT, true);

                    hideProgressDialog();
                    start();
                } else {
                    Log.d(TAG, "getCompanyByUserId " + "null");
                }

            }

            @Override
            public void onFailure(Call<List<CompanyOfCustomer>> call, Throwable t) {
                Log.d(TAG, "getCompanyByUserId " + "onFailure " + t.toString());
            }
        });
    }

    public void start() {
        Intent intent = new Intent(CustomerLoginActivity.this, CustomerMainActivity.class);
        intent.putExtra(MainApplication.PUSH_NOTIFICATION, mStartNotification);
        startActivity(intent);
        finish();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        getShowMessages(getString(R.string.login_google_fails));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_facebook:
                if (!ConnectivityReceiver.isConnect()) {
                    getShowMessages(getString(R.string.check_network));
                    return;
                }
                onClickLoginFacebook();
                break;
            case R.id.btn_google:
                if (!ConnectivityReceiver.isConnect()) {
                    getShowMessages(getString(R.string.check_network));
                    return;
                }
                onClickLoginGoogle();
                break;
            case R.id.btn_login:
                if (!ConnectivityReceiver.isConnect()) {
                    getShowMessages(getString(R.string.check_network));
                    return;
                }


                String user = mEdtUser.getText().toString().trim();
                String pass = mEdtPassword.getText().toString().trim();
                if (user.length() == 0 || pass.length() == 0 || !checkEmail(user)) {
                    getShowMessages(getString(R.string.email_do_not_match));
                    return;
                }

                showProgressDialog();
                String device_os = "android";
                String token = MainApplication.getPreferences().getString(MainApplication.DEVICE_TOKEN, "");

                AccountOfUser account = new AccountOfUser(user, user, null, token);
                String strAccount = new Gson().toJson(account);
                writeSharePreferences(MainApplication.ACCOUNT_CUSTOMER, strAccount);
                login(user, token, device_os, pass);
                break;
            case R.id.txt_forgot_password:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    public void login(final String userId, String token, String device_os, final String password) {

        CustomerProfile profile = new CustomerProfile(userId, device_os, token, password);
        Call<Integer> loginCustomer = MainApplication.getAPI().updateUserToken(profile);
        loginCustomer.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == MainApplication.SUCCESS) {
                    MainApplication.setIsAddToken(true);
                    getCompanyByUserId(userId);
                    getNewsOfCustomer(userId);
                    String strCity = MainApplication.getPreferences().getString(MainApplication.CITY_OF_USER, "");
                    getNewsMore(userId, strCity);

                    if (password != null) {
                        writeSharePreferences(MainApplication.USER_CUSTOMER, userId);
                        writeSharePreferences(MainApplication.PASSWORD_CUSTOMER, password);
                    }
                } else {
                    getShowMessages(getString(R.string.login_fails));
                }

                Log.d(TAG, "login: " + response.body());
                Log.d(TAG, "login: " + userId);

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, "Login " + t.toString());
            }
        });
    }

    private void onClickLoginFacebook() {
        LoginManager.getInstance().logInWithReadPermissions
                (
                        CustomerLoginActivity.this,
                        Arrays.asList(MainApplication.FACEBOOK_PROFILE,
                                MainApplication.FACEBOOK_EMAIL)
                );
    }


    private void onClickLoginGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, MainApplication.GOOGLE_SIGN_IN);
    }

    private void getNewsOfCustomer(final String id) {

        Call<List<NewsOfCustomer>> call = mCouponAPI.getNewsByUserId(id);
        call.enqueue(new Callback<List<NewsOfCustomer>>() {
            @Override
            public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {

                if (response.body() != null) {
                    DatabaseManager.addListNewsOfCustomer(response.body(), MainApplication.TYPE_NEWS, id);
                    loadImages();
                    Log.d(TAG, "List NewsOfCustomer " + response.body().size());
                }
                Log.d(TAG, "getNewsOfCustomer: " + response.body());
            }

            @Override
            public void onFailure(Call<List<NewsOfCustomer>> call, Throwable t) {
                Log.d(TAG, "getNewsOfCustomer" + t.toString());
            }
        });
    }

    private void getNewsMore(final String id, String city) {
        Call<List<NewsOfCustomer>> newsMore = mCouponAPI.getNewsMoreByUserId(id, city);
        newsMore.enqueue(new Callback<List<NewsOfCustomer>>() {
            @Override
            public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {
                if (response.body() != null) {
                    DatabaseManager.addListNewsOfCustomer(response.body(), MainApplication.TYPE_NEWS_MORE, id);
                    Log.d(TAG, " getNewsMore " + response.body().size());
                } else {
                    Log.d(TAG, " getNewsMore " + " null");
                }

            }

            @Override
            public void onFailure(Call<List<NewsOfCustomer>> call, Throwable t) {
                Log.d(TAG, "getNewsMore " + " onFailure " + t.toString());

            }
        });
    }

    private void loadImages() {
        List<NewsOfCustomer> listNews = new ArrayList<>();
        listNews.addAll(DatabaseManager.getListNewsOfCustomer(MainApplication.TYPE_NEWS));
        for (NewsOfCustomer news : listNews) {

            if (news.getLogo_link() != null) {
                if (news.getLogo_link().contains("http")) {
                    Glide.with(MainApplication.getInstance())
                            .load(news.getLogo_link())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .preload();
                }
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


    private void preImageShop(List<CompanyOfCustomer> companies) {
        for (CompanyOfCustomer item : companies) {
            if (item.getLogo_link() != null) {
                Glide.with(MainApplication.getInstance())
                        .load(item.getLogo_link())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .preload();
            }
        }
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

    private boolean checkEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static class CustomerProfile {

        private String user_id;
        private String device_os;
        private String device_token;
        private String password;

        public CustomerProfile() {
        }

        CustomerProfile(String user_id, String device_os,
                        String device_token, String password) {
            this.user_id = user_id;
            this.device_os = device_os;
            this.device_token = device_token;
            this.password = password;
        }
    }
}
