package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.api.SaveData;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.mycoupon.MainApplication;
import com.ln.mycoupon.R;
import com.ln.mycoupon.TestQRCode;
import com.ln.views.RippleView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CouponAdapter extends BaseAdapter {

    public List<CouponTemplate> mListCouponTemplate;
    private LayoutInflater mInflater = null;
    private Context mContext;

    public CouponAdapter(Context context, List<CouponTemplate> apps) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mListCouponTemplate = apps;
    }

    @Override
    public int getCount() {
        return mListCouponTemplate.size();
    }

    @Override
    public Object getItem(int position) {
        return mListCouponTemplate.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_coupon_template, null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.mTxtNameCoupon = (TextView) convertView
                    .findViewById(R.id.txt_company_name);
            holder.mTxtTimeCoupon = (TextView) convertView
                    .findViewById(R.id.txt_time);

            holder.mQRCode = (RippleView) convertView
                    .findViewById(R.id.riple_qrcode);
            holder.mTxtPriceCoupon = (TextView)
                    convertView.findViewById(R.id.txt_price_coupon);
            holder.mTxtDescription = (TextView)
                    convertView.findViewById(R.id.txt_description);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CouponTemplate item = (CouponTemplate) getItem(position);
        if (item != null) {
            Company company = SaveData.company;
            if (company != null) {
                holder.mTxtNameCoupon.setText(company.getName());
                Glide.with(mContext).load(MainApplication.convertToBytes(company.getLogo()))
                        .asBitmap()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(holder.appIcon);
            }
            holder.mTxtPriceCoupon.setText(item.getValue());
            String dayLeft = dayLeft(item.getCreated_date(), item.getDuration()) + "";
            holder.mTxtTimeCoupon.setText(dayLeft + " ngày");
            holder.mTxtDescription.setText(item.getContent());

        }

        final String coupon = item.getValue() + "";
        final String coupon_template_id = item.getCoupon_template_id();

        holder.mQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, TestQRCode.class);
                intent.putExtra("value", coupon);
                intent.putExtra("coupon_template_id", coupon_template_id);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private ImageView appIcon;
        private TextView mTxtNameCoupon, mTxtPriceCoupon, mTxtDescription;
        private TextView mTxtTimeCoupon;
        private RippleView mQRCode;
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
}
