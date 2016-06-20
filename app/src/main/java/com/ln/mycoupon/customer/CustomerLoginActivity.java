package com.ln.mycoupon.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company1;
import com.ln.model.DetailUser;
import com.ln.model.User;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String PERMISSION = "publish_actions";
    // create login facebook
    private Button mBtnFacebook;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private AccessToken mAccessToken;
    private Profile mProfile;
    private ProfileTracker mProfileTracker;

    private final String TAG = getClass().getSimpleName();

    private Gson gson = new Gson();

    // create login google
    private Button mBtnGoogle;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        initViews();
        addEvents();
    }

    private void initViews() {

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
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

                mProfile = Profile.getCurrentProfile();
                if (mProfile != null) {
                    MainApplication.sDetailUser = new DetailUser(mProfile.getId(), mProfile.getName());
                    Log.d(TAG, mProfile.getId() + " - " + mProfile.getName());
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
        mBtnFacebook = (Button) findViewById(R.id.btn_facebook_customer);

        // google
        mBtnGoogle = (Button) findViewById(R.id.btn_google_customer);


        //56:CE:70:45:DA:93:5A:92:02:D8:45:C3:58:4E:10:36:09:24:42:C4
        GoogleSignInOptions mInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
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
//                    onClickLoginGoogle();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    private void addEvents() {
        mBtnFacebook.setOnClickListener(new Events());
        mBtnGoogle.setOnClickListener(new Events());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainApplication.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
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

    private void loginGoogleSuccess(GoogleSignInAccount account) {
        // init detailUser
        Log.d(TAG, "id google:" + account.getId());
        MainApplication.sDetailUser = new DetailUser(account.getId(), account.getEmail());

        getSnackBar("Login Google Success " + account.getId() + " - " + account.getEmail());

        getCompanyByUserId(account.getId());
        updateUserToken(account.getIdToken(), MainApplication.getDeviceToken(), "android");

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
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

    private void getSnackBar(String string) {
        Snackbar.make(mBtnFacebook, string, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initDataFacebook();
    }

    private void initDataFacebook() {

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

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
        mProfileTracker.stopTracking();
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


    private void getCompanyByUserId(final String userId) {

//        Call<List<Company1>> call3 = MainApplication.apiService1.getCompaniesByUserId(userId);
        Call<List<Company1>> call3 = MainApplication.apiService.getCompaniesByUserId(userId);
        call3.enqueue(new Callback<List<Company1>>() {

            @Override
            public void onResponse(Call<List<Company1>> arg0,
                                   Response<List<Company1>> arg1) {
                List<Company1> templates = arg1.body();

                if (templates == null) {
                    SaveData.listCompanyCustomer = new ArrayList<>();
                } else {
                    SaveData.listCompanyCustomer = templates;
                }

                SaveData.USER_ID = userId;

                String data = gson.toJson(SaveData.listCompanyCustomer);
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

    }


    private class Events implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_facebook_customer:
                    onClickLoginFaceBook();
                    break;
                case R.id.btn_google_customer:
                    onClickLoginGoogle();
                    break;
                default:
                    break;
            }
        }

        private void onClickLoginFaceBook() {
            LoginManager.getInstance().logInWithPublishPermissions(
                    CustomerLoginActivity.this,
                    Collections.singleton(PERMISSION));
        }

        private void onClickLoginGoogle() {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(intent, MainApplication.GOOGLE_SIGN_IN);
        }

    }
}
