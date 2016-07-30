package com.ln.fragment.shop;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.mycoupon.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        (view.findViewById(R.id.fab_share)).setOnClickListener(this);
        (view.findViewById(R.id.image_email)).setOnClickListener(this);
        (view.findViewById(R.id.image_web)).setOnClickListener(this);
        setHasOptionsMenu(false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_share:
                if (ConnectivityReceiver.isConnect()) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(MainApplication.WEB_SITE_LOVE_COUPON))
                            .setImageUrl(Uri.parse(MainApplication.LINK_SHARE_IMAGE))
                            .setContentTitle(getString(R.string.share_love_coupon))
                            .setContentDescription(getString(R.string.description_love_coupon))
                            .build();

                    ShareDialog shareDialog = new ShareDialog(ShareFragment.this);
                    shareDialog.show(content);
                } else {
                    getShowMessages(getActivity().getString(R.string.check_network));
                }

                break;
            case R.id.image_email:
                if (ConnectivityReceiver.isConnect()) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.setData(Uri.parse("mailto:" + MainApplication.EMAIL_LOVE_COUPON)); // or just "mailto:" for blank
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                    startActivity(Intent.createChooser(intent, "Send email..."));
                } else {
                    getShowMessages(getActivity().getString(R.string.check_network));
                }

                break;
            case R.id.image_web:
                if (!ConnectivityReceiver.isConnect()) {
                    getShowMessages(getActivity().getString(R.string.check_network));
                    return;
                }
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(MainApplication.WEB_SITE_LOVE_COUPON));
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    private void getShowMessages(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
