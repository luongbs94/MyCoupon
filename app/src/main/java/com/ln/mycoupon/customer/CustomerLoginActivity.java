package com.ln.mycoupon.customer;

import android.content.Intent;
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
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company1;
import com.ln.model.DetailUser;
import com.ln.model.User;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerLoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = getClass().getSimpleName();

    // FACEBOOK
    private LoginButton mBtnFacebook;
    private CallbackManager mCallbackManager;

    private Gson gson = new Gson();

    private Button mBtnGoogle;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        FacebookSdk.sdkInitialize(getApplicationContext());

        initViews();
        addEvents();
    }

    private void initViews() {

        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCallbackManager = CallbackManager.Factory.create();

        mBtnFacebook = (LoginButton) findViewById(R.id.btn_facebook_customer);
        if (mBtnFacebook != null) {
            mBtnFacebook.setReadPermissions(MainApplication.FACEBOOK_PROFILE,
                    MainApplication.FACEBOOK_EMAIL);
        }

        mBtnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                Profile mProfile = Profile.getCurrentProfile();
                String id = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();


                String url = null;
                DetailUser detailUser = new DetailUser();
                if (id != null) {
                    detailUser.setId(id);
                    url = getString(R.string.face_image) + id + getString(R.string.face_image_end);

                }
                if (token != null) {
                    detailUser.setAccessToken(token);
                    Log.d(TAG, "mProfile " + id + " - " + token);
                }

                if (mProfile.getName() != null) {
                    detailUser.setName(mProfile.getName());
                }
                if (url != null) {
                    detailUser.setPicture(url);
                }
                if (detailUser.getId() != null) {
                    MainApplication.sDetailUser = detailUser;
                    getCompanyByUserId(detailUser.getId());
                    LoginManager.getInstance().logOut();
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

        MainApplication.sDetailUser = new DetailUser(account.getId(), account.getEmail(), "", account.getIdToken());
        if (account.getPhotoUrl() != null) {
            MainApplication.sDetailUser.setPicture(account.getPhotoUrl().toString());
        }

        getSnackBar("Login Google Success " + account.getId() + " - " + account.getEmail());
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

    private void getCompanyByUserId(final String userId) {

        Call<List<Company1>> call3 = MainApplication.apiService.getCompaniesByUserId(userId);
        call3.enqueue(new Callback<List<Company1>>() {

            @Override
            public void onResponse(Call<List<Company1>> arg0, Response<List<Company1>> arg1) {

                List<Company1> templates = arg1.body();
                if (templates == null) {
                    SaveData.listCompanyCustomer = new ArrayList<>();
                } else {
                    SaveData.listCompanyCustomer = templates;
                }

                SaveData.USER_ID = userId;

                String data = gson.toJson(SaveData.listCompanyCustomer);
                MainApplication.editor.putBoolean(MainApplication.LOGIN_SHOP, false);
                MainApplication.editor.putBoolean(MainApplication.LOGIN_CLIENT, true);
                MainApplication.editor.putString(MainApplication.CLIENT_DATA, data);
                MainApplication.editor.commit();

                start();
            }

            @Override
            public void onFailure(Call<List<Company1>> arg0, Throwable arg1) {
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

                case R.id.btn_google_customer:
                    onClickLoginGoogle();
                    break;
                default:
                    break;
            }
        }


        private void onClickLoginGoogle() {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(intent, MainApplication.GOOGLE_SIGN_IN);
        }
    }

}
