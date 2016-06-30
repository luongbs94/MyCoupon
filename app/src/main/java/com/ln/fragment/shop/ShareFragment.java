package com.ln.fragment.shop;


import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ln.mycoupon.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment {

    private FloatingActionButton mFabShare;
    private ImageView mImageEmail, mImageWeb;


    public ShareFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_share, container, false);
        initViews(view);
        addEvents();

        setHasOptionsMenu(false);


        return view;
    }

    private void initViews(View view) {

        mFabShare = (FloatingActionButton) view.findViewById(R.id.fab_share);
        mImageEmail = (ImageView) view.findViewById(R.id.image_email);
        mImageWeb = (ImageView) view.findViewById(R.id.image_web);
    }

    private void addEvents() {

        mFabShare.setOnClickListener(new Events());
    }

    private class Events implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_share:
                    onClickFabShare();
                    break;
                case R.id.image_email:
                    break;
                case R.id.image_web:
                    break;
                default:
                    break;
            }
        }

        private void onClickFabShare() {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.pixelcrater.Diaro"))
                    .setContentTitle(getString(R.string.share_love_coupon))
                    .setContentDescription(getString(R.string.description_love_coupon))
                    .build();

            ShareDialog shareDialog = new ShareDialog(ShareFragment.this);
            shareDialog.show(content);
        }
    }


}
