package com.ln.mycoupon.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.adapter.CouponTemplateClientAdapter;
import com.ln.app.MainApplication;
import com.ln.model.CompanyOfCustomer;
import com.ln.mycoupon.R;
import com.ln.realm.RealmController;

/**
 * Created by luongnguyen on 6/7/16.
 * <p/>
 * coupon
 */
public class CouponCompanyOfClientActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private RealmController mRealmController;

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


        CompanyOfCustomer mCompanyOfCustomer = mRealmController.getCompanyOfCustomer(idCompany);

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
}
