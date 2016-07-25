package com.ln.mycoupon.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.ln.interfaces.OnClickRecyclerView;
import com.ln.interfaces.RecyclerViewListener;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.Coupon;
import com.ln.mycoupon.R;
import com.ln.realm.RealmController;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 6/7/16.
 * <p/>
 * coupon
 */
public class CouponCompanyOfClientActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private RealmController mRealmController;
    private CompanyOfCustomer mCompanyOfCustomer;
    private CouponTemplateClientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_company_client);

        mRealmController = MainApplication.mRealmController;
        initViews();
    }

    private void initViews() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String idCompany = bundle.getString(MainApplication.ID_COMPANY);

        mCompanyOfCustomer = mRealmController.getCompanyOfCustomer(idCompany);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

        ImageView mImageView = (ImageView) findViewById(R.id.img_logo_customer_nav);
        TextView mTxtName = (TextView) findViewById(R.id.txt_name_customer_nav);


        if (mCompanyOfCustomer.getLogo() != null) {
            String strLogo = mCompanyOfCustomer.getLogo();
            strLogo = strLogo.substring(0, 4);
            if (strLogo.equals(MainApplication.LOGO)) {
                Glide.with(this).load(MainApplication.convertToBytes(mCompanyOfCustomer.getLogo()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_logo_blank)
                        .into(mImageView);
            } else {
                Glide.with(this).load(mCompanyOfCustomer.getLogo())
                        .placeholder(R.drawable.ic_logo_blank)
                        .into(mImageView);
            }

            Log.d(TAG, mCompanyOfCustomer.getLogo());
        }

        if (mCompanyOfCustomer.getName() != null) {
            mTxtName.setText(mCompanyOfCustomer.getName());
        }

        RecyclerView mRecCoupon = (RecyclerView) findViewById(R.id.recycler_view);
        mRecCoupon.setHasFixedSize(true);
        mRecCoupon.setLayoutManager(new LinearLayoutManager(this));
        mRecCoupon.addOnItemTouchListener(new RecyclerViewListener(this, new OnClickRecyclerView() {
            @Override
            public void onClick(View view, final int position) {
                new MaterialDialog
                        .Builder(CouponCompanyOfClientActivity.this)
                        .content(R.string.delete_coupon)
                        .positiveText(R.string.agree)
                        .negativeText(R.string.disagree)
                        .positiveColor(getResources().getColor(R.color.title_bg))
                        .negativeColor(getResources().getColor(R.color.title_bg))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                deleteCoupon(position);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }));
        adapter = new CouponTemplateClientAdapter(this, mCompanyOfCustomer);
        mRecCoupon.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCollapsingToolbar() {

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(" ");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void deleteCoupon(int position) {
        final String id = mCompanyOfCustomer.getCoupon().get(position).getCoupon_id();
        Coupon coupon = new Coupon();
        coupon.setCoupon_id(id);

        Call<Integer> deleteCoupon = MainApplication
                .getAPI()
                .useCoupon(coupon);

        deleteCoupon.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == MainApplication.SUCCESS) {
                    MainApplication.mRealmController.deleteCoupon(id);
                    getShowMessages(getString(R.string.delete_coupon_fail));
                    adapter.notifyDataSetChanged();
                } else {
                    getShowMessages(getString(R.string.delete_coupon_fail));
                }
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
