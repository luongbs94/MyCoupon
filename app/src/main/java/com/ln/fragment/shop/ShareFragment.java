package com.ln.fragment.shop;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ln.app.MainApplication;
import com.ln.mycoupon.R;
import com.ln.views.IconTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton mFabShare;
    private IconTextView mImageEmail, mImageWeb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share, container, false);
        initViews(view);
        addEvents();

        setHasOptionsMenu(false);
        return view;
    }

    private void initViews(View view) {

        mFabShare = (FloatingActionButton) view.findViewById(R.id.fab_share);
        mImageEmail = (IconTextView) view.findViewById(R.id.image_email);
        mImageWeb = (IconTextView) view.findViewById(R.id.image_web);
    }

    private void addEvents() {

        mFabShare.setOnClickListener(this);
        mImageEmail.setOnClickListener(this);
        mImageWeb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_share:
                onClickFabShare();
                break;
            case R.id.image_email:
                onClickSendMessageToEmail();
                break;
            case R.id.image_web:
                onClickVisitToWebsite();
                break;
            default:
                break;
        }
    }

    private void onClickFabShare() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(MainApplication.WEB_SITE_LOVE_COUPON))
                .setImageUrl(Uri.parse("http://188.166.179.187:3001/upload/ImageSelector_20160616_223027_19062016_010851.png"))
                .setContentTitle(getString(R.string.share_love_coupon))
                .setContentDescription(getString(R.string.description_love_coupon))
                .build();

        ShareDialog shareDialog = new ShareDialog(ShareFragment.this);
        shareDialog.show(content);
    }

    private void onClickSendMessageToEmail() {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setData(Uri.parse("mailto:" + getString(R.string.support)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void onClickVisitToWebsite() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainApplication.WEB_SITE_LOVE_COUPON));
        startActivity(intent);
    }
}
