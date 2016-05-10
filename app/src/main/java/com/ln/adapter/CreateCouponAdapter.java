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

    public List<Coupon> mlistCouponTemplate;
    LayoutInflater infater = null;
    private Context mContext;


    public CreateCouponAdapter(Context context, List<Coupon> apps) {
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
            convertView = infater.inflate(R.layout.item_fragment_create,
                    null);
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
            String dateFormateInUTC = formatter.format(item.getCreated_date());

            holder.time.setText(dateFormateInUTC);
            holder.value.setText(item.getValue());

        }

        return convertView;
    }

    class ViewHolder {
        ImageView appIcon;
        TextView time;
        TextView name;
        TextView value;
    }

}
