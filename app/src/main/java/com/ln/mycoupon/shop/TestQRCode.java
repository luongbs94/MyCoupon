package com.ln.mycoupon.shop;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.mycoupon.R;
import com.ln.until.UntilCoupon;

import net.glxn.qrgen.android.QRCode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestQRCode extends AppCompatActivity {

    private static final String TAG = "TestQRCode";
    private LoveCouponAPI apiService;
    private String value, coupon_template_id, mContent;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_qrcode);

        apiService = MainApplication.getAPI();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            value = bundle.getString(MainApplication.VALUE);
            duration = bundle.getInt(MainApplication.DURATION);
            coupon_template_id = bundle.getString(MainApplication.COUPON_TEMpLATE_ID);
            mContent = bundle.getString(MainApplication.CONTENT_COUPON);
            if (value != null) {
                setTitle(getString(R.string.title_qr_code, value));
            }
        }


        findViewById(R.id.card_other_code)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String test = MainApplication.getRandomString(20);
                        generateQRCode(test);
                    }
                });

        generateQRCode(MainApplication.getRandomString(20));

    }

    private void generateQRCode(final String text) {

        addCoupon(text);
        Bitmap myBitmap = QRCode.from(text).bitmap();
        ((ImageView) findViewById(R.id.img_qr_code_image))
                .setImageBitmap(myBitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addCoupon(final String coupon_id) {

        UntilCoupon template = new UntilCoupon();

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);

        template.setCompany_id(company.getCompany_id());
        template.setCoupon_id(coupon_id);
        template.setValue(value);
        template.setCoupon_template_id(coupon_template_id);
        template.setDuration(duration);
        template.setContent(mContent);

        Call<Integer> call2 = apiService.addCoupon(company.getWeb_token(), template);
        call2.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == MainApplication.SUCCESS) {
                    Log.d(TAG, "success");
                } else {
                    Log.d(TAG, "khong thanh cong");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }
}
