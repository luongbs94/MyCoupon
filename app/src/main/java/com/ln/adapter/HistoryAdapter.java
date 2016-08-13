package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ln.model.Coupon;
import com.ln.mycoupon.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nhahv on 5/22/2016.
 * <></>
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<Coupon> mListCoupons;
    private Context mContext;

    public HistoryAdapter(Context context, List<Coupon> listCoupon) {
        mListCoupons = listCoupon;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_fragment_create, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Coupon item = mListCoupons.get(position);
        if (item.getUser_image_link() != null) {
            Picasso.with(mContext)
                    .load(item.getUser_image_link())
                    .placeholder(R.drawable.ic_logo_blank)
                    .into(holder.mImgLogo);
        }

        if (item.getUser_name() != null) {
            holder.mTxtCompanyName.setText(item.getUser_name());
        } else {
            holder.mTxtCompanyName.setText("");
        }

        if (item.getValue() != null) {
            holder.mTxtPrice.setText(item.getValue());
        }

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date(item.getCreated_date());
        holder.mTxtDate.setText(fmt.format(date));

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String link = "";
                    if (item.getUser_social().equals("facebook")) {
                        link = "https://facebook.com/" + item.getUser_id();
                    } else if (item.getUser_social().equals("google")) {
                        link = "'https://plus.google.com/" + item.getUser_id();
                    }

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    mContext.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListCoupons.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImgLogo;
        private TextView mTxtCompanyName, mTxtPrice, mTxtDate;
        private LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgLogo = (ImageView) itemView.findViewById(R.id.img_logo_coupon);
            mTxtCompanyName = (TextView) itemView.findViewById(R.id.txt_company_name_coupon);
            mTxtDate = (TextView) itemView.findViewById(R.id.txt_date_coupon);
            mTxtPrice = (TextView) itemView.findViewById(R.id.txt_price_coupon);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.layout_user);
        }
    }
}
