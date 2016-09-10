package com.ln.mycoupon.shop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.databases.DatabaseManager;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.model.NewsOfCompany;
import com.ln.mycoupon.BaseActivity;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.ForgetPasswordActivity;
import com.ln.mycoupon.R;
import com.ln.views.MaterialEditText;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopLoginActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int LOGIN_GOOGLE = 1121;
    private final String TAG = getClass().getSimpleName();

    private LoveCouponAPI mCouponAPI;

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;

    private ProgressDialog mProgressDialog;

    private int mStartNotification = 1;
    private boolean isGoogle, isLoginFacebook;
    private String mTokenGoogle;


    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getDataFromIntent();
        initViews();
        initGoogle();
        addEvents();

    }

    private void getDataFromIntent() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        mCouponAPI = MainApplication.getAPI();
        try {
            Intent intent = getIntent();
            mStartNotification = intent.getIntExtra(MainApplication.PUSH_NOTIFICATION, 1);

        } catch (NullPointerException e) {
            Log.d(TAG, "Intent null " + e.toString());
        }
    }

    private void initViews() {

        setTitle(R.string.login);

        MaterialEditText edtUser = (MaterialEditText) findViewById(R.id.username);
        MaterialEditText edtPass = (MaterialEditText) findViewById(R.id.password);

        String user = MainApplication.getPreferences().getString(MainApplication.USER_SHOP, "");
        String pass = MainApplication.getPreferences().getString(MainApplication.PASSWORD_SHOP, "");

        edtUser.setText(user);
        edtPass.setText(pass);

        edtUser.setSelection(edtUser.getText().length());
        edtPass.setSelection(edtPass.getText().length());


        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Profile mProfile = Profile.getCurrentProfile();
                        String id = loginResult.getAccessToken().getUserId();
                        String token = loginResult.getAccessToken().getToken();

                        Log.d(TAG, "mProfile " + id + " - " + token);

                        if (id == null) {
                            id = mProfile.getId();
                        }

                        if (token != null) {
                            writeSharePreferences(MainApplication.TOKEN_SHOP, token);
                        }

                        if (id != null) {
                            isLoginFacebook = true;
                            writeSharePreferences(MainApplication.ID_SHOP, id);
                            getCompanyProfileSocial(id, token);
                            Log.d(TAG, "user:" + id + " -token:" + token);
//                            LoginManager.getInstance().logOut();
                            writeSharePreferences(MainApplication.ADMIN, true);
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
                });

    }

    private void initGoogle() {

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
    }

    private void addEvents() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_google).setOnClickListener(this);
        findViewById(R.id.btn_facebook).setOnClickListener(this);
        findViewById(R.id.txt_forgot_password).setOnClickListener(this);
        findViewById(R.id.text_back).setOnClickListener(this);
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                getShowMessages(getString(R.string.login_google_fails));
                Log.d(TAG, "Login fails 1");
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        Log.d(TAG, acct.getIdToken());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Log.d(TAG, "Authentication failed.");
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });

//        account.getServerAuthCode();
        if (acct != null) {
            Log.d(TAG, "Login Google " + acct.getId() + " - " + acct.getIdToken());

            String mScope = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
            new GetAccessTokenTask(acct, mScope).execute();
        }
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.d(TAG, "Log out");
                    }
                });
    }


    private void getCompanyProfile(final String user, final String pass) {

        ShopProfile profile = new ShopProfile(null, null, null, user, pass);
        Call<List<Company>> call = mCouponAPI.getCompanyProfile(profile);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {

                if (response.body() != null) {

                    Company company = response.body().get(0);

                    String strCompany = new Gson().toJson(company);
                    writeSharePreferences(MainApplication.COMPANY_SHOP, strCompany);
                    writeSharePreferences(MainApplication.USER_SHOP, user);
                    writeSharePreferences(MainApplication.PASSWORD_SHOP, pass);
                    if (company.getUser1() != null
                            && user.equals(company.getUser1())) {
                        boolean isAdmin = false;
                        if (company.getUser1_admin() != null
                                && company.getUser1_admin().equals("1")) {
                            isAdmin = true;
                        }
                        writeSharePreferences(MainApplication.ADMIN, isAdmin);
                    } else if (company.getUser2() != null
                            && user.equals(company.getUser2())) {
                        boolean isAdmin = false;
                        if (company.getUser2_admin() != null
                                && company.getUser2_admin().equals("1")) {
                            isAdmin = true;
                        }
                        writeSharePreferences(MainApplication.ADMIN, isAdmin);

                    } else {
                        writeSharePreferences(MainApplication.ADMIN, false);
                    }
                    signInSuccess(company);
                    Log.d(TAG, "getCompanyProfile " + company.getCompany_id());
                } else {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            getShowMessages(getString(R.string.login_fails));
                        }
                    }, MainApplication.TIME_SLEEP_SETTING);

                    Log.d(TAG, "getCompanyProfile " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {
                Log.d(TAG, "getCompanyProfile " + "onFailure " + t.toString());
            }
        });
    }

    private void getCompanyProfileSocial(String user_id, String accessToken) {

        String social = MainApplication.FACEBOOK;
        if (!isLoginFacebook) {
            social = MainApplication.GOOGLE;
            isLoginFacebook = false;
        }

        ShopProfile profile = new ShopProfile(user_id, social, accessToken, null, null);
        Call<List<Company>> call = mCouponAPI.getCompanyProfile(profile);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {

                if (response.body() != null) {
                    Company company = response.body().get(0);
                    signInSuccess(response.body().get(0));
                    String strCompany = new Gson().toJson(company);
                    writeSharePreferences(MainApplication.COMPANY_SHOP, strCompany);
                } else {
                    Log.d(TAG, "getCompanyProfileSocial " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<Company>> arg0, Throwable arg1) {
                Log.d(TAG, "getCompanyProfileSocial " + "Failure");
            }
        });
    }

    private void signInSuccess(Company company) {

        writeSharePreferences(MainApplication.LOGIN_SHOP, true);
        writeSharePreferences(MainApplication.LOGIN_CLIENT, false);
        writeSharePreferences(MainApplication.OFF_LINE, false);

        getCouponTemplate(company.getCompany_id());
        getNewsByCompanyId(company.getCompany_id());

    }

    private void getShowMessages(String string) {
        Toast.makeText(ShopLoginActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, FirstActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionListener + " + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (!ConnectivityReceiver.isConnect()) {
                    getShowMessages(getString(R.string.check_network));
                    return;
                }
                onClickLogin();
                break;
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
                onClickGooglePlus();
                break;
            case R.id.txt_forgot_password:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.text_back:
                startActivity(new Intent(this, FirstActivity.class));
                finish();
            default:
                break;
        }
    }

    private void onClickLogin() {

        String user = ((EditText) findViewById(R.id.username))
                .getText().toString().trim();

        String password = ((EditText) findViewById(R.id.password))
                .getText().toString().trim();

        if (user.length() > 0 && password.length() > 0) {
            showProgressDialog();
            getCompanyProfile(user, password);
        } else {
            getShowMessages(getString(R.string.not_fill_login));
        }
    }

    private void onClickGooglePlus() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void onClickLoginFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(ShopLoginActivity.this,
                Arrays.asList(MainApplication.FACEBOOK_PROFILE, MainApplication.FACEBOOK_EMAIL));
    }

    /* =============== Get list coupon of company ==============*/
    private void getNewsByCompanyId(String idCompany) {

        Call<List<NewsOfCompany>> call = MainApplication.getAPI().getNewsByCompanyId(idCompany);
        call.enqueue(new Callback<List<NewsOfCompany>>() {
            @Override
            public void onResponse(Call<List<NewsOfCompany>> call, Response<List<NewsOfCompany>> response) {
                if (response.body() != null) {
                    DatabaseManager.addListNewsOfCompany(response.body());
                    Log.d(TAG, "getNewsByCompanyId " + response.body().size());
                } else {
                    Log.d(TAG, "getNewsByCompanyId " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<NewsOfCompany>> call, Throwable t) {
                Log.d(TAG, "getNewsByCompanyId " + "onFailure");
            }
        });
    }

    /* ============= GET LIST COUPON TEMPLATE =================*/
    private void getCouponTemplate(String idCompany) {

        Call<List<CouponTemplate>> couponShop = mCouponAPI.getCouponTemplates("abc", idCompany);
        couponShop.enqueue(new Callback<List<CouponTemplate>>() {
            @Override
            public void onResponse(Call<List<CouponTemplate>> call,
                                   Response<List<CouponTemplate>> response) {
                if (response.body() != null) {
                    DatabaseManager.addListCouponTemplate(response.body());

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            Intent intent = new Intent(ShopLoginActivity.this, ShopMainActivity.class);
                            intent.putExtra(MainApplication.PUSH_NOTIFICATION, mStartNotification);
                            startActivity(intent);
                            finish();
                        }
                    }, MainApplication.TIME_SLEEP_SETTING);
                    Log.d(TAG, "getCouponTemplate  " + response.body().size());
                } else {
                    Log.d(TAG, "getCouponTemplate  " + "null");
                }
            }

            @Override
            public void onFailure(Call<List<CouponTemplate>> call, Throwable t) {
                Log.d(TAG, "getCouponTemplate  " + "onFailure");
            }
        });
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


    public class GetAccessTokenTask extends AsyncTask<Void, Void, Boolean> {

        private String mScope;
        private GoogleSignInAccount mAccout;

        GetAccessTokenTask(GoogleSignInAccount account, String scope) {
            mAccout = account;
            mScope = scope;
        }

        @Override
        protected Boolean doInBackground(Void... account) {
            try {
                mTokenGoogle = GoogleAuthUtil.getToken(ShopLoginActivity.this, mAccout.getEmail(), mScope);
                Log.d(TAG, "Token" + mTokenGoogle);
                return true;
            } catch (GoogleAuthException fatalException) {
                Log.d(TAG, fatalException.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {

                writeSharePreferences(MainApplication.ID_SHOP, mAccout.getId());
                writeSharePreferences(MainApplication.TOKEN_SHOP, mAccout.getIdToken());
                getCompanyProfileSocial(mAccout.getId(), mTokenGoogle);

                writeSharePreferences(MainApplication.ADMIN, true);
                logoutGoogle();
            } else {
                Log.d(TAG, "login fails 2");
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, FirstActivity.class));
        fileList();
        super.onBackPressed();
    }


    private void logoutGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {

                    @Override
                    public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                        Log.d(TAG, "Logout Google ");
                    }
                });
    }

    public static class ShopProfile {
        private String user_id;
        private String social;
        private String access_token;
        private String user_name;
        private String password;

        ShopProfile(String user_id, String social, String access_token,
                    String user_name, String password) {
            this.user_id = user_id;
            this.social = social;
            this.access_token = access_token;
            this.user_name = user_name;
            this.password = password;
        }
    }
}

