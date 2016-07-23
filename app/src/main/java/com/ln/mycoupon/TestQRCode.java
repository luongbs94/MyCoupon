package com.ln.mycoupon;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.Coupon;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestQRCode extends AppCompatActivity {

    private ImageView mImageQRCode;
    private String QRCode;
    private final static int WIDTH = 200;
    private LoveCouponAPI apiService;
    private final String TAG = "Coupon";
    private String value, coupon_template_id;
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
            if (value != null) {
                setTitle("QR Code - " + value + " coupon");
            }
        }

        mImageQRCode = (ImageView) findViewById(R.id.img_qr_code_image);


        findViewById(R.id.card_other_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = MainApplication.getRandomString(10);
                generateQRCode(test);
            }
        });

        generateQRCode(MainApplication.getRandomString(10));

    }

    private void generateQRCode(final String text) {

        addCoupon(text);

        Thread t = new Thread(new Runnable() {
            public void run() {
                QRCode = text;
                try {
                    synchronized (this) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap bitmap = null;

                                    bitmap = encodeAsBitmap(QRCode);
                                    mImageQRCode.setImageBitmap(bitmap);

                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, 300, 0, 0, w, h);
        return bitmap;
    } /// end of this method

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void addCoupon(final String coupon_id) {
        Coupon template = new Coupon();

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);

        template.setCompany_id(company.getCompany_id() + "");
        template.setCoupon_id(coupon_id);
        template.setValue(value);
        template.setCoupon_template_id(coupon_template_id);
        template.setDuration(duration);


        Log.d("test", new Date().getTime() + "");
        template.setCreated_date(new Date().getTime());


        Call<Integer> call2 = apiService.addCoupon(template);
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
                Log.d(TAG, "onFailure");
            }
        });
    }


}
