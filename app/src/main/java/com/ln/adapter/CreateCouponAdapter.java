package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.model.Coupon;
import com.ln.mycoupon.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nhahv on 5/22/2016.
 * <></>
 */
public class CreateCouponAdapter extends RecyclerView.Adapter<CreateCouponAdapter.ViewHolder> {

    private ArrayList<Coupon> mListCoupons;
    private LayoutInflater mInflater;
    private Context mContext;

    public CreateCouponAdapter(Context context, ArrayList<Coupon> listCoupon) {
        mInflater = LayoutInflater.from(context);
        mListCoupons = listCoupon;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_fragment_create, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Coupon item = mListCoupons.get(position);
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
    }

    @Override
    public int getItemCount() {
        return mListCoupons.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImgLogo;
        private TextView mTxtCompanyName, mTxtPrice, mTxtDate;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgLogo = (ImageView) itemView.findViewById(R.id.img_logo_coupon);
            mTxtCompanyName = (TextView) itemView.findViewById(R.id.txt_company_name_coupon);
            mTxtDate = (TextView) itemView.findViewById(R.id.txt_date_coupon);
            mTxtPrice = (TextView) itemView.findViewById(R.id.txt_price_coupon);
        }
    }
}
