package com.ln.mycoupon.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ln.mycoupon.R;

public class CustomerLoginActivity extends AppCompatActivity {

    private LoginButton mBtnFacebook;
    private CallbackManager mCallbackManager;
    private Profile mProfile;
    private AccessToken mAccessToken;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        initViews();
        addEvents();
    }

    private void initViews(){

        mCallbackManager = CallbackManager.Factory.create();
        mBtnFacebook  = (LoginButton) findViewById(R.id.btn_facebook_customer);
    }

    private void addEvents(){
        mBtnFacebook.setOnClickListener(new Events());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class Events implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
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
                    mProfile = Profile.getCurrentProfile();
                    mAccessToken = AccessToken.getCurrentAccessToken();
                    Log.d(TAG, mProfile.getId());
                    Log.d(TAG, mAccessToken.getUserId());

//                    getWebTokenSocial(mProfile.getId(), "facebook", mAccessToken.toString());

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
