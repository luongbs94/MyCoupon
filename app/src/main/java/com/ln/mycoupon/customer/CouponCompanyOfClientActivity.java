package com.ln.mycoupon.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.adapter.CouponTemplateClientAdapter;
import com.ln.app.MainApplication;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.Coupon;
import com.ln.mycoupon.R;
import com.ln.realm.RealmController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luongnguyen on 6/7/16.
 * <p>
 * coupon
 */
public class CouponCompanyOfClientActivity extends AppCompatActivity {

    private RealmController mRealmController;
    private RecyclerView mRecCoupon;
    private CompanyOfCustomer mCompanyOfCustomer;
    private ImageView mImageView;
    private TextView mTxtName;

    private final String TAG = getClass().getSimpleName();

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        mImageView = (ImageView) findViewById(R.id.img_logo_customer_nav);
        mTxtName = (TextView) findViewById(R.id.txt_name_customer_nav);

        if (mCompanyOfCustomer.getLogo() != null) {
            Glide.with(this).load(MainApplication.convertToBytes(mCompanyOfCustomer.getLogo()))
                    .asBitmap()
                    .into(mImageView);

            Log.d(TAG, mCompanyOfCustomer.getLogo());
        }

        if (mCompanyOfCustomer.getName() != null) {
            mTxtName.setText(mCompanyOfCustomer.getName());
        }

        mRecCoupon = (RecyclerView) findViewById(R.id.recycler_view);
        mRecCoupon.setHasFixedSize(true);
        mRecCoupon.setLayoutManager(new LinearLayoutManager(this));

        CouponTemplateClientAdapter adapter = new CouponTemplateClientAdapter(this, mCompanyOfCustomer);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
}
