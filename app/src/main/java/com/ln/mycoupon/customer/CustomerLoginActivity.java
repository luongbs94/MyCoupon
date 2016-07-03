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
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.CityOfUser;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.Message;
import com.ln.model.User;
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

    // FACEBOOK
    private Button mBtnFacebook;
    private CallbackManager mCallbackManager;

    private Gson gson = new Gson();

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

        getCityOfUser();

        initViews();
        addEvents();
    }

    private void initViews() {

        setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCallbackManager = CallbackManager.Factory.create();

        mBtnFacebook = (Button) findViewById(R.id.btn_facebook_customer);
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {

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

                        if (mProfile != null && mProfile.getName() != null) {
                            accountOflUser.setName(mProfile.getName());
                        }

                        if (accountOflUser.getId() != null) {
                            try {
                                MainApplication.sDetailUser = accountOflUser;
                                getCompanyByUserId(accountOflUser.getId());
                                updateUserToken(accountOflUser.getAccessToken(), MainApplication.getDeviceToken(), "android");

                                LoginManager.getInstance().logOut();
                                MainApplication.TYPE_LOGIN_SHOP = MainApplication.TYPE_FACEBOOK;
                                Log.d(TAG, "mProfile1 " + accountOflUser.getId() + " - " + token);

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
                    loginGoogleSuccess(account);
                }

            } else {
                getSnackBar(getString(R.string.login_google_fails));
            }
        }
    }

    private void loginGoogleSuccess(GoogleSignInAccount account) {

        MainApplication.sDetailUser = new AccountOflUser(account.getId(), account.getEmail(), "", account.getIdToken());
        if (account.getPhotoUrl() != null) {
            MainApplication.sDetailUser.setPicture(account.getPhotoUrl().toString());
        }

        Log.d(TAG, "Login Google Success " + account.getId() + " - " + account.getEmail());
        getCompanyByUserId(account.getId());
        updateUserToken(account.getIdToken(), MainApplication.getDeviceToken(), "android");
        MainApplication.TYPE_LOGIN_CUSTOMER = MainApplication.TYPE_GOOGLE;


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

        Call<List<CompanyOfCustomer>> call3 = mCouponAPI.getCompaniesByUserId(id);
        call3.enqueue(new Callback<List<CompanyOfCustomer>>() {

            @Override
            public void onResponse(Call<List<CompanyOfCustomer>> arg0, Response<List<CompanyOfCustomer>> arg1) {

                if (arg1.body() != null) {
                    SaveData.listCompanyCustomer = arg1.body();
                    mRealmController.deleteListCompanyCustomer();
                    mRealmController.addListCompanyCustomer(arg1.body());
                } else {
                    Log.d(TAG, "templates " + "null");
                }

                getNewsOfCustomer(id);

                Log.d(TAG, "templates " + arg1.body().size());

//                SaveData.USER_ID = userId;
//
//
//                String data = gson.toJson(SaveData.listCompanyCustomer);
//                MainApplication.editor.putBoolean(MainApplication.LOGIN_SHOP, false);
//                MainApplication.editor.putBoolean(MainApplication.LOGIN_CLIENT, true);
//                MainApplication.editor.putString(MainApplication.CLIENT_DATA, data);
//                MainApplication.editor.commit();


                SharedPreferences preferences =
                        getSharedPreferences(MainApplication.SHARED_PREFERENCE, MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(MainApplication.LOGIN_SHOP, false);
                editor.putBoolean(MainApplication.LOGIN_CLIENT, true);
                editor.apply();

                start();
            }

            @Override
            public void onFailure(Call<List<CompanyOfCustomer>> arg0, Throwable arg1) {
                Log.d(TAG, "Login fails" + arg1.toString());
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

    private void getCityOfUser() {
        Call<CityOfUser> call = mCouponAPI.getCityOfUser();
        call.enqueue(new Callback<CityOfUser>() {
            @Override
            public void onResponse(Call<CityOfUser> call, Response<CityOfUser> response) {
                if (response.body() != null) {
                    MainApplication.cityOfUser = response.body();

                    Log.d(TAG, "City : " + MainApplication.cityOfUser.getCity());
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

    public void getNewsOfCustomer(String id) {

        Call<List<Message>> call = mCouponAPI.getNewsByUserId(id);
        call.enqueue(new Callback<List<Message>>() {

            @Override
            public void onResponse(Call<List<Message>> arg0, Response<List<Message>> arg1) {

                if (arg1.body() != null) {
                    mRealmController.deleteAllNewsOfCustomer();
                    mRealmController.addListNewsOfCustomer(arg1.body());
                    Log.d(TAG, "List NewsOfCustomer " + arg1.body().size());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> arg0, Throwable arg1) {
                Log.d(TAG, "getNewsOfCustomer" + arg1.toString());
            }
        });
    }
}
