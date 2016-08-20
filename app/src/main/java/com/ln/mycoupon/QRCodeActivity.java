package com.ln.mycoupon;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.gson.Gson;
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.model.AccountOfUser;
import com.ln.model.CompanyOfCustomer;
import com.ln.until.UntilCoupon;

import java.util.List;

import eu.livotov.labs.android.camview.ScannerLiveView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/13/16.
 * <></>
 */
public class QRCodeActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private final String TAG = getClass().getSimpleName();


    private ScannerLiveView mQRCodeReaderView;
    private QRCodeReaderView qrCodeReaderView;

    private LoveCouponAPI apiService;
    private AccountOfUser mAccountOflUser;
    private boolean isCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        apiService = MainApplication.getAPI();

        String strCompany = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
        mAccountOflUser = new Gson().fromJson(strCompany, AccountOfUser.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mQRCodeReaderView = (ScannerLiveView) findViewById(R.id.decoder_view);

        mQRCodeReaderView.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
//                Toast.makeText(QRCodeActivity.this, "Scanner Started", Toast.LENGTH_SHORT).show();
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

//                Log.d(TAG, "onCodeScanned : " + data);
//                if (!isCamera) {
//                    isCamera = true;
//                    mQRCodeReaderView.stopScanner();
//                    updateCoupon(data);
//                }
            }
        });


        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qr_code);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);


    }


    @Override
    protected void onResume() {
        super.onResume();
//        ZXDecoder decoder = new ZXDecoder();
//        decoder.setScanAreaPercent(0.5);
//        mQRCodeReaderView.setDecoder(decoder);
//        mQRCodeReaderView.startScanner();
        //     mQRCodeReaderView.getCameraManager().startPreview();

        qrCodeReaderView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        mQRCodeReaderView.stopScanner();
        qrCodeReaderView.stopCamera();
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

        UntilCoupon coupon = new UntilCoupon();
        coupon.setCoupon_id(coupon_id);
        coupon.setUser_id(mAccountOflUser.getId());
        coupon.setUser_image_link(mAccountOflUser.getPicture());
        coupon.setUser_name(mAccountOflUser.getName());
        coupon.setUser_social(MainApplication.GOOGLE);
        if (mAccountOflUser.getPicture().contains(MainApplication.FACEBOOK)) {
            coupon.setUser_social(MainApplication.FACEBOOK);
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
                                    }, 1500);

                                    mQRCodeReaderView.startScanner();
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


    @Override
    public void onQRCodeRead(String data, PointF[] points) {
        Log.d(TAG, "onCodeScanned : " + data);
        if (!isCamera) {
            isCamera = true;
            mQRCodeReaderView.stopScanner();
            updateCoupon(data);
        }
    }
}
