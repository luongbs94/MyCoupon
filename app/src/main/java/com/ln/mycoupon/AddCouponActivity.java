package com.ln.mycoupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.until.UntilCouponTemplate;

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

    private EditText mEdtMoney, mEdtDescription;
    private Spinner mSpinner;
    private LoveCouponAPI mApiService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_coupon);

        mApiService = MainApplication.getAPI();

        String[] mListMonth = getResources().getStringArray(R.array.month);

        mEdtMoney = (EditText) findViewById(R.id.money);
        mEdtDescription = (EditText) findViewById(R.id.description);
        mSpinner = (Spinner) findViewById(spinner);

        findViewById(R.id.card_view).setOnClickListener(this);

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
            setResult(MainApplication.ADD_COUPON_TEMPLATE, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.card_view) {
            onClickSaveCouponTemplate();
        }
    }

    private void onClickSaveCouponTemplate() {
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

        UntilCouponTemplate template = new UntilCouponTemplate();
        template.setCoupon_template_id(MainApplication.getRandomString(15));
        template.setContent(content);
        template.setValue(value);
        template.setDuration(duration);

        String strCompany = MainApplication
                .getPreferences()
                .getString(MainApplication.COMPANY_SHOP, "");

        Company mCompany = new Gson().fromJson(strCompany, Company.class);

        template.setCompany_id(mCompany.getCompany_id() + "");

        Call<Integer> createCoupon = mApiService.addCouponTemplate(mCompany.getWeb_token(), template);
        createCoupon.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == MainApplication.SUCCESS) {
                    getShowSnackBar(getString(R.string.add_coupon_success));
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getShowSnackBar(getString(R.string.add_coupon_fail));
            }
        });
    }

    private void getShowSnackBar(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
