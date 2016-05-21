package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ln.api.SaveData;
import com.ln.model.Message;
import com.ln.mycoupon.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by luongnguyen on 4/6/16.
 */

public class MessageAdapter extends BaseAdapter {

    private static final Firebase ROOT =
            new Firebase("https://nhahv-firebase.firebaseio.com/");
    private List<Message> mListMessage;
    private LayoutInflater mInflater = null;
    private GridAdapter mGridAdapter;
    private ArrayList<String> mListImages = new ArrayList<>();

    private String TAG = "MessageAdapter";
    private Context mContext;

    public MessageAdapter(Context context, List<Message> apps) {
        mInflater = LayoutInflater.from(context);
        mListMessage = apps;
        mContext = context;
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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_news, null);
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
            holder.mRecImages = (RecyclerView) convertView.findViewById(R.id.rec_images);
            holder.mRecImages.setLayoutManager(new GridLayoutManager(mContext, 3));
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

            mListImages = new ArrayList<>();
            ROOT.child("user/" + "coupon").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String string = snapshot.getValue().toString();
                        mListImages.add(string);
                        mGridAdapter = new GridAdapter(mContext, mListImages);
                        holder.mRecImages.setAdapter(mGridAdapter);
                        Log.d(TAG, snapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView appIcon;
        private TextView company_name;
        private TextView date;
        private TextView title;
        private TextView content;
        private TextView link;
        private RecyclerView mRecImages;
    }
}