package com.ln.mycoupon;

import android.app.ProgressDialog;
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
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.Coupon;
import com.ln.realm.RealmController;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/13/16.
 * <></>
 */
public class QRCodeActivity extends AppCompatActivity
        implements QRCodeReaderView.OnQRCodeReadListener {

    private final String TAG = getClass().getSimpleName();
    private RealmController mRealmController;


    private QRCodeReaderView mQRCodeReaderView;

    private LoveCouponAPI apiService;
    private AccountOflUser mAccountOflUser;
    private ProgressDialog mProgressDialog;
    private boolean isCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        apiService = MainApplication.getAPI();
        mRealmController = MainApplication.mRealmController;

        String strCompany = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
        mAccountOflUser = new Gson().fromJson(strCompany, AccountOflUser.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mQRCodeReaderView = (QRCodeReaderView) findViewById(R.id.decoder_view);
        if (mQRCodeReaderView != null) {
            mQRCodeReaderView.setOnQRCodeReadListener(this);
        }
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        mQRCodeReaderView.getCameraManager().stopPreview();
        if (!isCamera) {
            isCamera = true;
            updateCoupon(text);
        }

        Log.d(TAG, "onQRCodeRead " + text);
    }


    @Override
    public void cameraNotFound() {
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQRCodeReaderView.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQRCodeReaderView.getCameraManager().stopPreview();
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
                            .title("Coupon")
                            .content("Coupon đã được sử dụng")
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
                                            mQRCodeReaderView.getCameraManager().startPreview();
                                        }
                                    }, 1500);

                                    mQRCodeReaderView.getCameraManager().startPreview();
                                }
                            })
                            .show();


                    Log.d(TAG, "CompanyOfCustomer " + response.body());
                } else {

                    final CompanyOfCustomer company = response.body().get(0);
                    showDialog();
                    mRealmController.addCompanyOfCustomer(company);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = getIntent();
                            Bundle bundle = new Bundle();
                            bundle.putString(MainApplication.ID_COMPANY, company.getCompany_id());
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            Toast.makeText(QRCodeActivity.this, "Ban da them 1 coupon moi", Toast.LENGTH_SHORT).show();
                            hideDialog();
                            finish();
                        }
                    }, MainApplication.TIME_SLEEP);

                    Log.d(TAG, "CompanyOfCustomer " + response.body());
                }
            }

            @Override
            public void onFailure(Call<List<CompanyOfCustomer>> call, Throwable t) {
                Log.d(TAG, "CompanyOfCustomer  onFailure " + t.toString());

            }
        });
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.com_facebook_loading));
        }
        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
