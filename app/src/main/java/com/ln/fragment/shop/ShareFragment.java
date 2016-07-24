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
                            .setImageUrl(Uri.parse("http://188.166.179.187:3001/upload/ImageSelector_20160616_223027_19062016_010851.png"))
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
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.setData(Uri.parse("mailto:" + getString(R.string.support)));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    getShowMessages(getActivity().getString(R.string.check_network));
                }
                break;
            case R.id.image_web:
                if (ConnectivityReceiver.isConnect()) {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(MainApplication.WEB_SITE_LOVE_COUPON));
                    startActivity(intent1);
                } else {
                    getShowMessages(getActivity().getString(R.string.check_network));
                }
                break;
            default:
                break;
        }
    }

    private void getShowMessages(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
