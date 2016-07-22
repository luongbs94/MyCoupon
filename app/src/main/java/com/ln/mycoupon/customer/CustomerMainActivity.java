package com.ln.mycoupon.customer;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.broadcast.ConnectivityReceiverListener;
import com.ln.fragment.customer.CouponFragment;
import com.ln.fragment.customer.MoreNewsFragment;
import com.ln.fragment.customer.NewsCustomerFragment;
import com.ln.fragment.shop.ShareFragment;
import com.ln.interfaces.OnClickLogoutGoogle;
import com.ln.model.AccountOflUser;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.QRCodeActivity;
import com.ln.mycoupon.R;

public class CustomerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiverListener {

    private static String sTitle;

    private FloatingActionButton mFabButton;
    private DrawerLayout mDrawerLayout;
    public static OnClickLogoutGoogle mOnClickLogoutGoogle;
    private TextView mTxtConnectNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);


        sTitle = getString(R.string.my_coupon);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(sTitle);


        mFabButton = (FloatingActionButton) findViewById(R.id.fab);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerMainActivity.this, QRCodeActivity.class);
                startActivityForResult(intent, MainApplication.START_QRCODE);

            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) headerView.findViewById(R.id.img_logo_customer_nav);
        TextView txt = (TextView) headerView.findViewById(R.id.txt_name_customer_nav);


        startFragment(new CouponFragment());

        String strCompany = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
        AccountOflUser accountOflUser = new Gson().fromJson(strCompany, AccountOflUser.class);

        if (accountOflUser != null) {

            if (accountOflUser.getPicture() != null) {
                Glide.with(this).load(accountOflUser.getPicture()).into(imageView);
            }

            if (accountOflUser.getName() != null) {
                txt.setText(accountOflUser.getName());
            }
        }


        mTxtConnectNetwork = (TextView) findViewById(R.id.txt_network);
        checkNetwork();
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

        int id = item.getItemId();

        Fragment fragment = new CouponFragment();
        switch (id) {
            case R.id.nav_coupon:
                sTitle = getString(R.string.my_coupon);
                fragment = new CouponFragment();
                break;
            case R.id.nav_new:
                sTitle = getString(R.string.news);
                fragment = new NewsCustomerFragment();
                break;
            case R.id.nav_new_more:
                sTitle = getString(R.string.news_more);
                fragment = new MoreNewsFragment();
                break;
            case R.id.menu_share:
                sTitle = getString(R.string.love_coupon);
                fragment = new ShareFragment();
                break;
            case R.id.logout:
            default:

                if (MainApplication.TYPE_LOGIN_CUSTOMER == MainApplication.TYPE_FACEBOOK) {
                    LoginManager.getInstance().logOut();
                } else if (MainApplication.TYPE_LOGIN_CUSTOMER == MainApplication.TYPE_GOOGLE) {

                }

//                MainApplication.sDetailUser = null;
                MainApplication.editor.putBoolean(MainApplication.LOGIN_CLIENT, false);
                MainApplication.editor.commit();
                Intent intent = new Intent(this, FirstActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        if (id == R.id.nav_coupon) {
            mFabButton.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_new || id == R.id.menu_share || id == R.id.logout) {
            mFabButton.setVisibility(View.GONE);
        }
        startFragment(fragment);
        getSupportActionBar().setTitle(sTitle);
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


    /* ============ Check network ==============*/
    @Override
    public void onNetworkConnectChange(boolean isConnect) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == MainApplication.START_QRCODE) {
                Bundle bundle = data.getExtras();
                Intent intent = new Intent(this, CouponCompanyOfClientActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }


    private void checkNetwork() {
        boolean isNetWork = ConnectivityReceiver.isConnect();
        showConnectNetWork(isNetWork);
    }

    private void showConnectNetWork(boolean isNetWork) {
        if (mTxtConnectNetwork != null) {
            if (isNetWork) {
                mTxtConnectNetwork.setVisibility(View.GONE);
            } else {
                mTxtConnectNetwork.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.getInstance().setConnectivityListener(this);
    }
}
