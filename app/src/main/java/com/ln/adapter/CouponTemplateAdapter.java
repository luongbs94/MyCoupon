package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.api.SaveData;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.model.Models;
import com.ln.mycoupon.MainApplication;
import com.ln.mycoupon.QRCodeActivity;
import com.ln.mycoupon.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nhahv on 5/21/2016.
 */

public class CouponTemplateAdapter extends RecyclerView.Adapter<CouponTemplateAdapter.ViewHolder> {

    private ArrayList<CouponTemplate> mListCoupon;
    private Context mContext;
    private LayoutInflater mInflater;

    public CouponTemplateAdapter(Context context, ArrayList<CouponTemplate> coupons) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mListCoupon = coupons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_coupon_template, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final CouponTemplate item = mListCoupon.get(position);
        if (item != null) {
            Company company = SaveData.company;
            if (company != null) {
                holder.mTxtNameCoupon.setText(company.getName());
                Glide.with(mContext).load(MainApplication.convertToBytes(company.getLogo()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_profile)
                        .into(holder.mImgLogo);
            }
            holder.mTxtPriceCoupon.setText(item.getValue());

            Date date = convertStringToDate(item.getCreated_date());
            String dayLeft = dayLeft(date, item.getDuration()) + "";
            holder.mTxtTimeCoupon.setText(dayLeft + " ngày");
            holder.mTxtDescription.setText(item.getContent());


            holder.mQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, QRCodeActivity.class);
                    intent.putExtra(Models.VALUE, item.getValue());
                    intent.putExtra(Models.COUPON_TEMpLATE_ID, item.getCoupon_template_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mListCoupon.size();
    }

    public long dayLeft(Date created_date, int duration) {
        Calendar last_cal = Calendar.getInstance();
        last_cal.setTime(created_date);
        last_cal.add(Calendar.DAY_OF_YEAR, duration);
        Date last_date = last_cal.getTime();

        long diff = last_date.getTime() - new Date().getTime();
        long dayLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (dayLeft < 0) {
            dayLeft = 0;
        }

        return dayLeft;
    }

    private Date convertStringToDate(String date) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        try {
            startDate = df.parse(date);
            return startDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgLogo;
        private TextView mTxtNameCoupon, mTxtPriceCoupon, mTxtDescription, mTxtTimeCoupon;
        private Button mQRCode;

        public ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.app_icon);
            mTxtNameCoupon = (TextView) itemView.findViewById(R.id.txt_company_name);
            mTxtPriceCoupon = (TextView) itemView.findViewById(R.id.txt_price_coupon);
            mTxtTimeCoupon = (TextView) itemView.findViewById(R.id.txt_time);
            mTxtDescription = (TextView) itemView.findViewById(R.id.txt_description);
            mQRCode = (Button) itemView.findViewById(R.id.btn_qr_code);
        }
    }

}
