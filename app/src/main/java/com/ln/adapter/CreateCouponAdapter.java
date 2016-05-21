package com.ln.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.model.Coupon;
import com.ln.mycoupon.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;


public class CreateCouponAdapter extends BaseAdapter {

    public List<Coupon> mListCouponTemplate;
    private LayoutInflater mInflater = null;


    public CreateCouponAdapter(Context context, List<Coupon> apps) {
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
            convertView = mInflater.inflate(R.layout.item_fragment_create, null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.time = (TextView) convertView
                    .findViewById(R.id.time);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.value = (TextView) convertView.findViewById(R.id.value);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Coupon item = (Coupon) getItem(position);
        if (item != null) {

            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");

            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatInUTC = formatter.format(item.getCreated_date());

            holder.time.setText(dateFormatInUTC);
            holder.value.setText(item.getValue());

        }

        return convertView;
    }

    private class ViewHolder {
        ImageView appIcon;
        TextView time;
        TextView name;
        TextView value;
    }
}
