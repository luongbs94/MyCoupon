package com.ln.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.model.Company1;
import com.ln.mycoupon.R;

import java.util.List;


public class Coupon1Adapter extends BaseAdapter {

    public List<Company1> listcompany;
    LayoutInflater infater = null;
    private Context mContext;


    public Coupon1Adapter(Context context, List<Company1> apps) {
        infater = LayoutInflater.from(context);
        mContext = context;
        this.listcompany = apps;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listcompany.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listcompany.get(position);
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
            convertView = infater.inflate(R.layout.item_company,
                    null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.companyName = (TextView) convertView
                    .findViewById(R.id.company_name);
            holder.companyAddress = (TextView) convertView
                    .findViewById(R.id.company_address);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Company1 item = (Company1) getItem(position);

        if (item != null) {

            holder.companyName.setText(item.getName());
            holder.companyAddress.setText(item.getAddress());

        }

        return convertView;
    }


    class ViewHolder {
        ImageView appIcon;
        TextView companyName;
        TextView companyAddress;
    }

}
