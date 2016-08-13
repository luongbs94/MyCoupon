package com.ln.images.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ln.app.MainApplication;
import com.ln.images.adapter.ImageCheckAdapter;
import com.ln.images.models.FileUtils;
import com.ln.images.models.GridSpacingItemDecoration;
import com.ln.images.models.ImagesManager;
import com.ln.images.models.LocalMedia;
import com.ln.images.models.ScreenUtils;
import com.ln.mycoupon.R;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagesCheckActivity extends AppCompatActivity {

    private static final int spanCount = 3;
    private static final int REQUEST_CAMERA = 66;
    public static final String REQUEST_OUTPUT = "REQUEST_OUTPUT";
    private final static String BUNDLE_CAMERA_PATH = "CameraPath";
    private ImageCheckAdapter mAdapter;
    private TextView mTextDone;
    private String cameraPath;
    private static final int maxSelectNum = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_check);

        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH);
        }
        initViews();
    }

    private void initViews() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rec_select_images);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, ScreenUtils.dip2px(this, 2), false));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        mAdapter = new ImageCheckAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mTextDone = (TextView) findViewById(R.id.text_done);
        mTextDone.setText(getString(R.string.text_done, 0 + ""));

        mAdapter.setOnImageSelectChangedListener(new ImageCheckAdapter.OnImageSelectChangedListener() {
            @Override
            public void onChange(List<LocalMedia> selectImages) {
                boolean enable = selectImages.size() != 0;
                mTextDone.setEnabled(enable);
                if (enable) {
                    mTextDone.setText(getString(R.string.text_done, selectImages.size() + ""));

                } else {
                    mTextDone.setText(getString(R.string.text_done, 0 + ""));
                }
            }

            @Override
            public void onTakePhoto() {
                startCamera();
            }

        });
        mTextDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDone(mAdapter.getSelectedImages());
            }
        });

    }

    private void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = FileUtils.createCameraFile(this);
            cameraPath = cameraFile.getAbsolutePath();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }


    private void onSelectDone(List<LocalMedia> medias) {
        ArrayList<String> images = new ArrayList<>();
        for (LocalMedia media : medias) {
            images.add(media.getPath());
        }
        onResult(images);
    }

    private void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        onResult(images);
    }

    private void onResult(ArrayList<String> images) {
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainApplication.LIST_IMAGES, Parcels.wrap(images));
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(cameraPath))));
                onSelectDone(cameraPath);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath);
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
}
