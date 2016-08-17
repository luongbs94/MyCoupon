package com.ln.mycoupon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.Coupon;
import com.ln.realm.RealmController;

import java.util.List;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/13/16.
 * <></>
 */
public class QRCodeActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private RealmController mRealmController;


    private ScannerLiveView mQRCodeReaderView;

    private LoveCouponAPI apiService;
    private AccountOflUser mAccountOflUser;
    private boolean isCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        apiService = MainApplication.getAPI();
        mRealmController = MainApplication.mRealmController;

        String strCompany = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
        mAccountOflUser = new Gson().fromJson(strCompany, AccountOflUser.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mQRCodeReaderView = (ScannerLiveView) findViewById(R.id.decoder_view);

        mQRCodeReaderView.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
                Toast.makeText(QRCodeActivity.this, "Scanner Started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {
                // Toast.makeText(QRCodeActivity.this,"Scanner Stopped",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerError(Throwable err) {
                //      Toast.makeText(QRCodeActivity.this,"Scanner Error: " + err.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeScanned(String data) {

                Log.d(TAG, "onCodeScanned : " + data);
                if (!isCamera) {
                    isCamera = true;
                    mQRCodeReaderView.stopScanner();
                    updateCoupon(data);
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        ZXDecoder decoder = new ZXDecoder();
        decoder.setScanAreaPercent(0.5);
        mQRCodeReaderView.setDecoder(decoder);
        mQRCodeReaderView.startScanner();
        //     mQRCodeReaderView.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQRCodeReaderView.stopScanner();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCoupon(String coupon_id) {

        Log.d(TAG, "updateCoupon " + mAccountOflUser.getId());
        Log.d(TAG, "updateCoupon " + mAccountOflUser.getName());
        Log.d(TAG, "updateCoupon " + mAccountOflUser.getPicture());

        Coupon coupon = new Coupon();
        coupon.setCoupon_id(coupon_id);
        coupon.setUser_id(mAccountOflUser.getId());
        coupon.setUser_image_link(mAccountOflUser.getPicture());
        coupon.setUser_name(mAccountOflUser.getName());

        if (mAccountOflUser.getPicture().contains(MainApplication.FACEBOOK)) {
            coupon.setUser_social(MainApplication.FACEBOOK);
        } else {
            coupon.setUser_social(MainApplication.GOOGLE);
        }
        String city = MainApplication
                .getSharePrefer()
                .getString(MainApplication.CITY_OF_USER, "");

        Call<List<CompanyOfCustomer>> updateCoupon = apiService.updateUserCoupon(city, coupon);

        updateCoupon.enqueue(new Callback<List<CompanyOfCustomer>>() {
            @Override
            public void onResponse(Call<List<CompanyOfCustomer>> call, Response<List<CompanyOfCustomer>> response) {
                if (response.body() == null) {
                    new MaterialDialog.Builder(QRCodeActivity.this)
                            .title(R.string.coupon)
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
                                            mQRCodeReaderView.startScanner();
                                        }
                                    }, 1500);

                                    mQRCodeReaderView.startScanner();
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
                                            mQRCodeReaderView.startScanner();
                                        }
                                    }, 1000);

                                    mQRCodeReaderView.startScanner();
                                }
                            })
                            .show();


                    Log.d(TAG, "CompanyOfCustomer " + response.body());
                } else {

                    final CompanyOfCustomer company = response.body().get(0);
                    mRealmController.addCompanyOfCustomer(company);
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putString(MainApplication.ID_COMPANY, company.getCompany_id());
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(QRCodeActivity.this, getString(R.string.you_add_news_coupon), Toast.LENGTH_SHORT).show();
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
