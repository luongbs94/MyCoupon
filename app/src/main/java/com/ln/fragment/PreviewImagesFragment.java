package com.ln.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ln.app.MainApplication;
import com.ln.mycoupon.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviewImagesFragment extends Fragment {

    private View mView;
    private ImageView mImagePreview;
    private String TAG = getClass().getSimpleName();


    public static PreviewImagesFragment getInstance(String path) {
        PreviewImagesFragment fragment = new PreviewImagesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MainApplication.PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }


    public PreviewImagesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_preview_images, container, false);
        mImagePreview = (ImageView) mView.findViewById(R.id.image_preview);

        String string = getArguments().getString(MainApplication.PATH);
        string = MainApplication.getStringNoBase64(string);

        Log.d(TAG, string);
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImagePreview);




        Glide.with(container.getContext())
                .load(string)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(480, 800) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mImagePreview.setImageBitmap(resource);
                        mAttacher.update();
                    }
                });
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
            }
        });
        return mView;
    }

}
