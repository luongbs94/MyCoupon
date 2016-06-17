package com.ln.mycoupon.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

public class CustomerLoginActivity extends AppCompatActivity {

    private LoginButton mBtnFacebook;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private AccessToken mAccessToken;
    private Profile mProfile;
    private ProfileTracker mProfileTracker;

    private final String TAG = getClass().getSimpleName();

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        initViews();
        addEvents();
    }

    private void initViews() {

        mCallbackManager = CallbackManager.Factory.create();
        mBtnFacebook = (LoginButton) findViewById(R.id.btn_facebook_customer);
        mBtnFacebook.setReadPermissions("public_profile");
        mBtnFacebook.setReadPermissions("email");
    }

    private void addEvents() {
        mBtnFacebook.setOnClickListener(new Events());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initDataFacebook();
    }

    private void initDataFacebook() {

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                mAccessToken = currentAccessToken;
            }
        };

        mAccessToken = AccessToken.getCurrentAccessToken();

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                mProfile = currentProfile;
            }
        };

        mProfile = Profile.getCurrentProfile();

        if (mAccessToken != null) {
            Log.i(TAG, mAccessToken.getUserId() + "");
            getCompanyByUserId(mAccessToken.getUserId());

            //   if(MainApplication.isAddToken() == false && MainApplication.getDeviceToken().length() > 5){
            updateUserToken(mAccessToken.getUserId(), MainApplication.getDeviceToken(), "android");

        }

        if (mProfile != null) {
            MainApplication.sDetailUser = new DetailUser(mProfile.getId(), mProfile.getName());
            Log.d(TAG, mProfile.getName() + " - " + mProfile.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAccessTokenTracker.stopTracking();
    }

    private void getCompanyByUserId(final String userId) {

//        Call<List<Company1>> call3 = MainApplication.apiService1.getCompaniesByUserId(userId);
        Call<List<Company1>> call3 = MainApplication.apiService.getCompaniesByUserId(userId);
        call3.enqueue(new Callback<List<Company1>>() {

            @Override
            public void onResponse(Call<List<Company1>> arg0,
                                   Response<List<Company1>> arg1) {
                List<Company1> templates = arg1.body();

                if (templates == null) {
                    SaveData.listCompany = new ArrayList<Company1>();
                } else {
                    SaveData.listCompany = templates;
                }

                SaveData.USER_ID = userId;

                String data = gson.toJson(SaveData.listCompany);
                MainApplication.editor.putBoolean(MainApplication.LOGINSHOP, false);
                MainApplication.editor.putBoolean(MainApplication.LOGINCLIENT, true);
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
            public void onResponse(Call<List<User>> arg0,
                                   Response<List<User>> arg1) {

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


    private class Events implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_facebook_customer:
                    onClickLoginFaceBook();
                    break;
                default:
                    break;
            }
        }

        private void onClickLoginFaceBook() {
            mBtnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    mAccessToken = AccessToken.getCurrentAccessToken();
                    if (mAccessToken != null) {
                        Log.d(TAG, mAccessToken.getUserId());
                        //10205539341392320
                        getCompanyByUserId(mAccessToken.getUserId());

                        //   if(MainApplication.isAddToken() == false && MainApplication.getDeviceToken().length() > 5){
//                        updateUserToken(mAccessToken.getUserId(), MainApplication.getDeviceToken(), "android");
                        updateUserToken("10205539341392320", MainApplication.getDeviceToken(), "android");

                    }
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException error) {
                }
            });
        }
    }
}
