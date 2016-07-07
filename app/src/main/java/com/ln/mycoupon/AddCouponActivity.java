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
import android.widget.EditText;
import android.widget.Spinner;

import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.CouponTemplate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ln.mycoupon.R.id.spinner;

/**
 * Created by luongnguyen on 4/2/16.
 * <></>
 */
public class AddCouponActivity extends AppCompatActivity
        implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private static final int SIZE_MONTH = 12;

    private EditText mEdtMoney, mEdtDescription;

    private String[] mListMonth = new String[SIZE_MONTH];
    private CardView mSaveCouponTemplate;
    private Spinner mSpinner;
    private LoveCouponAPI mApiService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_coupon);

        mApiService = MainApplication.getAPI();
        mListMonth = getResources().getStringArray(R.array.month);

        mEdtMoney = (EditText) findViewById(R.id.money);
        mEdtDescription = (EditText) findViewById(R.id.description);
        mSpinner = (Spinner) findViewById(spinner);
        mSaveCouponTemplate = (CardView) findViewById(R.id.card_view);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mListMonth);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.card_view) {
            onClickSaveCouponTemplate(v);
        }
    }

    private void onClickSaveCouponTemplate(View view) {
        String money = mEdtMoney.getText().toString().trim();
        String description = mEdtDescription.getText().toString().trim();
        if (!money.isEmpty() && !description.isEmpty()) {
            int duration = (mSpinner.getSelectedItemPosition() + 1) * 30;
            Log.d("duration", duration + "");
            postCouponTemplate(money, description, duration);
        } else {
            getShowSnackBar(getString(R.string.not_fill_login));
        }
    }

    private void postCouponTemplate(String value, String content, int duration) {
        CouponTemplate template = new CouponTemplate();
        template.setCoupon_template_id(MainApplication.getRandomString(15));
        template.setContent(content);
        template.setValue(value);
        template.setDuration(duration);
        template.setCompany_id(MainApplication.mRealmController.getAccountShop().getCompany_id() + "");

        Call<CouponTemplate> createCoupon = mApiService.addCouponTemplate(template);
        createCoupon.enqueue(new Callback<CouponTemplate>() {
            @Override
            public void onResponse(Call<CouponTemplate> call, Response<CouponTemplate> response) {
                getShowSnackBar(getString(R.string.add_coupon_success));
            }

            @Override
            public void onFailure(Call<CouponTemplate> call, Throwable t) {
                getShowSnackBar(getString(R.string.add_coupon_fail));
            }
        });
    }

    private void getShowSnackBar(String s) {
        Snackbar.make(mSaveCouponTemplate, s, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
