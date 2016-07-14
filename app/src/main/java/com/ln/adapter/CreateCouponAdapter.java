package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.Coupon;
import com.ln.mycoupon.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nhahv on 5/22/2016.
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

        Coupon coupon = mListCoupons.get(position);
        String strCompany = MainApplication.getSharedPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);
        Glide.with(mContext).load(coupon.getUser_image_link())
                .placeholder(R.drawable.ic_logo_blank)
                .into(holder.mImgLogo);

        holder.mTxtCompanyName.setText(company.getName());
        holder.mTxtPrice.setText(coupon.getValue());

        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

        Date date = new Date(coupon.getCreated_date());


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
