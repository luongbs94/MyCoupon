package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.images.models.LocalMedia;
import com.ln.model.Company;
import com.ln.model.NewsOfCompany;
import com.ln.until.Until;
import com.ln.mycoupon.AddMessageActivity;
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

public class NewsShopAdapter extends RecyclerView.Adapter<NewsShopAdapter.ViewHolder> {

    private static final String TAG = "NewsShopAdapter";
    private Context mContext;
    private List<NewsOfCompany> mListNews;
    private ShareDialog mShareDialog;
    private LoveCouponAPI mApi;

    public NewsShopAdapter(Context context, List<NewsOfCompany> listNews, Fragment fragment) {
        mContext = context;
        mListNews = listNews;
        mShareDialog = new ShareDialog(fragment);
        mApi = MainApplication.getAPI();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        NewsOfCompany item = mListNews.get(position);
        if (item == null) {
            return;
        }

        String strCompany = MainApplication
                .getPreferences()
                .getString(MainApplication.COMPANY_SHOP, "");

        final Company company = new Gson().fromJson(strCompany, Company.class);

        if (company != null) {
            if (company.getName() != null) {
                holder.mTxtCompanyName.setText(company.getName());
            }
            if (company.getLogo() != null) {

                byte[] bytes = MainApplication.convertToBytes(company.getLogo());
                Glide.with(mContext)
                        .load(bytes)
                        .asBitmap()
                        .thumbnail(0.5f)
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
        if (item.getLink() != null) {
            holder.mTxtLink.setVisibility(View.VISIBLE);
            holder.mTxtLink.setText(item.getLink());
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
                    onClickShare(this.getAdapterPosition());
                    break;
                case R.id.linear_delete:
                    onClickMoreNews(this.getAdapterPosition(), v);
                    break;
                default:
                    break;
            }
        }
    }

    private void onClickMoreNews(final int position, View view) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_news, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_edit_news) {
                    Intent intent = new Intent(mContext, AddMessageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(MainApplication.WHAT_ADD_MESSAGES, MainApplication.WHAT_UPDATE_NEWS);
                    bundle.putString(MainApplication.DATA, mListNews.get(position).getMessage_id());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                } else if (item.getItemId() == R.id.menu_delete) {
                    onClickDeleteNews(position);
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void onClickDeleteNews(final int position) {

        final String idNewsOfCompany = mListNews.get(position).getMessage_id();
        new MaterialDialog
                .Builder(mContext)
                .icon(mContext.getResources().getDrawable(R.drawable.ic_delete_black_24px))
                .title(R.string.delete_news)
                .positiveText(R.string.agree)
                .positiveColor(mContext.getResources().getColor(R.color.title_bg))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteNewsOfCompany(idNewsOfCompany, position);
                    }
                })
                .show();
    }

    private void onClickLikeNews(int position, ViewHolder holder) {

        NewsOfCompany item = mListNews.get(position);
        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        final Company company = new Gson().fromJson(strCompany, Company.class);


        if (item.isLike()) {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.icon_heart));
            item.setLike(false);
            DatabaseManager.deleteOptionNews(item.getMessage_id(), MainApplication.SHOP);

        } else {
            holder.mImgLike.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            holder.mImageBookmarks.setText(mContext.getString(R.string.ic_start_like));
            holder.mImageBookmarks.setTextColor(mContext.getResources().getColor(R.color.heart_color));
            item.setLike(true);
            DatabaseManager.addOptionNews(item.getMessage_id(), company.getCompany_id(), MainApplication.SHOP);
        }
    }

    private void onClickShare(int position) {

        NewsOfCompany item = mListNews.get(position);

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        final Company company = new Gson().fromJson(strCompany, Company.class);

        Uri uriLink;
        if (item.getLink() != null) {

            uriLink = Uri.parse(item.getLink());
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

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);


        Log.d(TAG, "Id: " + idNews + " token: " + company.getWeb_token());

        Company company1 = new Company();
        company1.setCompany_id("2342423423");
        Until news = new Until(idNews);
        Call<Integer> call = mApi.deleteMessage(company.getWeb_token(), news);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == MainApplication.SUCCESS) {
                    getShowMessages(mContext.getString(R.string.delete_news_success));
                    DatabaseManager.deleteNewsOfCompany(idNews);
                    mListNews.remove(positionNews);
                    notifyItemRemoved(positionNews);
                    Log.d("NewsShopAdapter", "Delete : News Success");
                } else {
                    getShowMessages(mContext.getString(R.string.delete_news_error));
                }

                Log.d(TAG, "deleteNewsOfCompany " + response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getShowMessages(mContext.getString(R.string.delete_news_error));
                Log.d(TAG, t.toString());
            }
        });
    }

    private void getShowMessages(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
