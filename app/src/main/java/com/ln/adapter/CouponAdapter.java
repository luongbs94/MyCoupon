package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.api.SaveData;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.mycoupon.R;
import com.ln.mycoupon.TestQRCode;
import com.ln.views.RippleView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CouponAdapter extends BaseAdapter {

    public List<CouponTemplate> mlistCouponTemplate;
    LayoutInflater infater = null;
    private Context mContext;


    public CouponAdapter(Context context, List<CouponTemplate> apps) {
        infater = LayoutInflater.from(context);
        mContext = context;
        this.mlistCouponTemplate = apps;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mlistCouponTemplate.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mlistCouponTemplate.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = infater.inflate(R.layout.item_coupon_template,
                    null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.appCoupon = (TextView) convertView
                    .findViewById(R.id.app_coupon);
            holder.appTime = (TextView) convertView
                    .findViewById(R.id.app_time);

            holder.qrcode = (RippleView) convertView
                    .findViewById(R.id.riple_qrcode);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CouponTemplate item = (CouponTemplate) getItem(position);
        Company company = SaveData.company;
        if (item != null) {

            holder.appCoupon.setText(item.getValue() + "Coupon");
            String dayLeft = dayLeft(item.getCreated_date(), item.getDuration()) + "";
            holder.appTime.setText("Còn " + dayLeft +" ngày");

       //     String base= company.getLogo();
       //     byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
        //    holder.appIcon.setImageBitmap(
        //            BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }

        final String coupon = item.getValue() + "";
        final String coupon_template_id = item.getCoupon_template_id();

        holder.qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, TestQRCode.class);
                intent.putExtra("value", coupon);
                intent.putExtra("coupon_template_id", coupon_template_id );
                mContext.startActivity(intent);


            }
        });


        return convertView;
    }


    class ViewHolder {
        ImageView appIcon;
        TextView appCoupon;
        TextView appTime;
        RippleView qrcode;
    }

    public long dayLeft(Date created_date, int duration){
        Calendar last_cal = Calendar.getInstance();
        last_cal.setTime(created_date);
        last_cal.add(Calendar.DAY_OF_YEAR, duration);

        Date last_date = last_cal.getTime();

        long diff = last_date.getTime() - new Date().getTime();
        long dayLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if(dayLeft < 0){
            dayLeft = 0;
        }


        return dayLeft;
    }

}
