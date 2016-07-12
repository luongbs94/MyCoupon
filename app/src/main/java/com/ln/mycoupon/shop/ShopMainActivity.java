package com.ln.mycoupon.shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.fragment.shop.CouponFragment;
import com.ln.fragment.shop.HistoryFragment;
import com.ln.fragment.shop.NewsFragment;
import com.ln.fragment.shop.SettingFragment;
import com.ln.fragment.shop.ShareFragment;
import com.ln.interfaces.OnClickSetInformation;
import com.ln.model.Company;
import com.ln.mycoupon.AddCouponActivity;
import com.ln.mycoupon.AddMessageActivity;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.R;

public class ShopMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnClickSetInformation, View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private int currentPosition = 0;
    private static String sTitle;

    private FloatingActionButton mFbButton;
    private DrawerLayout mDrawerLayout;

    private ImageView mImageLogo;
    private TextView mTxtNameCompany, mTxtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_main);

        String strCompany = getSharedPreferences(MainApplication.SHARED_PREFERENCE,
                MODE_PRIVATE).getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);

        if (company != null) {

            if (company.getUser_id() != null
                    || (company.getUser1_admin() != null
                    && company.getUser1_admin().equals("1"))
                    || (company.getUser2_admin() != null
                    && company.getUser2_admin().equals("1"))) {
                MainApplication.sIsAdmin = true;
            }

            Log.d(TAG, "Company " + company.getName());
            Log.d(TAG, "Company " + company.getLogo());
            Log.d(TAG, "Company " + company.getLogo_link());
            Log.d(TAG, "Company " + company.getAddress());
        }

        sTitle = getString(R.string.my_coupon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(sTitle);

        mFbButton = (FloatingActionButton) findViewById(R.id.fab);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        View headView = navigationView.getHeaderView(0);
        mImageLogo = (ImageView) headView.findViewById(R.id.img_logo_nav);
        mTxtNameCompany = (TextView) headView.findViewById(R.id.txt_name_nav);
        mTxtAddress = (TextView) headView.findViewById(R.id.txt_email_nav);

        if (company != null) {
            if (company.getLogo() != null) {

                Glide.with(this).load(MainApplication
                        .convertToBytes(company.getLogo()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_logo_blank)
                        .into(mImageLogo);


            } else if (company.getLogo() == null && company.getLogo_link() != null) {

                Glide.with(this).load(company.getLogo_link())
                        .placeholder(R.drawable.ic_logo_blank)
                        .into(mImageLogo);
                Log.d(TAG, "Logo " + company.getLogo_link());
                Log.d(TAG, "Logo " + MainApplication.getStringNoBase64(company.getLogo()));
            }

            if (company.getName() != null) {
                mTxtNameCompany.setText(company.getName());
            } else {
                mTxtNameCompany.setText("");
            }
            if (company.getAddress() != null) {
                mTxtAddress.setText(company.getAddress());
            } else {
                mTxtAddress.setText("");
            }
        }

//        if (!MainApplication.sIsAdmin) {
//            mFbButton.setVisibility(View.GONE);
//        }

        if (company != null && company.getName() != null) {
            startFragment(new CouponFragment());
        } else {
            startFragment(new SettingFragment());
        }

        mFbButton.setVisibility(View.GONE);
        mFbButton.setOnClickListener(this);
        if (MainApplication.sIsAdmin) {
            mFbButton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            MaterialDialog.Builder dialog = new MaterialDialog.Builder(this);
            dialog.content(R.string.exit_alert)
                    .positiveText(R.string.agree)
                    .negativeText(R.string.disagree)
                    .positiveColor(getResources().getColor(R.color.title_bg))
                    .negativeColor(getResources().getColor(R.color.title_bg))
                    .show();

            dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    finish();
                }
            });

            dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            });
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
                sTitle = getString(R.string.my_coupon);
                fragment = new CouponFragment();
                break;
            case R.id.nav_new:
                currentPosition = 1;
                sTitle = getString(R.string.news);
                fragment = new NewsFragment();
                break;
            case R.id.nav_history:
                sTitle = getString(R.string.history);
                fragment = new HistoryFragment();
                break;
            case R.id.nav_manage:
                sTitle = getString(R.string.setting);
                fragment = new SettingFragment();
                SettingFragment.setListener(this);
                break;
            case R.id.menu_share:
                sTitle = getString(R.string.love_coupon);
                fragment = new ShareFragment();
                break;
            case R.id.logout:
                MainApplication.editor.putBoolean(MainApplication.LOGIN_SHOP, false);
                MainApplication.editor.commit();

                MainApplication.sIsAdmin = false;

                Intent intent = new Intent(this, FirstActivity.class);
                startActivity(intent);

                finish();
                break;
            default:
                break;
        }

        if (MainApplication.sIsAdmin) {
            if (id == R.id.nav_coupon || id == R.id.nav_new) {
                mFbButton.setVisibility(View.VISIBLE);
            } else if (R.id.nav_history == id || R.id.nav_manage == id || R.id.nav_view == id || id == R.id.menu_share) {
                mFbButton.setVisibility(View.GONE);
            }
        } else if (!MainApplication.sIsAdmin) {
            mFbButton.setVisibility(View.GONE);
        }

        setTitle(sTitle);
        startFragment(fragment);

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startFragment(Fragment fragment) {
//        String backStateName = fragment.getClass().getName();
//        String fragmentTag = backStateName;
//
//        FragmentManager manager = getSupportFragmentManager();
//        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
//
//        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
//            FragmentTransaction ft = manager.beginTransaction();
//            ft.replace(R.id.content_main, fragment, fragmentTag);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//            ft.commit();
//        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();
    }

    @Override
    public void onClickSetInformation(String logo, String name, String address) {
        if (mImageLogo != null) {
            Glide.with(this).load(MainApplication.convertToBytes(logo))
                    .asBitmap()
                    .placeholder(R.drawable.ic_logo_blank).into(mImageLogo);
        }

        if (mTxtNameCompany != null) {
            mTxtNameCompany.setText(name);
        }
        if (mTxtAddress != null) {
            mTxtAddress.setText(address);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            switch (currentPosition) {
                case 0:
                    Intent intent = new Intent(ShopMainActivity.this, AddCouponActivity.class);
                    startActivityForResult(intent, MainApplication.ADD_COUPON_TEMPLATE);

                    return;
                case 1:
                    Intent intent1 = new Intent(ShopMainActivity.this, AddMessageActivity.class);
                    startActivityForResult(intent1, MainApplication.ADD_MESSAGES);
                    return;
                default:
                    break;
            }
        }
    }

    private void writeBooleanShare(String key, boolean isValue) {
        SharedPreferences.Editor editor = getSharedPreferences(
                MainApplication.SHARED_PREFERENCE, MODE_PRIVATE).edit();
        editor.putBoolean(key, isValue);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);

            switch (requestCode) {
                case MainApplication.ADD_COUPON_TEMPLATE:
                    if (fragment instanceof CouponFragment) {
                        ((CouponFragment) fragment).getCouponTemplate();
                    }
                    break;
                case MainApplication.ADD_MESSAGES:
                    if (fragment instanceof NewsFragment) {
                        ((NewsFragment) fragment).setNewsOfCompany();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
