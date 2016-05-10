package com.ln.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.model.Coupon;
import com.ln.mycoupon.MainApplication;
import com.ln.mycoupon.R;

import java.util.List;


public class Coupon2Adapter extends BaseAdapter {

    public List<Coupon> mlistCouponTemplate;
    LayoutInflater infater = null;
    private Context mContext;


    public Coupon2Adapter(Context context, List<Coupon> apps) {
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
            convertView = infater.inflate(R.layout.item_coupon,
                    null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.company_name);
            holder.appValue = (TextView) convertView
                    .findViewById(R.id.company_coupon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Coupon item = (Coupon) getItem(position);
        if (item != null) {

            holder.appName.setText(MainApplication.getCompanyName(item.getCompany_id()));
            holder.appValue.setText(item.getValue() + " Coupon");
        }

        return convertView;
    }


    class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appValue;
    }

}
