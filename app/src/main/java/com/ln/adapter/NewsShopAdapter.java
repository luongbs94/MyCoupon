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
import com.ln.model.Company;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCompanyLike;
import com.ln.mycoupon.R;
import com.ln.views.IconTextView;
import com.ln.views.MyTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final NewsOfCompanyLike item = mListNews.get(position);
        final int positionNews = position;

        final String idNewsOfCompany = item.getMessage_id();

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        final Company company = new Gson().fromJson(strCompany, Company.class);

        if (company != null) {
            holder.mTxtCompanyName.setText(company.getName());
            if (company.getLogo() != null) {
                Glide.with(mContext).load(MainApplication.convertToBytes(company.getLogo()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_logo_blank)
                        .into(holder.mImgLogo);
            }
        }

        holder.mTxtTile.setText(item.getTitle());
        holder.mTxtContent.setText(item.getContent());
        holder.mTxtLink.setText(item.getLink());


        SimpleDateFormat fmt;
        if (MainApplication.getLanguage()) {
            fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        } else {
            fmt = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());

        }

        String date = fmt.format(item.getCreated_date());

        holder.mTxtTime.setText(date);

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
        });

        holder.mLinearShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String logoLink;
                if (company != null && company.getLogo_link() != null) {
                    logoLink = company.getLogo_link();
                } else {
                    logoLink = "http://api.lovecoupon.com:3000/logo/7.jpg";
                }


                Uri uri = Uri.parse(logoLink);
                ShareLinkContent content;
                if (item.getLink() != null) {
                    content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(item.getLink()))
                            .setContentTitle(item.getTitle())
                            .setContentDescription(item.getContent())
                            .setImageUrl(uri)
                            .build();
                } else {
                    content = new ShareLinkContent.Builder()
                            .setContentUrl(null)
                            .setContentTitle(item.getTitle())
                            .setContentDescription(item.getContent())
                            .setImageUrl(uri)
                            .build();
                }
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
                                deleteNewsOfCompany(idNewsOfCompany, positionNews);

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
        private IconTextView mImgLike, mImageBookmarks;

        private TextView mTxtTile, mTxtLink;
        private RecyclerView mRecyclerView;

        private MyTextView mTxtTime, mTxtContent;
        private TextView mTxtCompanyName;

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
        }
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
