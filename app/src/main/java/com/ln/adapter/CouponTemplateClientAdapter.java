package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.model.Coupon;
import com.ln.mycoupon.R;

import java.util.List;

/**
 * Created by Nhahv on 5/21/2016.
 */

public class CouponTemplateClientAdapter extends RecyclerView.Adapter<CouponTemplateClientAdapter.ViewHolder> {

    private List<Coupon> mListCoupon;
    private Context mContext;
    private LayoutInflater mInflater;

    public CouponTemplateClientAdapter(Context context, List<Coupon> coupons) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mListCoupon = coupons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Coupon item = mListCoupon.get(position);
        if (item != null) {
            holder.couponName.setText(item.getValue());
            holder.couponDate.setText(item.getDuration() + "");



        }
    }

    @Override
    public int getItemCount() {
        return mListCoupon.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgLogo;
        private TextView couponName, couponDate;
        private Button mQRCode;

        public ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.app_icon);
            couponName = (TextView) itemView.findViewById(R.id.coupon_name);
            couponDate = (TextView) itemView.findViewById(R.id.coupon_date);

        }
    }

}
