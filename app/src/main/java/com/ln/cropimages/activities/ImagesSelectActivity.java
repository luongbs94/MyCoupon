package com.ln.cropimages.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ln.cropimages.adapter.ImagesSelectAdapter;
import com.ln.mycoupon.R;
import com.yongchun.library.utils.GridSpacingItemDecoration;
import com.yongchun.library.utils.ScreenUtils;
import com.yongchun.library.view.ImageCropActivity;

import java.util.ArrayList;

public class ImagesSelectActivity extends AppCompatActivity {

    public final static int REQUEST_IMAGE = 66;
    public final static int REQUEST_CAMERA = 67;
    public final static String REQUEST_OUTPUT = "outputList";
    private RecyclerView mRecyclerImages;
    private static final int mCountColumn = 3;
    private ImagesSelectAdapter mAdapter;
    private String cameraPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_select);

        initViews();
    }

    private void initViews() {
        mRecyclerImages = (RecyclerView) findViewById(R.id.rec_select_images);
        mRecyclerImages.setHasFixedSize(true);
        mRecyclerImages.addItemDecoration(new GridSpacingItemDecoration(mCountColumn,
                ScreenUtils.dip2px(this, 2), false));
        mRecyclerImages.setLayoutManager(new GridLayoutManager(this, mCountColumn));

        mAdapter = new ImagesSelectAdapter(this);
        mRecyclerImages.setAdapter(mAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ImageCropActivity.REQUEST_CROP) {
                String path = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);
                onSelectDone(path);
            }
        }
    }


    public void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        onResult(images);
    }

    public void onResult(ArrayList<String> images) {
        setResult(RESULT_OK, new Intent().putStringArrayListExtra(REQUEST_OUTPUT, images));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
