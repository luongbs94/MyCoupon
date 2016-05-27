package com.ln.adapter;

import android.content.Context;
import android.os.AsyncTask;
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
import com.ln.loadimage.LoadImages;
import com.ln.loadimage.ViewHolder;
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

    private ViewHolder holder;

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

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_news, null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.company_name = (TextView) convertView
                    .findViewById(R.id.company_name);
//            holder.date = (TextView) convertView
//                    .findViewById(R.id.date);
            holder.title = (TextView) convertView
                    .findViewById(R.id.title);
            holder.content = (TextView) convertView
                    .findViewById(R.id.content);
            holder.link = (TextView) convertView
                    .findViewById(R.id.link);
//            holder.mRecImages = (RecyclerView) convertView.findViewById(R.id.rec_images);
//            holder.mRecImages.setLayoutManager(new GridLayoutManager(mContext, 3));
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

            String url = "user/coupon";
            LoadImages loadImages = new LoadImages(holder, url);
            new AsyncTaskLoadImages().execute(loadImages);

        }
        return convertView;
    }


    private class AsyncTaskLoadImages extends AsyncTask<LoadImages, String, Boolean> {

        @Override
        protected Boolean doInBackground(LoadImages... params) {
            final LoadImages urlImages = params[0];
            mListImages = new ArrayList<>();
            ROOT.child(urlImages.getUrl()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String string = snapshot.getValue().toString();
                        mListImages.add(string);
                        mGridAdapter = new GridAdapter(mContext, mListImages);
                        urlImages.getViewHolder().mRecImages.setAdapter(mGridAdapter);
                        Log.d(TAG, snapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}