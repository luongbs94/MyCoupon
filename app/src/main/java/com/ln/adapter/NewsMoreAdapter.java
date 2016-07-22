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
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.Message;
import com.ln.mycoupon.R;
import com.ln.views.IconTextView;
import com.ln.views.MyTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by nhahv on 9/7/16.
 * <></>
 */

public class NewsMoreAdapter extends RecyclerView.Adapter<NewsMoreAdapter.ViewHolder> {

    private List<Message> mListNews;
    private Context mContext;
    private ShareDialog mShareDialog;
    private int mType;


    public NewsMoreAdapter(Context context, List<Message> listNews, Fragment fragment) {
        mContext = context;
        mListNews = listNews;
        mShareDialog = new ShareDialog(fragment);
        mType = MainApplication.NEWS_MORE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final int positionNews = position;
        final Message item = mListNews.get(positionNews);

        String strAccount = MainApplication.getSharedPreferences()
                .getString(MainApplication.ACCOUNT_CUSTOMER, "");
        final String idUser = new Gson().fromJson(strAccount, AccountOflUser.class).getId();

        if (item.getLogo_link() != null) {
            Glide.with(mContext).load(item.getLogo_link())
                    .placeholder(R.drawable.ic_love_coupon)
                    .into(holder.mImgLogo);
        }

        if (item.getContent() != null) {
            holder.mTxtContent.setText(item.getContent());
        } else {
            holder.mTxtContent.setVisibility(View.GONE);
        }

        if (item.getTitle() != null) {
            holder.mTxtTile.setText(item.getTitle());
        }

        if (item.getLink() != null) {
            holder.mTxtLink.setText(item.getLink());
        } else {
            holder.mTxtLink.setVisibility(View.GONE);
        }
        if (item.getName() != null) {
            holder.mTxtCompanyName.setText(item.getName());
        } else {
            holder.mTxtCompanyName.setText("");
        }

        SimpleDateFormat fmt;
        if (MainApplication.getLanguage()) {
            fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        } else {
            fmt = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());

        }

        String date = fmt.format(item.getCreated_date());
        holder.mTxtTime.setText(date);

        if (item.getImages_link() != null) {
            String strImages = item.getImages_link();

            List<String> listImages = new ArrayList<>();

            String[] listStrImages = strImages.split(";");
            listImages.addAll(Arrays.asList(listStrImages));
            GridAdapter gridAdapter = new GridAdapter(mContext, listImages);
            holder.mRecyclerView.setAdapter(gridAdapter);
            holder.mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.mRecyclerView.setVisibility(View.GONE);
        }

        holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
        holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start));
        holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.icon_heart));

        if (item.isLike()) {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start_like));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.heart_color));
        }

        holder.mLinearLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isLike()) {
                    holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
                    holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start));
                    holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
//
                    item.setLike(false);
                    MainApplication.mRealmController.deleteLikeNewsById(item.getMessage_id());

//                    MainApplication.mRealmController.deleteNewsCustomerLike(news.getMessage_id());


                } else {
                    holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.heart_color));
                    holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start_like));
                    holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.heart_color));

                    item.setLike(true);
                    MainApplication.mRealmController.addLikeNewsCustomer(item.getMessage_id(), mType, idUser);
                }
            }
        });
//
        holder.mLinearShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("http://188.166.179.187:3001/upload/ImageSelector_20160616_223027_19062016_010851.png");
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(MainApplication.WEB_SITE_LOVE_COUPON))
                        .setContentTitle(item.getTitle())
                        .setContentDescription(item.getContent())
                        .setImageUrl(uri)
                        .build();

                mShareDialog.show(content);
            }
        });

        holder.mLinearDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext);
                dialog.content(R.string.delete_news)
                        .positiveText(R.string.agree)
                        .negativeText(R.string.disagree)
                        .positiveColor(mContext.getResources().getColor(R.color.title_bg))
                        .negativeColor(mContext.getResources().getColor(R.color.title_bg))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                MainApplication.mRealmController.addDeleteNewsByIdNews(item.getMessage_id(), idUser);
                                mListNews.remove(positionNews);
                                notifyDataSetChanged();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mListNews.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImgLogo;
        private TextView mTxtTile, mTxtLink, mTxtCompanyName, textTime;
        private IconTextView mImgLike, mImageBookmarks;
        private RecyclerView mRecyclerView;

        private MyTextView mTxtTime, mTxtContent;
        private LinearLayout mLinearLike, mLinearShare, mLinearDelete;

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

            mLinearLike = (LinearLayout) itemView.findViewById(R.id.linear_like);
            mLinearShare = (LinearLayout) itemView.findViewById(R.id.linear_share);
            mLinearDelete = (LinearLayout) itemView.findViewById(R.id.linear_delete);
            textTime = (TextView) itemView.findViewById(R.id.txt_title_news);
        }
    }
}