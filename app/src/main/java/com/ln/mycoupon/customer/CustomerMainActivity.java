package com.ln.mycoupon.customer;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.ln.app.MainApplication;
import com.ln.fragment.NewsCustomerFragment;
import com.ln.fragment.customer.CouponFragment;
import com.ln.fragment.shop.ShareFragment;
import com.ln.model.DetailUser;
import com.ln.mycoupon.QRCodeActivity;
import com.ln.mycoupon.R;

public class CustomerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = getClass().getSimpleName();
    private static String sTitle;

    private FloatingActionButton mFabButton;
    private DrawerLayout mDrawerLayout;
    private boolean isShowMenu;

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
                startActivity(intent);

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

        if (MainApplication.sDetailUser != null) {

            DetailUser detailUser = MainApplication.sDetailUser;
            if (detailUser.getPicture() != null) {

                Glide.with(this).load(detailUser.getPicture()).into(imageView);
            }
            if (detailUser.getName() != null) {
                txt.setText(MainApplication.sDetailUser.getName());
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isShowMenu) {
            getMenuInflater().inflate(R.menu.menu_shop_main, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_all_news:
                return true;
            case R.id.menu_delete_news:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
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
            case R.id.menu_share:
                sTitle = getString(R.string.love_coupon);
                fragment = new ShareFragment();
                break;
            case R.id.logout:
            default:
//                Intent intent = new Intent(this, FirstActivity.class);
//                startActivity(intent);
                if (MainApplication.TYPE_LOGIN_CUSTOMER == MainApplication.TYPE_FACEBOOK) {
                    LoginManager.getInstance().logOut();
                } else if (MainApplication.TYPE_LOGIN_CUSTOMER == MainApplication.TYPE_GOOGLE) {
                    FirebaseAuth.getInstance().signOut();
                }

                MainApplication.sDetailUser = null;
                MainApplication.editor.putBoolean(MainApplication.LOGINCLIENT, false);
                MainApplication.editor.commit();
                finish();
                break;
        }

        if (id == R.id.nav_new) {
            isShowMenu = true;
        } else {
            isShowMenu = false;
        }

        if (id == R.id.nav_coupon) {
            mFabButton.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_new || id == R.id.menu_share || id == R.id.logout) {
            mFabButton.setVisibility(View.GONE);
        }
        startFragment(fragment);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startFragment(Fragment fragment) {

        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_main, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }
}
