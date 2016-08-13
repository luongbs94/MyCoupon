package com.ln.mycoupon.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.model.AccountOflUser;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.NewsOfCustomer;
import com.ln.model.NewsOfMore;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.R;
import com.ln.realm.RealmController;
import com.orhanobut.logger.Logger;

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
    private RealmController mRealm;
    private int mStartNotification = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCouponAPI = MainApplication.getAPI();
        mRealm = MainApplication.mRealmController;

        getDataFromIntent();
        initViews();
        addEvents();
    }

    private void getDataFromIntent() {
        try {
            Intent intent = getIntent();
            mStartNotification = intent.getIntExtra(MainApplication.PUSH_NOTIFICATION, 1);
        } catch (NullPointerException e) {
            Logger.d("Intent null");
        }

    }

    private void initViews() {

        setTitle(R.string.login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

                        AccountOflUser accountOflUser = new AccountOflUser();
                        if (id != null) {
                            accountOflUser.setId(id);
                            accountOflUser.setPicture(getString(R.string.face_image, id));
                        }

                        if (token != null) {
                            accountOflUser.setAccessToken(token);
                        }

                        SharedPreferences preferences = MainApplication.getPreferences();

                        String strCity = preferences.getString(MainApplication.CITY_OF_USER, "");

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

                            String strUser = new Gson().toJson(accountOflUser);
                            writeSharePreferences(MainApplication.ACCOUNT_CUSTOMER, strUser);

                            getCompanyByUserId(accountOflUser.getId());
                            getNewsOfCustomer(id);
                            getNewsMore(id, strCity);

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
                        Log.d(TAG, "FACEBOOK - onError");
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
        findViewById(R.id.btn_google_customer).setOnClickListener(this);
        findViewById(R.id.btn_facebook_customer).setOnClickListener(this);
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

        String mCity = MainApplication.getPreferences()
                .getString(MainApplication.CITY_OF_USER, "");

        AccountOflUser accountOflUser = new AccountOflUser(account.getId(), account.getDisplayName(), "", account.getIdToken());
        if (account.getPhotoUrl() != null) {
            accountOflUser.setPicture(account.getPhotoUrl().toString());
        }

        String strUser = new Gson().toJson(accountOflUser);
        writeSharePreferences(MainApplication.ACCOUNT_CUSTOMER, strUser);

        Log.d(TAG, "Login Google Success " + account.getId() + " - " + account.getIdToken());
        getCompanyByUserId(account.getId());
        getNewsOfCustomer(account.getId());
        getNewsMore(account.getId(), mCity);
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
//                    mRealmController.deleteListCompanyCustomer();
                    mRealm.addListCompanyCustomer(response.body());
                    Log.d(TAG, "getCompanyByUserId " + response.body().size());

                    writeSharePreferences(MainApplication.LOGIN_SHOP, false);
                    writeSharePreferences(MainApplication.LOGIN_CLIENT, true);
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
            case R.id.btn_facebook_customer:
                if (ConnectivityReceiver.isConnect()) {
                    onClickLoginFacebook();
                } else {
                    getShowMessages(getString(R.string.check_network));
                }
                break;
            case R.id.btn_google_customer:
                if (ConnectivityReceiver.isConnect()) {
                    onClickLoginGoogle();
                } else {
                    getShowMessages(getString(R.string.check_network));
                }
                break;
            default:
                break;
        }
    }

    private void onClickLoginFacebook() {
        LoginManager.getInstance()
                .logInWithReadPermissions(
                        CustomerLoginActivity.this,
                        Arrays.asList(MainApplication.FACEBOOK_PROFILE,
                                MainApplication.FACEBOOK_EMAIL));
    }


    private void onClickLoginGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, MainApplication.GOOGLE_SIGN_IN);
    }

    private void getNewsOfCustomer(String id) {

        Call<List<NewsOfCustomer>> call = mCouponAPI.getNewsByUserId(id);
        call.enqueue(new Callback<List<NewsOfCustomer>>() {
            @Override
            public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {

                if (response.body() != null) {
//                    mRealm.deleteListNewsOfCustomer();
                    mRealm.addListNewsOfCustomer(response.body());
                    loadImages();
                    Log.d(TAG, "List NewsOfCustomer " + response.body().size());
                }
            }

            @Override
            public void onFailure(Call<List<NewsOfCustomer>> call, Throwable t) {
                Log.d(TAG, "getNewsOfCustomer" + t.toString());
            }
        });
    }

    private void getNewsMore(String id, String city) {
        Call<List<NewsOfMore>> newsMore = mCouponAPI.getNewsMoreByUserId(id, city);
        newsMore.enqueue(new Callback<List<NewsOfMore>>() {
            @Override
            public void onResponse(Call<List<NewsOfMore>> call,
                                   Response<List<NewsOfMore>> response) {
                if (response.body() != null) {
                    mRealm.addListNewsOfMore(response.body());
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

    private void loadImages() {
        List<NewsOfCustomer> listNews = new ArrayList<>();
        listNews.addAll(RealmController.with(this).getListNewsOfCustomer());
        for (NewsOfCustomer news : listNews) {
            if (news.getLogo_link().contains("http")) {
                Glide.with(MainApplication.getInstance())
                        .load(news.getLogo_link())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .preload();

                if (news.getImages_link() != null) {
                    String strImages = news.getImages_link();
                    String[] listStrImages = strImages.split(";");
                    for (String path : listStrImages) {
                        Glide.with(this)
                                .load(path)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .preload();
                    }
                }
            }
        }

    }
}
