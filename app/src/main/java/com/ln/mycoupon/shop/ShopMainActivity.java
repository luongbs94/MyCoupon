package com.ln.mycoupon.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.fragment.HistoryFragment;
import com.ln.fragment.SettingFragment;
import com.ln.fragment.shop.CouponFragment;
import com.ln.fragment.shop.NewsFragment;
import com.ln.model.Company;
import com.ln.mycoupon.AddCouponActivity;
import com.ln.mycoupon.AddMessageActivity;
import com.ln.mycoupon.R;

public class ShopMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = getClass().getSimpleName();
    private int currentPosition = 0;
    private FloatingActionButton mFbButton;
    private ImageView mImageLogo;
    private TextView mTxtNameCompany, mTxtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFbButton = (FloatingActionButton) findViewById(R.id.fab);
        mFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (currentPosition) {
                    case 0:
                        Intent intent = new Intent(ShopMainActivity.this, AddCouponActivity.class);
                        startActivityForResult(intent, 2);
                        break;
                    case 1:
                        Intent intent1 = new Intent(ShopMainActivity.this, AddMessageActivity.class);
                        startActivityForResult(intent1, 3);
                        break;
                    case 2:

                        break;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headView = navigationView.getHeaderView(0);
        mImageLogo = (ImageView) headView.findViewById(R.id.img_logo_nav);
        mTxtNameCompany = (TextView) headView.findViewById(R.id.txt_name_nav);
        mTxtAddress = (TextView) headView.findViewById(R.id.txt_email_nav);

        Company company = SaveData.company;
        if (company.getLogo() != null) {
            Glide.with(this).load(MainApplication
                    .convertToBytes(company.getLogo()))
                    .placeholder(R.drawable.ic_profile)
                    .into(mImageLogo);
        }
        mTxtNameCompany.setText(company.getName());
        mTxtAddress.setText(company.getAddress());

        startFragment(new CouponFragment());

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_coupon:
                currentPosition = 0;
                setTitle(getString(R.string.my_coupon));
                startFragment(new CouponFragment());
                break;
            case R.id.nav_new:
                currentPosition = 1;
                setTitle(getString(R.string.news));
                startFragment(new NewsFragment());
                break;
            case R.id.nav_history:
                currentPosition = 2;
                setTitle(getString(R.string.history));
                startFragment(new HistoryFragment());
                break;
            case R.id.nav_manage:
                currentPosition = 2;
                mFbButton.setVisibility(View.GONE);
                setTitle(getString(R.string.setting));
                startFragment(new SettingFragment());
                break;
            case R.id.logout:
                finish();
                break;
            default:
                break;
        }
        if (id != R.id.nav_manage) {
            mFbButton.setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_main, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

}
