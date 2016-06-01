package com.ln.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.app.MainApplication;
import com.ln.model.Coupon;
import com.ln.mycoupon.R;

import java.util.List;


public class Coupon2Adapter extends BaseAdapter {

    public List<Coupon> mListCouponTemplate;
    private LayoutInflater mInflater = null;


    public Coupon2Adapter(Context context, List<Coupon> apps) {
        mInflater = LayoutInflater.from(context);
        this.mListCouponTemplate = apps;
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
            convertView = mInflater.inflate(R.layout.item_coupon, null);
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
            String string = item.getValue() + " Coupon";
            holder.appValue.setText(string);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appValue;
    }
}
