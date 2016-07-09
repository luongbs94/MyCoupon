package com.ln.mycoupon;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
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
public class QRCodeActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private final String TAG = getClass().getSimpleName();
    private TextView mTextView;
    private QRCodeReaderView mQRCodeReaderView;


    private LoveCouponAPI apiService;
    private RealmController mRealmController;
    private AccountOflUser mAccountOflUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        apiService = MainApplication.getAPI();
        mRealmController = MainApplication.mRealmController;
        mAccountOflUser = mRealmController.getAccountCustomer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mQRCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mQRCodeReaderView.setOnQRCodeReadListener(this);

        mTextView = (TextView) findViewById(R.id.exampleTextView);

    }


    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        mTextView.setText(text);
        mQRCodeReaderView.getCameraManager().stopPreview();
        getCoupon(text);
    }


    // Called when your device have no camera
    @Override
    public void cameraNotFound() {

    }

    // Called when there's no QR codes in the camera preview image
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

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void getCoupon(final String coupon_id) {
        Call<List<Coupon>> call = apiService.getCoupon(coupon_id);
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                if (response.body().size() > 0) {
                    Coupon coupon = response.body().get(0);
                    if (coupon.getUser_id() != null) {
                        new MaterialDialog.Builder(QRCodeActivity.this)
                                .title("Coupon")
                                .content("Coupon đã được sử dụng")
                                .positiveText(R.string.ok)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        mQRCodeReaderView.getCameraManager().startPreview();

                                    }
                                })
                                .show();

                    } else {

                        if (mAccountOflUser != null) {
                            updateCoupon(coupon_id, mAccountOflUser.getId(),
                                    coupon.getDuration());
                        }
                    }
                } else {

                    new MaterialDialog.Builder(QRCodeActivity.this)
                            .title("Coupon")
                            .content("Không tìm thấy coupon này")
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    mQRCodeReaderView.getCameraManager().startPreview();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Log.d(TAG, "getCoupon Failure");

                Toast.makeText(QRCodeActivity.this, "Not found", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void updateCoupon(String coupon_id, String user_id, int duration) {
        Coupon template = new Coupon();
        template.setCoupon_id(coupon_id);
        template.setUser_id(user_id);
        template.setDuration(duration);

        try {
            template.setUser_image_link(mAccountOflUser.getPicture());
            template.setUser_name(mAccountOflUser.getName());

        } catch (Exception e) {
            Log.d(TAG, "updateCoupon " + e.toString());
        }


        Call<List<CompanyOfCustomer>> call2 = apiService.updateUserCoupon(template);
        call2.enqueue(new Callback<List<CompanyOfCustomer>>() {

            @Override
            public void onResponse(Call<List<CompanyOfCustomer>> arg0,
                                   Response<List<CompanyOfCustomer>> arg1) {
                new MaterialDialog.Builder(QRCodeActivity.this)
                        .title("Coupon")
                        .content("Bạn đã thêm mới một coupon")
                        .positiveText(R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                SaveData.updateCoupon = true;
                                QRCodeActivity.this.finish();

                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<List<CompanyOfCustomer>> arg0, Throwable arg1) {
                Toast.makeText(QRCodeActivity.this, "Not found", Toast.LENGTH_LONG).show();
            }
        });
    }

}
