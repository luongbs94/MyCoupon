package com.ln.mycoupon.customer;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.adapter.CouponTemplateClientAdapter;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company1;
import com.ln.mycoupon.R;

/**
 * Created by luongnguyen on 6/7/16.
 * <p>
 * coupon
 */
public class CouponCompanyOfClientActivity extends AppCompatActivity {

    private RecyclerView mRecCoupon;
    private static Company1 company1;
    private ImageView mImageView;
    private TextView mTxtName;

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_company_client);

        initViews();


    }

    private void initViews() {

        int position = getIntent().getExtras().getInt("position");
        company1 = SaveData.listCompany.get(position);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        mImageView = (ImageView) findViewById(R.id.img_logo_customer_nav);
        mTxtName = (TextView) findViewById(R.id.txt_name_customer_nav);

        if (company1.getLogo() != null) {
            Glide.with(this).load(MainApplication.convertToBytes(company1.getLogo()))
                    .placeholder(R.drawable.ic_logo_blank)
                    .into(mImageView);

            Log.d(TAG, company1.getLogo());
        }

        if (company1.getName() != null) {
            mTxtName.setText(company1.getName());
        }

        mRecCoupon = (RecyclerView) findViewById(R.id.recycler_view);
        mRecCoupon.setHasFixedSize(true);
        mRecCoupon.setLayoutManager(new LinearLayoutManager(this));

        CouponTemplateClientAdapter adapter = new CouponTemplateClientAdapter(this, company1.getCoupon());
        mRecCoupon.setAdapter(adapter);
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
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    isShow = false;
                }
            }
        });
    }
}
