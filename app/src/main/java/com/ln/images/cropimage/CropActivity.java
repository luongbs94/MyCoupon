package com.ln.images.cropimage;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ln.app.MainApplication;
import com.ln.images.models.LocalMedia;
import com.ln.mycoupon.R;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Bundle bundle = getIntent().getExtras();

        LocalMedia item = (LocalMedia) bundle.getSerializable(MainApplication.LINK_IMAGES);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, MainFragment.getInstance(item.getPath()))
                    .commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void startResultActivity(Uri uri) {
        Intent intent = getIntent();
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        finish();
    }
}
