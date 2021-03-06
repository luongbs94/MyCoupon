package com.ln.mycoupon.customer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import com.ln.fragment.customer.CouponFragment;
import com.ln.fragment.customer.NewsCustomerFragment;
import com.ln.fragment.shop.ShareFragment;
import com.ln.model.AccountOfUser;
import com.ln.mycoupon.FirstActivity;
import com.ln.mycoupon.R;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CustomerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int NETWORK = 1000;
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private final String TAG = getClass().getSimpleName();
    private static String sTitle;

    private FloatingActionButton mFabButton;
    private DrawerLayout mDrawerLayout;
    private Snackbar mSnackbar;
    private int mStartNotification = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

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


        AccountOfUser account = new Gson()
                .fromJson(MainApplication
                        .getPreferences()
                        .getString(MainApplication.ACCOUNT_CUSTOMER, ""), AccountOfUser.class);

        Log.d("MyFirebaseIIDService", account.getId() + "  " + MainApplication.getDeviceToken());

        getDataFromIntent();

        sTitle = getString(R.string.my_coupon);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(sTitle);


        mFabButton = (FloatingActionButton) findViewById(R.id.fab);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerMainActivityPermissionsDispatcher.showCameraWithCheck(CustomerMainActivity.this);
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

        if (mStartNotification == MainApplication.NOTIFICATION) {
            sTitle = getString(R.string.news);

            startFragment(NewsCustomerFragment.getInstances(MainApplication.TYPE_NEWS));
        } else {
            sTitle = getString(R.string.coupon);
            startFragment(new CouponFragment());
        }


        String strCompany = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
        AccountOfUser accountOflUser = new Gson().fromJson(strCompany, AccountOfUser.class);

        if (accountOflUser != null) {

            if (accountOflUser.getPicture() != null) {
                Glide.with(this)
                        .load(accountOflUser.getPicture())
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }

            if (accountOflUser.getName() != null) {
                txt.setText(accountOflUser.getName());
            }
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
                showMessage(getString(R.string.press_back_again));
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

        int id = item.getItemId();

        Fragment fragment = new CouponFragment();
        switch (id) {
            case R.id.nav_coupon:
                sTitle = getString(R.string.my_coupon);
                fragment = new CouponFragment();
                break;
            case R.id.nav_new:
                sTitle = getString(R.string.news);
                fragment = NewsCustomerFragment.getInstances(MainApplication.TYPE_NEWS);

                break;
            case R.id.nav_new_more:
                sTitle = getString(R.string.news_more);
                fragment = NewsCustomerFragment.getInstances(MainApplication.TYPE_NEWS_MORE);

                break;
            case R.id.menu_share:
                sTitle = getString(R.string.love_coupon);
                fragment = new ShareFragment();
                break;
            case R.id.logout:
            default:

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
        } else {
            mFabButton.setVisibility(View.GONE);
        }
        startFragment(fragment);
        getSupportActionBar().setTitle(sTitle);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startFragment(Fragment fragment) {

//        String backStateName = fragment.getClass().getName();
//        String fragmentTag = backStateName;
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
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);

                if (fragment instanceof CouponFragment) {
                    ((CouponFragment) fragment).setListCompanyCustomer();
                }
                Intent intent = new Intent(this, ShopOfCustomerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }


    private void checkNetwork() {
        boolean isNetWork = ConnectivityReceiver.isConnect();
        showCheckNetwork(isNetWork);
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

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method

        CustomerMainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera() {
        Intent intent = new Intent(CustomerMainActivity.this, ScanQRcodeActivity.class);
        startActivityForResult(intent, MainApplication.START_QRCODE);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {
        showMessage(R.string.permission_camera_denied);
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera() {
        showMessage(R.string.permission_camera_never_askagain);
    }


}
