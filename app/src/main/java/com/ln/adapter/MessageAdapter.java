package com.ln.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.api.SaveData;
import com.ln.model.Message;
import com.ln.mycoupon.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by luongnguyen on 4/6/16.
 */

public class MessageAdapter extends BaseAdapter {

    public List<Message> listMessage;
    LayoutInflater infater = null;
    private Context mContext;


    public MessageAdapter(Context context, List<Message> apps) {
        infater = LayoutInflater.from(context);
        mContext = context;
        this.listMessage = apps;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listMessage.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listMessage.get(position);
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
            convertView = infater.inflate(R.layout.item_news,
                    null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.company_name = (TextView) convertView
                    .findViewById(R.id.company_name);
            holder.date = (TextView) convertView
                    .findViewById(R.id.date);
            holder.title = (TextView) convertView
                    .findViewById(R.id.title);
            holder.content = (TextView) convertView
                    .findViewById(R.id.content);
            holder.link = (TextView) convertView
                    .findViewById(R.id.link);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Message item = (Message) getItem(position);

        if (item != null) {

            holder.company_name.setText(SaveData.company.name);
            holder.title.setText(item.getTitle());
            holder.content.setText(item.getContent());
            holder.link.setText(item.getLink());

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String date = formatter.format(item.getCreated_date());
            holder.date.setText(date);


        }



        return convertView;
    }


    class ViewHolder {
        ImageView appIcon;
        TextView company_name;
        TextView date;
        TextView title;
        TextView content;
        TextView link;
    }
}