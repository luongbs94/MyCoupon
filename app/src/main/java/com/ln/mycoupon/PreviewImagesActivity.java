package com.ln.mycoupon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ln.model.LocalMediaStore;

import java.util.ArrayList;
import java.util.List;

public class PreviewImagesActivity extends AppCompatActivity {

    private List<LocalMediaStore> mListLocalImages = new ArrayList<>();
    private List<LocalMediaStore> mListSelect = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_images);
    }
}
