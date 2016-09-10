package com.ln.fragment.shop;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ln.app.MainApplication;
import com.ln.mycoupon.R;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 * <></>
 */
public class PreviewImagesFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    public static PreviewImagesFragment getInstance(String path) {
        PreviewImagesFragment fragment = new PreviewImagesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MainApplication.PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_preview_images, container, false);
        final ImageView mImagePreview = (ImageView) mView.findViewById(R.id.image_preview);

        String path = getArguments().getString(MainApplication.PATH);
        Log.d(TAG, "path: " + path);

        final PhotoViewAttacher attach = new PhotoViewAttacher(mImagePreview);

        if (path.contains("http")) {
            Glide.with(container.getContext())
                    .load(path)
                    .asBitmap()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .into(new SimpleTarget<Bitmap>(480, 800) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mImagePreview.setImageBitmap(resource);
                            attach.update();
                        }
                    });
        } else {
            Glide.with(container.getContext())
                    .load(new File(path))
                    .asBitmap()
                    .fitCenter()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<Bitmap>(480, 800) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mImagePreview.setImageBitmap(resource);
                            attach.update();
                        }
                    });
        }
        attach.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
            }
        });

        setHasOptionsMenu(false);

        return mView;
    }

}
