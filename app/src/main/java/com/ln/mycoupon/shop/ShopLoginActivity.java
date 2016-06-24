package com.ln.mycoupon.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.ln.model.Company;
import com.ln.model.DetailUser;
import com.ln.mycoupon.R;
import com.rengwuxian.materialedittext.MaterialEditText;

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
        implements GoogleApiClient.OnConnectionFailedListener {

    private String TAG = getClass().getSimpleName();

    private Button mBtnLogin;
    private Button mBtnLoginFacebook;
    private MaterialEditText mEdtUsername, mEdtPassword;
    private LoveCouponAPI apiService;
    private GoogleApiClient mGoogleApiClient;

    private Button mBtnGooglePlus;
    private CallbackManager mCallbackManager;
    private AccessToken mAccessToken;
    private Profile mProfile;

    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        initViews();
        addEvents();
    }

    private void initViews() {

        apiService = MainApplication.getAPI();

        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEdtUsername = (MaterialEditText) findViewById(R.id.username);
        mEdtPassword = (MaterialEditText) findViewById(R.id.password);

        /* ================== START FACEBOOK ==================*/
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                mProfile = Profile.getCurrentProfile();
                mAccessToken = AccessToken.getCurrentAccessToken();
                if (mAccessToken != null) {
                    try {
                        MainApplication.sShopDetail.setAccessToken(mAccessToken.getToken());

                    } catch (Exception e) {

                    }
                }
                if (mProfile != null) {
                    String picture = getString(R.string.face_image)
                            + mProfile.getId()
                            + getString(R.string.face_image_end);

                    MainApplication.sShopDetail =
                            new DetailUser(mProfile.getId(), mProfile.getName(), picture);

                    getSnackBar(mProfile.getId() + " - " + mProfile.getName());
                    Log.d(TAG, mProfile.getId() + " - " + mProfile.getName());
                    getCompanyProfileSocial(mProfile.getId());
                } else {

                    try {
                        getCompanyProfileSocial(mAccessToken.getUserId());
                    } catch (Exception e) {

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
        });


        mBtnLoginFacebook = (Button) findViewById(R.id.btn_login_facebook);
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_login_shop);

        /* ===================== END FACEBOOK ====================*/

        /*=============== START GOOGLE ===========*/

        GoogleSignInOptions mInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mInOptions)
                .build();

        mBtnGooglePlus = (Button) findViewById(R.id.btn_google);
        /* ===================== END GOOGLE ==================*/

    }

    private void addEvents() {

        mBtnLogin.setOnClickListener(new Events());
        mBtnGooglePlus.setOnClickListener(new Events());
        mBtnLoginFacebook.setOnClickListener(new Events());
    }


    @Override
    protected void onStop() {
        super.onStop();
        mProfile = null;
        mAccessToken = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainApplication.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                loginGoogleSuccess(account);
            } else {
                getSnackBar(getString(R.string.login_google_fails));
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void getCompanyProfile(final String user, final String pass) {

        Call<List<Company>> call = apiService.getCompanyProfile(user, pass, null);
        call.enqueue(new Callback<List<Company>>() {

            @Override
            public void onResponse(Call<List<Company>> arg0,
                                   Response<List<Company>> arg1) {
                List<Company> templates = arg1.body();

                SaveData.company = templates.get(0);

                MainApplication.sIdCompany = SaveData.company.getCompany_id();

                Gson gson = new Gson();

                String data = gson.toJson(SaveData.company);
                MainApplication.editor.putBoolean(MainApplication.LOGIN_SHOP, true);
                MainApplication.editor.putBoolean(MainApplication.LOGIN_CLIENT, false);
                MainApplication.editor.putString(MainApplication.SHOP_DATA, data);
                MainApplication.editor.commit();

                Intent intent = new Intent(ShopLoginActivity.this, ShopMainActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(Call<List<Company>> arg0, Throwable arg1) {
                getSnackBar(getString(R.string.login_fails));
            }
        });
    }

    private void getSnackBar(String string) {
        Snackbar.make(mLinearLayout, string, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void getCompanyProfileSocial(String user_id) {


        Call<List<Company>> call = apiService.getCompanyProfileSocial(user_id);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> arg0, Response<List<Company>> arg1) {
                List<Company> templates = arg1.body();

                SaveData.company = templates.get(0);

                Gson gson = new Gson();

                String data = gson.toJson(SaveData.company);
                MainApplication.editor.putBoolean(MainApplication.LOGIN_SHOP, true);
                MainApplication.editor.putBoolean(MainApplication.LOGIN_CLIENT, false);
                MainApplication.editor.putString(MainApplication.SHOP_DATA, data);
                MainApplication.editor.commit();

                Intent intent = new Intent(ShopLoginActivity.this, ShopMainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onFailure(Call<List<Company>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // integrator login google save state google login
    private void loginGoogleSuccess(GoogleSignInAccount account) {

        MainApplication.sShopDetail = new DetailUser(account.getId(), account.getEmail(), "", account.getIdToken());
        if (account.getPhotoUrl() != null) {
            MainApplication.sShopDetail.setPicture(account.getPhotoUrl().toString());
        }
        getSnackBar("Login Google Success " + account.getId() + " - " + account.getEmail());
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

    private class Events implements View.OnClickListener {
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
                default:
                    onClickGooglePlus();
                    break;
            }
        }

        private void onClickLogin() {

            String str_user = mEdtUsername.getText().toString();
            String str_password = mEdtPassword.getText().toString();

            if (str_user.length() > 0 && str_password.length() > 0) {
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
            LoginManager.getInstance().logInWithReadPermissions(
                    ShopLoginActivity.this,
                    Arrays.asList(MainApplication.FACEBOOK_PROFILE, MainApplication.FACEBOOK_EMAIL));
        }
    }
}

