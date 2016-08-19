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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.images.models.LocalMedia;
import com.ln.model.AccountOfUser;
import com.ln.model.NewsOfCustomer;
import com.ln.mycoupon.R;
import com.ln.views.IconTextView;
import com.ln.views.MyTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by nhahv on 9/7/16.
 * <></>
 */


public class NewsCustomerAdapter extends RecyclerView.Adapter<NewsCustomerAdapter.ViewHolder> {

    private List<NewsOfCustomer> mListNews;
    private Context mContext;
    private ShareDialog mShareDialog;
    private int mType;

    public NewsCustomerAdapter(Context context, List<NewsOfCustomer> listNews, Fragment fragment) {
        mContext = context;
        mListNews = listNews;
        mShareDialog = new ShareDialog(fragment);
        mType = MainApplication.NEWS_CUSTOMER;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_news_customer, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final NewsOfCustomer item = mListNews.get(position);

        if (item.getLogo_link() != null) {
//            Picasso.with(mContext)
//                    .load(item.getLogo_link())
//                    .into(holder.mImgLogo);
            Glide.with(mContext)
                    .load(item.getLogo_link())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
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

        holder.mTxtLink.setVisibility(View.GONE);
        if (item.getLink() != null) {
            holder.mTxtLink.setVisibility(View.VISIBLE);
            holder.mTxtLink.setText(item.getLink());
        }

        if (item.getImages_link() != null) {
            String strImages = item.getImages_link();
            if (strImages != null) {
                List<LocalMedia> listImages = new ArrayList<>();

                String[] listStrImages = strImages.split(";");
                for (String path : listStrImages) {
                    listImages.add(new LocalMedia(path));
                }
                GridAdapter gridAdapter = new GridAdapter(mContext, listImages);
                holder.mRecyclerView.setAdapter(gridAdapter);
                holder.mRecyclerView.setVisibility(View.VISIBLE);

            } else {
                holder.mRecyclerView.setVisibility(View.GONE);
            }
        }

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (MainApplication.isEnglish()) {
            fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        }

        holder.mTxtTime.setText(fmt.format(item.getCreated_date()));

        if (item.getLast_date() != 0) {
            holder.linearLastDate.setVisibility(View.VISIBLE);
            holder.textLastDate.setText(fmt.format(item.getLast_date()));
            holder.textTimeShelf.setText(String.valueOf(MainApplication.dayLeft(item.getLast_date())));
        }

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
        private TextView mTxtCompanyName, textLastDate, textTimeShelf;
        private LinearLayout linearLastDate;

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
            mRecyclerView.setVisibility(View.GONE);

            textLastDate = (TextView) itemView.findViewById(R.id.text_last_date);
            textTimeShelf = (TextView) itemView.findViewById(R.id.text_time_shelf);
            linearLastDate = (LinearLayout) itemView.findViewById(R.id.linear_last_date);
            linearLastDate.setVisibility(View.GONE);

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

        NewsOfCustomer item = mListNews.get(position);
        String strAccount = MainApplication.getPreferences()
                .getString(MainApplication.ACCOUNT_CUSTOMER, "");
        final String idUser = new Gson()
                .fromJson(strAccount, AccountOfUser.class)
                .getId();

        int type = MainApplication.NEW_LIKE;
        int typeShop = MainApplication.CUSTOMER;


        if (item.isLike()) {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
            item.setLike(false);
            DatabaseManager.deleteOptionNews(item.getMessage_id(), type, typeShop);
        } else {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start_like));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            item.setLike(true);
            DatabaseManager.addOptionNews(item.getMessage_id(), idUser, type, typeShop);
        }
    }

    private void onClickShareNews(int position) {

        NewsOfCustomer item = mListNews.get(position);
        Uri uriLink = null;
        if (item.getLink() != null) {
            uriLink = Uri.parse(item.getLink());
        } else {
            uriLink = Uri.parse(MainApplication.WEB_SITE_LOVE_COUPON);
        }
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(uriLink)
                .setContentTitle(item.getTitle())
                .setContentDescription(item.getContent())
                .setImageUrl(Uri.parse(item.getLogo_link()))
                .build();

        mShareDialog.show(content);
    }

    private void onClickDeleteNews(final int position, final ViewHolder holder) {

        final NewsOfCustomer item = mListNews.get(position);

        String strAccount = MainApplication.getPreferences()
                .getString(MainApplication.ACCOUNT_CUSTOMER, "");
        final String idUser = new Gson()
                .fromJson(strAccount, AccountOfUser.class)
                .getId();

        new MaterialDialog.Builder(mContext).content(R.string.delete_news)
                .positiveText(R.string.agree)
                .positiveColor(mContext.getResources().getColor(R.color.title_bg))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.addOptionNews(item.getMessage_id(), idUser, MainApplication.NEW_DELETE, MainApplication.CUSTOMER);
                        mListNews.remove(holder.getAdapterPosition());
                        notifyItemRemoved(position);
                    }
                })
                .show();
    }
}