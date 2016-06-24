package com.ln.mycoupon.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.fragment.shop.CouponFragment;
import com.ln.fragment.shop.HistoryFragment;
import com.ln.fragment.shop.NewsFragment;
import com.ln.fragment.shop.SettingFragment;
import com.ln.fragment.shop.ShareFragment;
import com.ln.interfaces.OnClickLogoutGoogle;
import com.ln.model.Company;
import com.ln.mycoupon.AddCouponActivity;
import com.ln.mycoupon.AddMessageActivity;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.R;

public class ShopMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = getClass().getSimpleName();

    private int currentPosition = 0;
    private static String sTitle;

    private FloatingActionButton mFbButton;
    private DrawerLayout mDrawerLayout;

    private OnClickLogoutGoogle mOnClickLogoutGoogle;

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


        sTitle = getString(R.string.my_coupon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(sTitle);

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
                    default:
                        break;
                }
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {

            navigationView.setNavigationItemSelectedListener(this);
            View headView = navigationView.getHeaderView(0);
            ImageView mImageLogo = (ImageView) headView.findViewById(R.id.img_logo_nav);
            TextView mTxtNameCompany = (TextView) headView.findViewById(R.id.txt_name_nav);
            TextView mTxtAddress = (TextView) headView.findViewById(R.id.txt_email_nav);

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
        }

        if (!MainApplication.sIsAdmin) {
            mFbButton.setVisibility(View.GONE);
        }
        startFragment(new CouponFragment());
    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
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
                break;
            case R.id.menu_share:
                sTitle = getString(R.string.love_coupon);
                fragment = new ShareFragment();
                break;
            case R.id.logout:
                MainApplication.editor.putBoolean(MainApplication.LOGIN_SHOP, false);
                MainApplication.editor.commit();

                if (MainApplication.TYPE_LOGIN_SHOP == MainApplication.TYPE_GOOGLE) {
                    SaveData.company = null;
                } else if (MainApplication.TYPE_LOGIN_SHOP == MainApplication.TYPE_FACEBOOK) {
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    LoginManager.getInstance().logOut();
                    MainApplication.sShopDetail = null;
                } else if (MainApplication.TYPE_LOGIN_SHOP == MainApplication.TYPE_NORMAL) {
                    mOnClickLogoutGoogle = new OnClickLogoutGoogle() {
                        @Override
                        public void onClickLogout() {
                            new ShopLoginActivity().onClickLogoutGoogle();
                        }
                    };
                    MainApplication.sShopDetail = null;
                }
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
