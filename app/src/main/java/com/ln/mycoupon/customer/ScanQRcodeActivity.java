package com.ln.mycoupon.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.model.AccountOfUser;
import com.ln.model.CompanyOfCustomer;
import com.ln.mycoupon.R;
import com.ln.until.UntilCoupon;

import java.util.List;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRcodeActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private final String TAG = getClass().getSimpleName();
    private ZBarScannerView mScannerView;

    private LoveCouponAPI apiService;
    private AccountOfUser mAccountOflUser;
    private boolean isCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);


        setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);

        initData();
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.qr_code);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(true);
        }
    }

    private void initData() {

        apiService = MainApplication.getAPI();

        String strCompany = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
        mAccountOflUser = new Gson().fromJson(strCompany, AccountOfUser.class);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(ScanQRcodeActivity.this);
//            }
//        }, 2000);

        Log.d(TAG, "onCodeScanned : " + rawResult.getContents());
        if (!isCamera) {
            isCamera = true;
            mScannerView.stopCamera();
            updateCoupon(rawResult.getContents());
        }
    }

    private void updateCoupon(String coupon_id) {

        Log.d(TAG, "updateCoupon " + mAccountOflUser.getId());
        Log.d(TAG, "updateCoupon " + mAccountOflUser.getName());
        Log.d(TAG, "updateCoupon " + mAccountOflUser.getPicture());

        UntilCoupon coupon = new UntilCoupon();
        coupon.setCoupon_id(coupon_id);
        coupon.setUser_id(mAccountOflUser.getId());
        coupon.setUser_image_link(mAccountOflUser.getPicture());
        coupon.setUser_name(mAccountOflUser.getName());
        coupon.setUser_social(null);
        if (mAccountOflUser.getPicture() != null) {
            if (mAccountOflUser.getPicture().contains(MainApplication.FACEBOOK)) {
                coupon.setUser_social(MainApplication.FACEBOOK);
            } else if (mAccountOflUser.getPicture().contains(MainApplication.GOOGLE)) {
                coupon.setUser_social(MainApplication.GOOGLE);
            }
        }
        String city = MainApplication
                .getSharePrefer()
                .getString(MainApplication.CITY_OF_USER, "");

        Call<List<CompanyOfCustomer>> updateCoupon = apiService.updateUserCoupon(city, coupon);

        updateCoupon.enqueue(new Callback<List<CompanyOfCustomer>>() {
            @Override
            public void onResponse(Call<List<CompanyOfCustomer>> call, Response<List<CompanyOfCustomer>> response) {
                if (response.body() == null) {
                    new MaterialDialog.Builder(ScanQRcodeActivity.this)
                            .content(R.string.coupon_used)
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            isCamera = false;
//                                            mScannerView.resumeCameraPreview(ScanQRcodeActivity.this);
                                            mScannerView.setResultHandler(ScanQRcodeActivity.this);
                                            mScannerView.startCamera();
                                        }
                                    }, 500);

//                                    mQRCodeReaderView.startScanner();
                                }
                            })
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            isCamera = false;
                                            mScannerView.setResultHandler(ScanQRcodeActivity.this);
                                            mScannerView.startCamera();
//                                            mScannerView.resumeCameraPreview(ScanQRcodeActivity.this);
                                        }
                                    }, 500);

//                                    mScannerView.startScanner();
                                }
                            })
                            .show();

                    Log.d(TAG, "CompanyOfCustomer " + response.body());
                } else {

                    final CompanyOfCustomer company = response.body().get(0);
                    DatabaseManager.addShopOfCustomer(company);
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putString(MainApplication.ID_COMPANY, company.getCompany_id());
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(ScanQRcodeActivity.this, getString(R.string.you_add_news_coupon), Toast.LENGTH_SHORT).show();
                    finish();

                    Log.d(TAG, "CompanyOfCustomer " + response.body());
                }
            }

            @Override
            public void onFailure(Call<List<CompanyOfCustomer>> call, Throwable t) {
                Log.d(TAG, "CompanyOfCustomer  onFailure " + t.toString());
            }
        });
    }

}
