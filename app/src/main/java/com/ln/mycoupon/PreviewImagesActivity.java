package com.ln.mycoupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ln.app.MainApplication;
import com.ln.fragment.PreviewImagesFragment;

import java.util.ArrayList;
import java.util.List;

public class PreviewImagesActivity extends AppCompatActivity {

    private int mPosition;
    private List<String> mListImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_images);

        initGetData();
        initViews();
    }

    private void initGetData() {

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MainApplication.DATA);
        if (bundle != null) {
            mPosition = bundle.getInt(MainApplication.POSITION);
            mListImages = (List<String>) bundle.getSerializable(MainApplication.LIST_IMAGES);
        }
    }

    private void initViews() {


        getSupportActionBar().setTitle(R.string.show_images);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.preview_pager);
        PreviewAdapter mPreviewAdapter = new PreviewAdapter(getSupportFragmentManager());
        mPreviewAdapter.setListImages(mListImages);

        if (mViewPager != null) {
            mViewPager.setAdapter(mPreviewAdapter);
            mViewPager.setCurrentItem(mPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class PreviewAdapter extends FragmentPagerAdapter {

        private List<String> mListStringImages = new ArrayList<>();

        PreviewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PreviewImagesFragment.getInstance(mListStringImages.get(position));
        }

        private void setListImages(List<String> listImages) {
            mListStringImages.addAll(listImages);
        }

        @Override
        public int getCount() {
            return mListStringImages.size();
        }
    }
}
