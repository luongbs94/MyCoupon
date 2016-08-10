package com.ln.images.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.ln.app.MainApplication;
import com.ln.images.adapter.ImagesCropAdapter;
import com.ln.images.cropimage.CropActivity;
import com.ln.images.models.GridSpacingItemDecoration;
import com.ln.images.models.ImagesManager;
import com.ln.images.models.LocalMedia;
import com.ln.images.models.ScreenUtils;
import com.ln.mycoupon.R;

import org.parceler.Parcels;

public class ImagesCropActivity extends AppCompatActivity
        implements ImagesCropAdapter.OnClickCropImages {

    private static final int CROP_IMAGES = 9999;
    private static final int mCountColumn = 3;
    private ImagesCropAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_select);

        initViews();
    }

    private void initViews() {

        setTitle(getString(R.string.crop_images));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerImages = (RecyclerView) findViewById(R.id.recycler_select_images);
        mRecyclerImages.setHasFixedSize(true);
        mRecyclerImages.addItemDecoration(new GridSpacingItemDecoration(mCountColumn,
                ScreenUtils.dip2px(this, 2), false));
        mRecyclerImages.setLayoutManager(new GridLayoutManager(this, mCountColumn));

        mAdapter = new ImagesCropAdapter(this);
        mAdapter.setOnClickCropImages(this);
        mRecyclerImages.setAdapter(mAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CROP_IMAGES) {
                Intent intent = getIntent();
                intent.setData(data.getData());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_all:
                mAdapter.setListImages(ImagesManager.TYPE_ALL_IMAGE);
                return true;
            case R.id.menu_external:
                mAdapter.setListImages(ImagesManager.TYPE_SD_CARD);
                return true;
            case R.id.menu_internal:
                mAdapter.setListImages(ImagesManager.TYPE_INTERNAL);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clickNextCropImages(LocalMedia localMedia) {
        Intent intent = new Intent(this, CropActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainApplication.LINK_IMAGES, Parcels.wrap(localMedia));
        intent.putExtras(bundle);
        startActivityForResult(intent, CROP_IMAGES);
    }
}
