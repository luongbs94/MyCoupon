package com.ln.mycoupon.customer;

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
import com.ln.model.AccountOflUser;
import com.ln.model.CityOfUser;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.NewsOfCustomer;
import com.ln.model.NewsOfMore;
import com.ln.model.User;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.R;
import com.ln.realm.RealmController;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerLoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = getClass().getSimpleName();

    private Button mBtnFacebook;
    private CallbackManager mCallbackManager;

    private Button mBtnGoogle;
    private GoogleApiClient mGoogleApiClient;

    private LoveCouponAPI mCouponAPI;
    private RealmController mRealmController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCouponAPI = MainApplication.getAPI();
        mRealmController = MainApplication.mRealmController;


        initViews();
        addEvents();
    }

    private void initViews() {

        setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCallbackManager = CallbackManager.Factory.create();
        mBtnFacebook = (Button) findViewById(R.id.btn_facebook_customer);
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Profile mProfile = Profile.getCurrentProfile();
                        String id = loginResult.getAccessToken().getUserId();
                        String token = loginResult.getAccessToken().getToken();

                        AccountOflUser accountOflUser = new AccountOflUser();
                        if (id != null) {
                            accountOflUser.setId(id);
                            accountOflUser.setPicture(getString(R.string.face_image)
                                    + id
                                    + getString(R.string.face_image_end));
                        }

                        if (token != null) {
                            accountOflUser.setAccessToken(token);
                        }

                        SharedPreferences preferences = MainApplication.getSharedPreferences();

                        String strCity = preferences.getString(MainApplication.CITY_OF_USER, "");
                        strCity = new Gson().fromJson(strCity, CityOfUser.class).getCity();

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
                            try {
                                String strUser = new Gson().toJson(accountOflUser);
                                writeSharePreferences(MainApplication.ACCOUNT_CUSTOMER, strUser);

                                getCompanyByUserId(accountOflUser.getId());
                                getNewsOfCustomer(id);
                                getNewsMore(id, strCity);

//                                updateUserToken(accountOflUser.getAccessToken(), MainApplication.getDeviceToken(), "android");
                                LoginManager.getInstance().logOut();
                                Log.d(TAG, "mProfile1 " + accountOflUser.getId() + " - " + token);
                                start();

                            } catch (NullPointerException e) {
                                Log.d(TAG, "Login Facebook Error");
                            }
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
        mBtnGoogle = (Button) findViewById(R.id.btn_google_customer);

        GoogleSignInOptions mInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
        mBtnGoogle.setOnClickListener(new Events());
        mBtnFacebook.setOnClickListener(new Events());
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
                getSnackBar(getString(R.string.login_google_fails));
            }
        }
    }

    private void signInGoogleSuccess(GoogleSignInAccount account) {

        String mCity = getSharedPreferences(
                MainApplication.SHARED_PREFERENCE, MODE_PRIVATE)
                .getString(MainApplication.CITY_OF_USER, "");
        mCity = new Gson().fromJson(mCity, CityOfUser.class).getCity();

        AccountOflUser accountOflUser = new AccountOflUser(account.getId(), account.getEmail(), "", account.getIdToken());
        if (account.getPhotoUrl() != null) {
            accountOflUser.setPicture(account.getPhotoUrl().toString());
        }

        String strUser = new Gson().toJson(accountOflUser);
        writeSharePreferences(MainApplication.ACCOUNT_CUSTOMER, strUser);


        Log.d(TAG, "Login Google Success " + account.getId() + " - " + account.getEmail());
        getCompanyByUserId(account.getId());
        getNewsOfCustomer(account.getId());
        getNewsMore(account.getId(), mCity);
        updateUserToken(account.getIdToken(), MainApplication.getDeviceToken(), "android");
        MainApplication.TYPE_LOGIN_CUSTOMER = MainApplication.TYPE_GOOGLE;

        start();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.d(TAG, "Logout Google " + status.toString());
                    }
                });
    }

    private void getSnackBar(String string) {
        Snackbar.make(mBtnFacebook, string, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void getCompanyByUserId(final String id) {

        Call<List<CompanyOfCustomer>> companyOfCustomer = mCouponAPI.getCompaniesByUserId(id);
        companyOfCustomer.enqueue(new Callback<List<CompanyOfCustomer>>() {
            @Override
            public void onResponse(Call<List<CompanyOfCustomer>> call, Response<List<CompanyOfCustomer>> response) {
                if (response.body() != null) {
//                    mRealmController.deleteListCompanyCustomer();
                    mRealmController.addListCompanyCustomer(response.body());
                    Log.d(TAG, "getCompanyByUserId " + response.body().size());
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

    private void updateUserToken(String userId, String token, String device_os) {

        Call<List<User>> call = mCouponAPI.updateUserToken(userId, token, device_os);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> arg0, Response<List<User>> arg1) {

                MainApplication.setIsAddToken(true);
            }

            @Override
            public void onFailure(Call<List<User>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }

    public void start() {
        Intent intent = new Intent(CustomerLoginActivity.this, CustomerMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void writeSharePreferences(String key, String value) {
        SharedPreferences.Editor editor = MainApplication.getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        getSnackBar(getString(R.string.login_google_fails));
    }

    private class Events implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_facebook_customer:
                    onClickLoginFacebook();
                    break;
                case R.id.btn_google_customer:
                    onClickLoginGoogle();
                    break;
                default:
                    break;
            }
        }

        private void onClickLoginFacebook() {
            LoginManager.getInstance().logInWithReadPermissions(CustomerLoginActivity.this,
                    Arrays.asList(MainApplication.FACEBOOK_PROFILE, MainApplication.FACEBOOK_EMAIL));
        }


        private void onClickLoginGoogle() {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(intent, MainApplication.GOOGLE_SIGN_IN);
        }
    }


    private void getNewsOfCustomer(String id) {

        Call<List<NewsOfCustomer>> call = mCouponAPI.getNewsByUserId(id);
        call.enqueue(new Callback<List<NewsOfCustomer>>() {
            @Override
            public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {

                if (response.body() != null) {
                    mRealmController.deleteListNewsOfCustomer();
                    mRealmController.addListNewsOfCustomer(response.body());
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
}
