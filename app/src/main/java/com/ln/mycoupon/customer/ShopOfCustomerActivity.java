package com.ln.mycoupon.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.ln.adapter.CouponTemplateClientAdapter;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.interfaces.RecyclerViewListener;
import com.ln.model.CompanyOfCustomer;
import com.ln.mycoupon.R;
import com.ln.until.Until;
import com.ln.views.RecyclerViewHeader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 6/7/16.
 * <p>
 * coupon
 */
public class ShopOfCustomerActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private CompanyOfCustomer mCompanyOfCustomer;
    private CouponTemplateClientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_company_client);

        initData();
        initViews();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String idCompany = bundle.getString(MainApplication.ID_COMPANY);
        mCompanyOfCustomer = DatabaseManager.getShopOfCustomer(idCompany);

    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.shop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        initCollapsingToolbar();

        RecyclerView mRecyclerCoupon = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerCoupon.setHasFixedSize(true);
        mRecyclerCoupon.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerCoupon.addOnItemTouchListener(new RecyclerViewListener(this,
                new RecyclerViewListener.OnClickRecyclerView() {
                    @Override
                    public void onClick(View view, final int position) {
                        new MaterialDialog
                                .Builder(ShopOfCustomerActivity.this)
                                .icon(getResources().getDrawable(R.drawable.ic_delete_black_24px))
                                .title(R.string.delete_coupon)
                                .positiveText(R.string.agree)
                                .positiveColor(getResources().getColor(R.color.title_bg))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        deleteCoupon(position);
                                    }
                                })
                                .show();
                    }
                }));
        adapter = new CouponTemplateClientAdapter(this, mCompanyOfCustomer);
        mRecyclerCoupon.setAdapter(adapter);

        final RecyclerViewHeader header = (RecyclerViewHeader) findViewById(R.id.header);
        assert header != null;
        header.attachTo(mRecyclerCoupon);
        if (mCompanyOfCustomer != null) {
            if (mCompanyOfCustomer.getLogo() != null) {

                if (mCompanyOfCustomer.getLogo().contains("http")) {
                    Glide.with(this)
                            .load(mCompanyOfCustomer.getLogo())
                            .thumbnail(0.5f)
                            .placeholder(R.drawable.ic_logo_blank)
                            .into((ImageView) header.findViewById(R.id.img_logo_nav));

                } else {
                    byte[] bytes = MainApplication.convertToBytes(mCompanyOfCustomer.getLogo());
                    Glide.with(this)
                            .load(bytes)
                            .asBitmap()
                            .thumbnail(0.5f)
                            .placeholder(R.drawable.ic_logo_blank)
                            .into((ImageView) header.findViewById(R.id.img_logo_nav));
                }

            }

            if (mCompanyOfCustomer.getName() != null) {
                ((TextView) header.findViewById(R.id.txt_name_nav)).setText(mCompanyOfCustomer.getName());
            }

            if (mCompanyOfCustomer.getAddress() != null) {
                ((TextView) header.findViewById(R.id.txt_email_nav)).setText(mCompanyOfCustomer.getAddress());
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCoupon(final int position) {
        final String id = mCompanyOfCustomer.getCoupon().get(position).getCoupon_id();
        Until coupon = new Until();
        coupon.setCoupon_id(id);

        Call<Integer> deleteCoupon = MainApplication.getAPI().useCoupon(coupon);
        deleteCoupon.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == MainApplication.SUCCESS) {
                    DatabaseManager.deleteCouponById(id);
                    getShowMessages(getString(R.string.delete_coupon_success));
                    adapter.remove(position);
                } else {
                    getShowMessages(getString(R.string.delete_coupon_fail));
                }

                Log.d(TAG, response.body() + "");
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, "deleteCoupon " + t.toString());
            }
        });
    }

    private void getShowMessages(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
