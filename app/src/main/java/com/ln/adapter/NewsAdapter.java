package com.ln.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.Message;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nhahv on 5/21/2016.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static Firebase sRoot = new Firebase("https://nhahv-fire-chat.firebaseio.com/users");
    private Context mContext;
    private List<Message> mListNews;
    private LayoutInflater mInflater;
    private ArrayList<String> mListImages;


    public NewsAdapter(Context context, List<Message> listNews) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mListNews = listNews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message news = mListNews.get(position);
        Company company = SaveData.company;
        if (company != null) {
            holder.mTxtCompanyName.setText(company.getName());
            Glide.with(mContext).load(MainApplication.convertToBytes(company.getLogo()))
                    .asBitmap()
                    .placeholder(R.drawable.ic_profile)
                    .into(holder.mImgLogo);
        }

        holder.mTxtTile.setText(news.getTitle());
        holder.mTxtContent.setText(news.getContent());
        holder.mTxtLink.setText(news.getLink());

//        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
//
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String date = formatter.format(news.getCreated_date());
//        holder.mTxtTime.setText(date);

        LoadImages loadImages = new LoadImages(holder, "coupon");
        new AsyncTaskLoadImages().execute(loadImages);

    }

    @Override
    public int getItemCount() {
        return mListNews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImgLogo, mImgLike, mImgShare, mImgDelete;
        private TextView mTxtCompanyName, mTxtTime, mTxtTile, mTxtContent, mTxtLink;
        private RecyclerView mRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.img_logo_news);
            mImgLike = (ImageView) itemView.findViewById(R.id.img_like_newx);
            mImgShare = (ImageView) itemView.findViewById(R.id.img_share_newx);
            mImgDelete = (ImageView) itemView.findViewById(R.id.img_delete_news);

            mTxtCompanyName = (TextView) itemView.findViewById(R.id.txt_company_name_news);
            mTxtTime = (TextView) itemView.findViewById(R.id.txt_date_news);
            mTxtTile = (TextView) itemView.findViewById(R.id.txt_title_news);
            mTxtContent = (TextView) itemView.findViewById(R.id.txt_content_news);
            mTxtLink = (TextView) itemView.findViewById(R.id.txt_link_news);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        }
    }


    private class AsyncTaskLoadImages extends AsyncTask<LoadImages, String, Boolean> {

        @Override
        protected Boolean doInBackground(LoadImages... params) {
            final LoadImages urlImages = params[0];
            mListImages = new ArrayList<>();
            sRoot.child(urlImages.getUrl()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String string = snapshot.getValue().toString();
                        if (!isExists(string, mListImages)) {
                            mListImages.add(string);
                        }

                        GridAdapter mGridAdapter = new GridAdapter(mContext, mListImages);
                        urlImages.getViewHolder().mRecyclerView.setAdapter(mGridAdapter);
                        Log.d("NewsAdapter", string);
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

    public class LoadImages {
        public ViewHolder viewHolder;
        public String url;

        public LoadImages() {
        }

        public LoadImages(ViewHolder viewHolder, String url) {
            this.viewHolder = viewHolder;
            this.url = url;
        }

        public ViewHolder getViewHolder() {
            return viewHolder;
        }

        public void setViewHolder(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    private boolean isExists(String string, ArrayList<String> mListImages) {
        for (String strImages : mListImages) {
            if (strImages.equals(string)) {
                return true;
            }
        }

        return false;
    }
}
