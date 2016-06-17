package com.ln.mycoupon.customer;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.ln.adapter.CouponTemplateClientAdapter;
import com.ln.api.SaveData;
import com.ln.model.Company1;
import com.ln.mycoupon.R;

/**
 * Created by luongnguyen on 6/7/16.
 * <p>
 * coupon
 */
public class CouponCompanyOfClientActivity extends AppCompatActivity {

    private LinearLayout mLnLayout;
    private RecyclerView mRecCoupon;

    private CollapsingToolbarLayout toolbarLayout;

    private Company1 company1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_coupon_company_client);


        mRecCoupon = (RecyclerView) findViewById(R.id.recycler_view);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mRecCoupon.setLayoutManager(new LinearLayoutManager(this));
        mLnLayout = (LinearLayout) findViewById(R.id.ln_fragment_coupon);

        int position = getIntent().getExtras().getInt("position");

        company1 = SaveData.listCompany.get(position);

      /*  toolbarLayout.setTitle(company1.getName());*/

        CouponTemplateClientAdapter adapter = new CouponTemplateClientAdapter(this, company1.getCoupon());
        mRecCoupon.setAdapter(adapter);

    }

}
