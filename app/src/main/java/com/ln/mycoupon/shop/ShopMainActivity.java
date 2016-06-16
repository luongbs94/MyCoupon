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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.fragment.shop.CouponFragment;
import com.ln.fragment.shop.HistoryFragment;
import com.ln.fragment.shop.NewsFragment;
import com.ln.fragment.shop.SettingFragment;
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


        Company company = SaveData.getCompany();

        if (company != null) {

            if (company.getUser_id() != null
                    || (company.getUser1_admin() != null && company.getUser1_admin().equals("1"))
                    || (company.getUser2_admin() != null && company.getUser2_admin().equals("1"))) {
                MainApplication.sIsAdmin = true;
            }
        }


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

        if (company != null && company.getLogo() != null) {
            Glide.with(this).load(MainApplication
                    .convertToBytes(company.getLogo()))
                    .into(mImageLogo);
            Log.d(TAG, company.getLogo());
        }
        if (company != null && company.getName() != null) {
            mTxtNameCompany.setText(company.getName());
        }
        if (company != null && company.getAddress() != null) {
            mTxtAddress.setText(company.getAddress());
        }

        if (!MainApplication.sIsAdmin) {
            mFbButton.setVisibility(View.GONE);
        }
        startFragment(new CouponFragment());

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = new CouponFragment();
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_coupon:
                currentPosition = 0;
                setTitle(getString(R.string.my_coupon));
                fragment = new CouponFragment();
                break;
            case R.id.nav_new:
                currentPosition = 1;
                setTitle(getString(R.string.news));
                fragment = new NewsFragment();
                break;
            case R.id.nav_history:
                currentPosition = 2;
                setTitle(getString(R.string.history));
                fragment = new HistoryFragment();
                break;
            case R.id.nav_manage:
                currentPosition = 2;
                mFbButton.setVisibility(View.GONE);
                setTitle(getString(R.string.setting));
                fragment = new SettingFragment();
                break;
            case R.id.logout:
                MainApplication.editor.putBoolean(MainApplication.LOGINSHOP, false);
                MainApplication.editor.commit();

                finish();

                SaveData.company = null;
                MainApplication.sIsAdmin = false;

                break;
            default:
                break;
        }

        if (MainApplication.sIsAdmin) {
            if (id == R.id.nav_coupon || id == R.id.nav_new) {
                mFbButton.setVisibility(View.VISIBLE);
            } else if (R.id.nav_history == id || R.id.nav_manage == id || R.id.nav_view == id) {
                mFbButton.setVisibility(View.GONE);
            }
        } else {
            mFbButton.setVisibility(View.GONE);
        }

        startFragment(fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void startFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_main, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }
}
