package com.ln.mycoupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ln.app.MainApplication;
import com.ln.fragment.PreviewImagesFragment;

import java.util.ArrayList;
import java.util.List;

public class PreviewImagesActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private int mPosition;
    private List<String> mListImages = new ArrayList<>();

    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_images);


        initGetData();
        initViews();

        addEvents();
    }

    private void initGetData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MainApplication.DATA);
        if (bundle != null) {
            mPosition = bundle.getInt(MainApplication.POSITION);
            Log.d(TAG, mPosition + "");
            mListImages = (List<String>) bundle.getSerializable(MainApplication.LIST_IMAGES);
        }
    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.preview_pager);
        mViewPager.setAdapter(new PreviewAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(mPosition);
    }

    private void addEvents() {

//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

    private class PreviewAdapter extends FragmentPagerAdapter {


        PreviewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PreviewImagesFragment.getInstance(mListImages.get(position));
        }

        @Override
        public int getCount() {
            return mListImages.size();
        }
    }
}
