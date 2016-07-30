package com.ln.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.images.models.LocalMedia;
import com.ln.model.Company;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCompanyLike;
import com.ln.mycoupon.R;
import com.ln.views.IconTextView;
import com.ln.views.MyTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nhahv on 5/21/2016.
 * adapter connect news fragments
 */
public class NewsShopAdapter extends RecyclerView.Adapter<NewsShopAdapter.ViewHolder> {

    private static final String TAG = "NewsShopAdapter";
    private Context mContext;
    private List<NewsOfCompanyLike> mListNews;
    private ShareDialog mShareDialog;

    public NewsShopAdapter(Context context, List<NewsOfCompanyLike> listNews, Fragment fragment) {
        mContext = context;
        mListNews = listNews;
        mShareDialog = new ShareDialog(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        NewsOfCompanyLike item = mListNews.get(position);

        String strCompany = MainApplication
                .getPreferences()
                .getString(MainApplication.COMPANY_SHOP, "");

        final Company company = new Gson().fromJson(strCompany, Company.class);

        if (company != null) {
            if (company.getName() != null) {
                holder.mTxtCompanyName.setText(company.getName());
            }
            if (company.getLogo() != null) {
                Glide.with(mContext)
                        .load(MainApplication.convertToBytes(company.getLogo()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_logo_blank)
                        .into(holder.mImgLogo);
            }
        }

        if (item.getTitle() != null) {
            holder.mTxtTile.setText(item.getTitle());
        }
        if (item.getContent() != null) {
            holder.mTxtContent.setText(item.getContent());
        }

        holder.mTxtLink.setVisibility(View.GONE);
        if (!item.getLink().isEmpty()) {
            holder.mTxtLink.setVisibility(View.VISIBLE);
            if (item.getLink().contains("http")) {
                holder.mTxtLink.setText(item.getLink());
            } else {
                String link = "http://" + item.getLink();
                holder.mTxtLink.setText(link);
            }
        }

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (MainApplication.getLanguage()) {
            fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        }

        holder.mTxtTime.setText(fmt.format(item.getCreated_date()));

        if (item.getLast_date() != 0) {
            holder.linearLastDate.setVisibility(View.VISIBLE);
            holder.textLastDate.setText(fmt.format(item.getLast_date()));
            holder.textTimeShelf.setText(String.valueOf(MainApplication.dayLeft(item.getLast_date())));
        }
        holder.mRecyclerView.setVisibility(View.GONE);
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
        private IconTextView mImgLike, mImageBookmarks;

        private TextView mTxtTile, mTxtLink;
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

            textLastDate = (TextView) itemView.findViewById(R.id.text_last_date);
            textTimeShelf = (TextView) itemView.findViewById(R.id.text_time_shelf);
            linearLastDate = (LinearLayout) itemView.findViewById(R.id.linear_last_date);
            linearLastDate.setVisibility(View.GONE);

            LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setVisibility(View.GONE);
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
                    onClickShare(this.getAdapterPosition(), this);
                    break;
                case R.id.linear_delete:
                    onClickDeleteNews(this.getAdapterPosition());
                    break;
                default:
                    break;
            }
        }
    }

    private void onClickDeleteNews(final int position) {

        final String idNewsOfCompany = mListNews.get(position).getMessage_id();
        new MaterialDialog
                .Builder(mContext)
                .content(R.string.delete_news)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .positiveColor(mContext.getResources().getColor(R.color.title_bg))
                .negativeColor(mContext.getResources().getColor(R.color.title_bg))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteNewsOfCompany(idNewsOfCompany, position);

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

    private void onClickLikeNews(int position, ViewHolder holder) {

        NewsOfCompanyLike item = mListNews.get(position);
        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        final Company company = new Gson().fromJson(strCompany, Company.class);


        if (item.isLike()) {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
            item.setLike(false);
            MainApplication.mRealmController.deleteShopLikeNewsByIdNews(item.getMessage_id());

        } else {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start_like));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            item.setLike(true);
            MainApplication.mRealmController.addShopLikeNewsByIdNews(item.getMessage_id(), company.getCompany_id());
        }
    }

    private void onClickShare(int position, ViewHolder holder) {

        NewsOfCompanyLike item = mListNews.get(position);

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        final Company company = new Gson().fromJson(strCompany, Company.class);

        Uri uriLink = null;
        if (!item.getLink().isEmpty()) {

            if (item.getLink().contains("http")) {
                uriLink = Uri.parse(item.getLink());
            } else {
                uriLink = Uri.parse("http://" + item.getLink());
            }
        } else {
            uriLink = Uri.parse(MainApplication.WEB_SITE_LOVE_COUPON);
        }
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(uriLink)
                .setContentTitle(item.getTitle())
                .setContentDescription(item.getContent())
                .setImageUrl(Uri.parse(company.getLogo_link()))
                .build();

        mShareDialog.show(content);
    }

    private void deleteNewsOfCompany(final String idNews, final int positionNews) {
        NewsOfCompany news = new NewsOfCompany(idNews);
        Call<Integer> call = MainApplication.getAPI().deleteMessage(news);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == MainApplication.SUCCESS) {
                    getShowMessages(mContext.getString(R.string.delete_news_success));
                    MainApplication.mRealmController.deleteNewsOfCompany(idNews);
                    mListNews.remove(positionNews);
                    notifyDataSetChanged();
                    Log.d("NewsShopAdapter", "Delete : News Success");
                } else {
                    getShowMessages(mContext.getString(R.string.delete_news_error));
                }

                Log.d(TAG, "deleteNewsOfCompany " + response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getShowMessages(mContext.getString(R.string.delete_news_error));
            }
        });
    }

    private void getShowMessages(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
