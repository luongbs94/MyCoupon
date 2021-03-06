package com.ln.mycoupon.shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.fragment.shop.CouponFragment;
import com.ln.fragment.shop.HistoryFragment;
import com.ln.fragment.shop.NewsFragment;
import com.ln.fragment.shop.SettingFragment;
import com.ln.fragment.shop.ShareFragment;
import com.ln.model.Company;
import com.ln.mycoupon.AddCouponActivity;
import com.ln.mycoupon.AddMessageActivity;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.R;

public class ShopMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SettingFragment.OnClickSetInformation, View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int NETWORK = 100;
    private final String TAG = getClass().getSimpleName();

    private int currentPosition = 0;
    private static String sTitle;

    private FloatingActionButton mFbButton;
    private DrawerLayout mDrawerLayout;

    private ImageView mImageLogo;
    private TextView mTxtNameCompany;
    private Snackbar mSnackbar;

    private int mStartNotification = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_main);

        checkNetwork();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == NETWORK) {
                    if (mSnackbar != null) {
                        mSnackbar.dismiss();
                    }
                }
            }
        };

        String strCompany = getSharedPreferences(MainApplication.SHARED_PREFERENCE,
                MODE_PRIVATE).getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);


        Log.d(TAG, "web token: " + company.getWeb_token());
        Log.d(TAG, "web companyId: " + company.getCompany_id());
        Log.d(TAG, "web userId: " + company.getUser_id());
        Log.d(TAG, "web user1: " + company.getUser1());
        Log.d(TAG, "web user2: " + company.getUser2());

        getDataFromIntent();

        sTitle = getString(R.string.coupon);
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
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(mImageLogo);
                Log.d(TAG, "Logo " + company.getLogo_link());
                Log.d(TAG, "Logo " + MainApplication.getStringNoBase64(company.getLogo()));
            }

            if (company.getName() != null) {
                mTxtNameCompany.setText(company.getName());
            } else {
                mTxtNameCompany.setText("");
            }
        }


        Fragment fragment;
        int intFragment = 0;
        if (mStartNotification == MainApplication.NOTIFICATION) {
            fragment = new NewsFragment();
            intFragment = 0;
            sTitle = getString(R.string.news);
        } else if (company != null && company.getName() != null) {
            fragment = new CouponFragment();
            intFragment = 1;
            sTitle = getString(R.string.coupon);
        } else {
            fragment = new SettingFragment();
            sTitle = getString(R.string.setting);
            intFragment = 2;
        }

        startFragment(fragment);
        mFbButton.setVisibility(View.GONE);
        mFbButton.setOnClickListener(this);
        if (MainApplication.getPreferences().getBoolean(MainApplication.ADMIN, false)
                && intFragment != 2) {
            mFbButton.setVisibility(View.VISIBLE);
        }
    }

    private void getDataFromIntent() {
        try {
            Intent intent = getIntent();
            mStartNotification = intent.getIntExtra(MainApplication.PUSH_NOTIFICATION, 1);
        } catch (NullPointerException e) {
            Log.d(TAG, "intent null " + e.toString());
        }

    }

    private boolean isClose;

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!isClose) {
                getShowMessages(getString(R.string.press_back_again));
                isClose = true;
            } else if (isClose) {
                super.onBackPressed();
                isClose = false;
            }
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
                sTitle = getString(R.string.coupon);
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

                Intent intent = new Intent(this, FirstActivity.class);
                startActivity(intent);

                finish();
                break;
            default:
                break;
        }

        if (MainApplication.getPreferences().getBoolean(MainApplication.ADMIN, false)) {
            if (id == R.id.nav_coupon || id == R.id.nav_new) {
                mFbButton.setVisibility(View.VISIBLE);
            } else if (R.id.nav_history == id || R.id.nav_manage == id || R.id.nav_view == id || id == R.id.menu_share) {
                mFbButton.setVisibility(View.GONE);
            }
        } else if (!MainApplication.getPreferences().getBoolean(MainApplication.ADMIN, false)) {
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

    @Override
    public void onClickSetInformation(String logo, String name, String address) {
        if (mImageLogo != null && logo != null) {
            Glide.with(this)
                    .load(MainApplication.convertToBytes(logo))
                    .asBitmap()
                    .placeholder(R.drawable.ic_logo_blank)
                    .into(mImageLogo);
        }

        if (mTxtNameCompany != null) {
            mTxtNameCompany.setText(name);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            switch (currentPosition) {
                case 0:
                    boolean isNetwork = ConnectivityReceiver.isConnect();
                    if (isNetwork) {
                        Intent intent = new Intent(ShopMainActivity.this, AddCouponActivity.class);
                        startActivityForResult(intent, MainApplication.ADD_COUPON_TEMPLATE);
                    } else {
                        getShowMessages(getString(R.string.check_network));
                    }
                    return;
                case 1:
                    boolean isNetwork1 = ConnectivityReceiver.isConnect();
                    if (isNetwork1) {
                        Intent intent1 = new Intent(ShopMainActivity.this, AddMessageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(MainApplication.WHAT_ADD_MESSAGES, MainApplication.WHAT_SHOP_MAIN_ADD_NEWS);
                        intent1.putExtras(bundle);
                        startActivityForResult(intent1, MainApplication.ADD_MESSAGES);
                    } else {
                        getShowMessages(getString(R.string.check_network));
                    }

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
                case MainApplication.WHAT_UPDATE_NEWS:
                    if (fragment instanceof NewsFragment) {
                        ((NewsFragment) fragment).setNewsOfCompany();
                    }

                    Log.d(TAG, "change mes");
                    break;
                default:
                    break;
            }
        }
    }

    private void getShowMessages(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void checkNetwork() {
        boolean isNetwork = ConnectivityReceiver.isConnect();
        showCheckNetwork(isNetwork);
    }


    @Override
    public void onNetworkConnectChange(boolean isConnect) {
        showCheckNetwork(isConnect);
    }

    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean isNetwork = ConnectivityReceiver.isConnect();
            while (!isNetwork) {
                SystemClock.sleep(100);
                isNetwork = ConnectivityReceiver.isConnect();
            }

            Message message = new Message();
            message.what = NETWORK;
            message.setTarget(handler);
            message.sendToTarget();
        }
    };

    private void showCheckNetwork(boolean isNetwork) {
        if (!isNetwork) {
            mSnackbar = Snackbar.make(findViewById(R.id.drawer_layout), R.string.check_network, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", null);
            View view = mSnackbar.getView();
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackbar));
            mSnackbar.show();

            new Thread(runnable).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.getInstance().setConnectivityListener(this);
    }

}
