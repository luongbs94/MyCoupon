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
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.DetailUser;
import com.ln.mycoupon.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Collections;
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

    private static final String PERMISSION = "publish_actions";

    private Button mBtnLogin;
    private Button mBtnLoginFacebook;
    private MaterialEditText username, password;
    private LoveCouponAPI apiService;
    private String TAG = getClass().getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    private Button mBtnGooglePlus;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private AccessToken mAccessToken;
    private ProfileTracker mProfileTracker;
    private Profile mProfile;

    private LinearLayout mLinearLayout;

    //login with google
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiService = MainApplication.getAPI();

        GoogleSignInOptions mInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mInOptions)
                .build();


        // key login google
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
//                    MainApplication.sDetailUser = new DetailUser(user)
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + " - " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        initViews();
        addEvents();
    }

    private void initViews() {


        mBtnLogin = (Button) findViewById(R.id.btn_login);
        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mProfile = Profile.getCurrentProfile();
                String picture = MainApplication.IMAGE_FACEBOOK + mProfile.getId() + MainApplication.IMAGE_FACEBOOK_END;
                MainApplication.sShopDetail = new DetailUser(mProfile.getId(), mProfile.getName(), picture);

                getSnackBar(mProfile.getId() + " - " + mProfile.getName());
                Log.d(TAG, mProfile.getId() + " - " + mProfile.getName());
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

        mBtnGooglePlus = (Button) findViewById(R.id.btn_google);
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_login_shop);
    }

    private void addEvents() {

        mBtnLogin.setOnClickListener(new Events());
        mBtnGooglePlus.setOnClickListener(new Events());
        mBtnLoginFacebook.setOnClickListener(new Events());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainApplication.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Fire base
                GoogleSignInAccount account = result.getSignInAccount();
                loginGoogleSuccess(account);
            } else {
                // login fails
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
                MainApplication.editor.putBoolean(MainApplication.LOGINSHOP, true);
                MainApplication.editor.putBoolean(MainApplication.LOGINCLIENT, false);
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
                MainApplication.editor.putBoolean(MainApplication.LOGINSHOP, true);
                MainApplication.editor.putBoolean(MainApplication.LOGINCLIENT, false);
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


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        initDataFacebook();
    }

    // integrator login facebook save state facebook login
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
            protected void onCurrentProfileChanged(
                    Profile oldProfile, Profile currentProfile) {
                mProfile = currentProfile;
            }
        };

        mProfile = Profile.getCurrentProfile();

        if (mAccessToken != null) {
//            mStatusTextView.setText(mAccessToken.getUserId() + "");
            Log.i("Tagsss", mAccessToken.getUserId() + "");
        }
        if (mProfile != null) {
            Log.i("Tagsss", mProfile.getId() + "");
        }
    }

    // integrator login google save state google login
    private void loginGoogleSuccess(GoogleSignInAccount acct) {

        // init detailUser
        Log.d(TAG, "id google:" + acct.getId());
        MainApplication.sShopDetail = new DetailUser(acct.getId(), acct.getEmail());
        getSnackBar("Login google Success " + acct.getId() + " - " + acct.getEmail());
        Log.d(TAG, "url: " + acct.getPhotoUrl());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "signInWithCredential", task.getException());
                            getSnackBar("Authentication failed.");
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProfileTracker.stopTracking();
        mAccessTokenTracker.stopTracking();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionListener + " + connectionResult);
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

            String str_user = username.getText().toString();
            String str_password = password.getText().toString();

            if (str_user.length() > 0 && str_password.length() > 0) {
                getCompanyProfile(str_user, str_password);

            } else {
                getSnackBar(getString(R.string.not_fill_login));
            }
        }

        private void onClickGooglePlus() {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(intent, MainApplication.GOOGLE_SIGN_IN);
        }

        private void onClickLoginFacebook() {

            LoginManager.getInstance().logInWithPublishPermissions(
                    ShopLoginActivity.this,
                    Collections.singletonList(PERMISSION));
        }
    }
}

