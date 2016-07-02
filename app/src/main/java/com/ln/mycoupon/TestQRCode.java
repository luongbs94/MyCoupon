package com.ln.mycoupon;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Coupon;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestQRCode extends AppCompatActivity {

    private ImageView qrCodeImageview;
    private String QRcode;
    private final static int WIDTH = 300;
    private LoveCouponAPI apiService;
    private final String TAG = "Coupon";
    private String value, coupon_template_id;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_qrcode);
        qrCodeImageview = (ImageView) findViewById(R.id.img_qr_code_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        value = getIntent().getExtras().getString(MainApplication.VALUE);
        duration = getIntent().getExtras().getInt(MainApplication.DURATION);
        coupon_template_id = getIntent().getExtras().getString("coupon_template_id");

        Button exit = (Button) findViewById(R.id.exit);
        Button reload = (Button) findViewById(R.id.reload);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = MainApplication.getRandomString(10);
                generateQRCode(test);
            }
        });

        apiService = MainApplication.getAPI();


        String value = getIntent().getExtras().getString("value");
        setTitle("QR Code - " + value + " coupon");

        generateQRCode(MainApplication.getRandomString(10));


    }

    private void generateQRCode(final String text) {

        addCoupon(text);

        Thread t = new Thread(new Runnable() {
            public void run() {
                QRcode = text;

                try {
                    synchronized (this) {
                        //   wait(1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap bitmap = null;

                                    bitmap = encodeAsBitmap(QRcode);
                                    qrCodeImageview.setImageBitmap(bitmap);

                                } catch (WriterException e) {
                                    e.printStackTrace();
                                } // end of catch block

                            } // end of run method
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
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
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
        template.setCompany_id(SaveData.company.getCompany_id() + "");
        template.setCoupon_id(coupon_id);
        template.setValue(value);
        template.setCoupon_template_id(coupon_template_id);
        template.setDuration(duration);


        //template.created_date= new Date();

        Call<Coupon> call2 = apiService.addCoupon(template);
        call2.enqueue(new Callback<Coupon>() {

            @Override
            public void onResponse(Call<Coupon> arg0,
                                   Response<Coupon> arg1) {
                Log.d(TAG, "success");

            }

            @Override
            public void onFailure(Call<Coupon> arg0, Throwable arg1) {
                // TODO Auto-generated method stub
                Log.d(TAG, "fail");
            }
        });
    }


}
