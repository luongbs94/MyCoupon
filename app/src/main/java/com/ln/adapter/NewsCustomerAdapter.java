package com.ln.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.Message;
import com.ln.mycoupon.R;
import com.ln.views.IconTextView;
import com.ln.views.MyTextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by nhahv on 9/7/16.
 * <></>
 */


public class NewsCustomerAdapter extends RecyclerView.Adapter<NewsCustomerAdapter.ViewHolder> {

    private List<Message> mListNews;
    private Context mContext;
    private ShareDialog mShareDialog;
    private int mType;

    public NewsCustomerAdapter(Context context, List<Message> listNews, Fragment fragment) {
        mContext = context;
        mListNews = listNews;
        mShareDialog = new ShareDialog(fragment);
        mType = MainApplication.NEWS_CUSTOMER;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Message item = mListNews.get(position);

        if (item.getLogo_link() != null) {
            Picasso.with(mContext)
                    .load(item.getLogo_link())
                    .into(holder.mImgLogo);
        }
        if (item.getName() != null) {
            holder.mTxtCompanyName.setText(item.getName());
        }

        if (item.getName() != null) {
            holder.mTxtTile.setText(item.getTitle());
        }
        if (item.getContent() != null) {
            holder.mTxtContent.setText(item.getContent());
        }
        if (item.getLink() != null) {
            holder.mTxtLink.setText(item.getLink());
        }

        if (item.getImages_link() != null) {
            String strImages = item.getImages_link();
            if (strImages != null) {
                List<String> listImages = new ArrayList<>();

                String[] listStrImages = strImages.split(";");
                listImages.addAll(Arrays.asList(listStrImages));
                GridAdapter gridAdapter = new GridAdapter(mContext, listImages);
                holder.mRecyclerView.setAdapter(gridAdapter);
                holder.mRecyclerView.setVisibility(View.VISIBLE);

            } else {
                holder.mRecyclerView.setVisibility(View.GONE);
            }
        }

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (MainApplication.getLanguage()) {
            fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        }

        String date = fmt.format(item.getCreated_date());
        holder.mTxtTime.setText(date);

        holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
        holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start));
        holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.icon_heart));

        if (item.isLike()) {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start_like));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.heart_color));
        }

    }

    @Override
    public int getItemCount() {
        return mListNews.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImgLogo;
        private TextView mTxtTile, mTxtLink;
        private IconTextView mImgLike, mImageBookmarks;
        private RecyclerView mRecyclerView;

        private MyTextView mTxtTime, mTxtContent;
        private TextView mTxtCompanyName;

        ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.img_logo_news);
            mImgLike = (IconTextView) itemView.findViewById(R.id.img_like_newx);
            mImageBookmarks = (IconTextView) itemView.findViewById(R.id.bookmark);

            mTxtCompanyName = (TextView) itemView.findViewById(R.id.txt_company_name_news);
            mTxtTime = (MyTextView) itemView.findViewById(R.id.txt_date_news);
            mTxtTile = (TextView) itemView.findViewById(R.id.txt_title_news);
            mTxtContent = (MyTextView) itemView.findViewById(R.id.txt_content_news);
            mTxtLink = (TextView) itemView.findViewById(R.id.txt_link_news);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);

            LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(manager);

            (itemView.findViewById(R.id.linear_like)).setOnClickListener(this);
            (itemView.findViewById(R.id.linear_share)).setOnClickListener(this);
            (itemView.findViewById(R.id.linear_delete)).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.linear_like:
                    onClickLikeNews(this.getAdapterPosition(), this);
                    break;
                case R.id.linear_share:
                    onClickShareNews(this.getAdapterPosition());
                    break;
                case R.id.linear_delete:
                    onClickDeleteNews(this.getAdapterPosition(), this);
                    break;
                default:
                    break;
            }
        }
    }

    private void onClickLikeNews(int position, ViewHolder holder) {

        Message item = mListNews.get(position);
        String strAccount = MainApplication.getPreferences()
                .getString(MainApplication.ACCOUNT_CUSTOMER, "");
        final String idUser = new Gson()
                .fromJson(strAccount, AccountOflUser.class)
                .getId();

        if (item.isLike()) {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
            item.setLike(false);
            MainApplication.mRealmController.deleteLikeNewsById(item.getMessage_id());
        } else {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start_like));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            item.setLike(true);
            MainApplication.mRealmController.addLikeNewsCustomer(item.getMessage_id(), mType, idUser);
        }
    }

    private void onClickShareNews(int position) {

        Message item = mListNews.get(position);
        Uri uri = Uri.parse("http://188.166.179.187:3001/upload/ImageSelector_20160616_223027_19062016_010851.png");
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://google.com"))
                .setContentTitle(item.getTitle())
                .setContentDescription(item.getContent())
                .setImageUrl(uri)
                .build();

        mShareDialog.show(content);
    }

    private void onClickDeleteNews(int position, final ViewHolder holder) {

        final Message item = mListNews.get(position);
        String strAccount = MainApplication.getPreferences()
                .getString(MainApplication.ACCOUNT_CUSTOMER, "");
        final String idUser = new Gson()
                .fromJson(strAccount, AccountOflUser.class)
                .getId();

        new MaterialDialog.Builder(mContext).content(R.string.delete_news)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .positiveColor(mContext.getResources().getColor(R.color.title_bg))
                .negativeColor(mContext.getResources().getColor(R.color.title_bg))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MainApplication.mRealmController.addDeleteNewsByIdNews(item.getMessage_id(), idUser);
                        mListNews.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}