package com.ln.mycoupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.CouponTemplate;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/2/16.
 * <></>
 */
public class AddCouponActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();

    private MaterialEditText money, content;
    private CardView saveCoupon;
    private Spinner spinner;
    private LoveCouponAPI apiService;
    private String TAG = "Coupon";
    private LinearLayout layoutView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_coupon);
        spinner = (Spinner) findViewById(R.id.spinner);
        money = (MaterialEditText) findViewById(R.id.money);
        content = (MaterialEditText) findViewById(R.id.description);
        saveCoupon = (CardView) findViewById(R.id.cardview);
        layoutView = (LinearLayout) findViewById(R.id.layout_add_coupon);

        saveCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_money = money.getText().toString();
                String str_content = content.getText().toString();
                if (str_money.length() > 0 && str_content.length() > 0) {
                    int  duration = (spinner.getSelectedItemPosition() + 1) * 30;
                    Log.d("duration", duration + "");
                    postCouponTemplate(str_money, str_content, duration);
                } else {
                    Snackbar.make(view, R.string.not_fill_login, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list.add("1 month");
        list.add("2 months");
        list.add("3 months");
        list.add("4 months");
        list.add("5 months");
        list.add("6 months");
        list.add("7 months");
        list.add("8 months");
        list.add("9 months");
        list.add("10 months");
        list.add("11 months");
        list.add("12 months");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        apiService = MainApplication.getAPI();
    }

    public void postCouponTemplate(final String value, final String content, int duration) {
        CouponTemplate template = new CouponTemplate();
        template.setCoupon_template_id(MainApplication.getRandomString(15));
        template.setContent(content);
        template.setValue(value);
        template.setDuration(duration);
        template.setCompany_id(SaveData.company.getCompany_id() + "");


        //template.created_date= new Date();

        Call<CouponTemplate> call2 = apiService.addCouponTemplate(template);
        call2.enqueue(new Callback<CouponTemplate>() {

            @Override
            public void onResponse(Call<CouponTemplate> arg0,
                                   Response<CouponTemplate> arg1) {

                Snackbar.make(layoutView, R.string.add_coupon_success, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                money.setText("");
                AddCouponActivity.this.content.setText("");
            }

            @Override
            public void onFailure(Call<CouponTemplate> arg0, Throwable arg1) {
                // TODO Auto-generated method stub
                Log.d(TAG, "fail");
                Snackbar.make(layoutView, R.string.add_coupon_fail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent();
            setResult(2, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
