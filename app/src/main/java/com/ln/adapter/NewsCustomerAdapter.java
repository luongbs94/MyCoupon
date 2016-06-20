package com.ln.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.app.MainApplication;
import com.ln.model.Message;
import com.ln.mycoupon.R;
import com.ln.views.MyTextView;

import java.util.List;

/**
 * Created by luongnguyen on 4/6/16.
 * <></>
 */

public class NewsCustomerAdapter extends BaseAdapter {

    private List<Message> mListMessage;
    private LayoutInflater mInflater = null;


    public NewsCustomerAdapter(Context context, List<Message> apps) {
        mInflater = LayoutInflater.from(context);
        this.mListMessage = apps;

    }

    @Override
    public int getCount() {
        return mListMessage.size();
    }

    @Override
    public Object getItem(int position) {
        return mListMessage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_news, null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.company_name = (TextView) convertView
                    .findViewById(R.id.txt_company_name_news);
//            holder.date = (MyTextView) convertView
//                    .findViewById(R.id.date);
            holder.title = (TextView) convertView
                    .findViewById(R.id.txt_title_news);
            holder.content = (MyTextView) convertView
                    .findViewById(R.id.txt_content_news);
            holder.link = (TextView) convertView
                    .findViewById(R.id.txt_link_news);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Message item = (Message) getItem(position);

        if (item != null) {

            holder.company_name.setText(" " + MainApplication.getCompanyName(item.getCompany_id()));
            holder.title.setText(item.getTitle() + "");
            holder.content.setText(item.getContent() + "");
            holder.link.setText(item.getLink() + "");
        }
        return convertView;
    }


    private class ViewHolder {
        ImageView appIcon;
        TextView company_name;
        MyTextView date;
        TextView title;
        MyTextView content;
        TextView link;
    }
}