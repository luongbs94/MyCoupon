package com.ln.mycoupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.InformationAccount;
import com.ln.model.Models;
import com.ln.mycoupon.shop.ShopMainActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 3/30/16.
 */
public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private Button mBtnLogin;
    private LoginButton mBtnLoginFacebook;
    private MaterialEditText username, password;
    private LoveCouponAPI apiService;
    private String TAG = getClass().getSimpleName();
    private GoogleSignInOptions mInOptions;
    private GoogleApiClient mGoogleApiClient;

    private SignInButton mBtnGooglePlus;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private AccessToken mAccessToken;
    private ProfileTracker mProfileTracker;
    private Profile mProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        setTitle(R.string.login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiService = MainApplication.getAPI();
        mInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mInOptions)
                .build();

        initViews();
        addEvents();
    }

    private void initViews() {

        mCallbackManager = CallbackManager.Factory.create();

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);

        mBtnLoginFacebook = (LoginButton) findViewById(R.id.btn_login_facebook);
        mBtnLoginFacebook.setReadPermissions("user_friends");
        mBtnLoginFacebook.setReadPermissions("public_profile");
        mBtnLoginFacebook.setReadPermissions("email");

        mBtnGooglePlus = (SignInButton) findViewById(R.id.btn_google);
        mBtnGooglePlus.setSize(SignInButton.SIZE_WIDE);
        mBtnGooglePlus.setScopes(mInOptions.getScopeArray());

    }

    private void addEvents() {

        mBtnLogin.setOnClickListener(new Events());
        mBtnGooglePlus.setOnClickListener(new Events());
        mBtnLoginFacebook.setOnClickListener(new Events());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Models.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount accountGoogle = result.getSignInAccount();
            InformationAccount account = new InformationAccount();
            account.setId(accountGoogle.getId());
            account.setDisplayName(accountGoogle.getDisplayName());
            account.setEmail(accountGoogle.getEmail());
            account.setPhotoUrl(accountGoogle.getPhotoUrl().toString());
            account.setIdToken(accountGoogle.getIdToken());
            username.setText(account.getDisplayName());
        }
    }

    public void getWebTokenUser(String user, String pass) {
        Call<ResponseBody> call1 = apiService.getWebTokenUser(user, pass);

        call1.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> arg0,
                                   Response<ResponseBody> arg1) {

                try {
                    String webToken = arg1.body().string();
                    SaveData.web_token = webToken;
                    Log.d("mycoupon", SaveData.web_token);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("mycoupon", "false");

                }

                finish();

            }

            @Override
            public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }

    public void getWebTokenSocial(String user_id, String social, String access_token) {
        Call<ResponseBody> call1 = apiService.getWebTokenSocial(user_id, social, access_token);

        call1.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> arg0,
                                   Response<ResponseBody> arg1) {

                try {
                    String webToken = arg1.body().string();
                    SaveData.web_token = webToken;
                    Intent intent = new Intent(LoginActivity.this, ShopMainActivity.class);
                    startActivity(intent);
                    Log.d("mycoupon", SaveData.web_token);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("mycoupon", "false");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }


    public void getCompanyProfile(final String user, final String pass) {


        Call<List<Company>> call = apiService.getCompanyProfile(user, pass, null);

        call.enqueue(new Callback<List<Company>>() {

            @Override
            public void onResponse(Call<List<Company>> arg0,
                                   Response<List<Company>> arg1) {
                List<Company> templates = arg1.body();

                SaveData.company = templates.get(0);

                Gson gson = new Gson();

                String data = gson.toJson(SaveData.company);
                MainApplication.editor.putBoolean(MainApplication.LOGINSHOP, true);
                MainApplication.editor.putBoolean(MainApplication.LOGINCLIENT, false);
                MainApplication.editor.putString(MainApplication.SHOP_DATA, data);
                MainApplication.editor.commit();


                Intent intent = new Intent(LoginActivity.this, ShopMainActivity.class);
                startActivity(intent);


                //    finish();
            }

            @Override
            public void onFailure(Call<List<Company>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }

    public void getCompanyProfileSocial(String user_id) {


        Call<List<Company>> call = apiService.getCompanyProfileSocial(user_id);

                call.enqueue(new Callback<List<Company>>() {

                    @Override
                    public void onResponse(Call<List<Company>> arg0,
                                           Response<List<Company>> arg1) {
                        List<Company> templates = arg1.body();

                        SaveData.company = templates.get(0);

                        Gson gson = new Gson();

                        String data = gson.toJson(SaveData.company);
                        MainApplication.editor.putBoolean(MainApplication.LOGINSHOP, true);
                        MainApplication.editor.putBoolean(MainApplication.LOGINCLIENT, false);
                        MainApplication.editor.putString(MainApplication.SHOP_DATA, data);
                        MainApplication.editor.commit();


                        Intent intent = new Intent(LoginActivity.this, ShopMainActivity.class);
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionListener + " + connectionResult);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
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
            protected void onCurrentProfileChanged(
                    Profile oldProfile, Profile currentProfile) {
                mProfile = currentProfile;
            }
        };

        mProfile = Profile.getCurrentProfile();

        if (mAccessToken != null) {
//            mStatusTextView.setText(mAccessToken.getUserId() + "");
//            Log.i("Tagsss", mAccessToken.getUserId() + "");

        }
        if (mProfile != null) {
            username.setText(mProfile.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProfileTracker.stopTracking();
        mAccessTokenTracker.stopTracking();
    }

    private class Events implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_login:
                    onClickLogin(view);
                    break;
                case R.id.btn_login_facebook:
                    onClickLoginFacebook();
                    break;
                case R.id.btn_google:
                    onClickGooglePlus();
                    break;
            }
        }

        private void onClickLogin(View view) {

            String str_user = username.getText().toString();
            String str_password = password.getText().toString();

            if (str_user.length() > 0 && str_password.length() > 0) {
                getCompanyProfile(str_user, str_password);

            } else {
                Snackbar.make(view, R.string.not_fill_login, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        private void onClickGooglePlus() {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(intent, Models.GOOGLE_SIGN_IN);
        }

        private void onClickLoginFacebook() {
            mBtnLoginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

                private ProfileTracker mProfileTracker;

                @Override
                public void onSuccess(LoginResult loginResult) {

                    if(Profile.getCurrentProfile() == null) {
                        mProfileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                Log.d(TAG, profile2.getFirstName());
                                Log.d(TAG, profile2.getId());
                                Log.d(TAG, profile2.getName());
                                Log.d(TAG, profile2.getLinkUri() + "");
                                getCompanyProfileSocial(profile2.getId());
                                mProfileTracker.stopTracking();
                            }
                        };
                        // no need to call startTracking() on mProfileTracker
                        // because it is called by its constructor, internally.
                    }
                    else {
                        Profile profile = Profile.getCurrentProfile();
                        Log.v(TAG, profile.getFirstName());
                    }



//                    username.setText(mProfile.getId() + "");
                   // Log.d(TAG, mProfile.getId());
          //          Log.d(TAG, mAccessToken.getUserId());

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

