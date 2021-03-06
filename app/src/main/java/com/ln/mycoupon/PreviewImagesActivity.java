package com.ln.mycoupon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.ln.app.MainApplication;
import com.ln.fragment.shop.PreviewImagesFragment;
import com.ln.images.models.LocalMedia;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PreviewImagesActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private List<LocalMedia> mListImages = new ArrayList<>();
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_images);

        initGetData();
        initViews();
    }

    private void initGetData() {
        Log.d(TAG, "media: ");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPosition = bundle.getInt(MainApplication.POSITION);
            mListImages = Parcels.unwrap(bundle.getParcelable(MainApplication.LIST_IMAGES));
            Log.d(TAG, "media2: ");

            for (LocalMedia media : mListImages) {
                Log.d(TAG, "media: " + media.getPath());
            }
        }
    }

    private void initViews() {

        getSupportActionBar().setTitle(R.string.show_images);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.preview_pager);
        PreviewAdapter mPreviewAdapter =
                new PreviewAdapter(getSupportFragmentManager(), mListImages);

        mViewPager.setAdapter(mPreviewAdapter);
        mViewPager.setCurrentItem(mPosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class PreviewAdapter extends FragmentPagerAdapter {

        private List<LocalMedia> mListLocalMedia;

        PreviewAdapter(FragmentManager fm, List<LocalMedia> localMedias) {
            super(fm);
            mListLocalMedia = localMedias;
        }

        @Override
        public Fragment getItem(int position) {
            return PreviewImagesFragment.getInstance(mListLocalMedia.get(position).getPath());
        }

        @Override
        public int getCount() {
            return mListLocalMedia.size();
        }
    }
}
