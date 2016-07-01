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
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.CompanyOfCustomer;
import com.ln.model.NewsOfLike;
import com.ln.mycoupon.R;
import com.ln.views.MyTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luongnguyen on 4/6/16.
 * <></>
 */

public class NewsCustomerAdapter extends RecyclerView.Adapter<NewsCustomerAdapter.ViewHolder> {

    private List<NewsOfLike> mListNews;
    private Context mContext;
    private ShareDialog mShareDialog;


    public NewsCustomerAdapter(Context context, List<NewsOfLike> listNews, Fragment fragment) {
        mContext = context;
        mListNews = listNews;
        try
        {
            mShareDialog = new ShareDialog(fragment);

        }catch (Exception e){

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final NewsOfLike news = mListNews.get(position);
        final int positionNews = position;

        CompanyOfCustomer company = SaveData.getCompany(news.getCompany_id());
        if (company != null) {
            holder.mTxtCompanyName.setText(company.getName());
            if (company.getLogo() != null) {
                Glide.with(mContext).load(MainApplication.convertToBytes(company.getLogo()))
                        .asBitmap()
                        .into(holder.mImgLogo);
            }
        }

        holder.mTxtTile.setText(news.getTitle());
        holder.mTxtContent.setText(news.getContent());
        holder.mTxtLink.setText(news.getLink());

        String strImages = news.getImages_link();
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

        if (news.isLike()) {
            holder.mImgLike.setImageResource(R.drawable.ic_heart_color);
        }

        holder.mLinearLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (news.isLike()) {
                    holder.mImgLike.setImageResource(R.drawable.ic_heart);
                    news.setLike(false);
                    MainApplication.mRealmController.deleteLikeNewsById(news.getMessage_id());


                } else {
                    holder.mImgLike.setImageResource(R.drawable.ic_heart_color);
                    news.setLike(true);
                    MainApplication.mRealmController.addLikeNewsByIdNews(news.getMessage_id());

                }
            }
        });

        holder.mLinearShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("http://188.166.179.187:3001/upload/ImageSelector_20160616_223027_19062016_010851.png");
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://google.com"))
                        .setContentTitle(news.getTitle())
                        .setContentDescription(news.getContent())
                        .setImageUrl(uri)
                        .build();


                try{
                    mShareDialog.show(content);

                }catch (Exception e){

                }

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
                        .show();

                dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MainApplication.mRealmController.addDeleteNewsByIdNews(news.getMessage_id());
                        mListNews.remove(positionNews);
                        notifyDataSetChanged();
                    }
                });

                dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return mListNews.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImgLogo, mImgLike, mImgShare, mImgDelete;
        private TextView mTxtTile, mTxtLink;
        private RecyclerView mRecyclerView;

        private MyTextView mTxtTime, mTxtContent;
        private TextView mTxtCompanyName;
        private LinearLayout mLinearLike, mLinearShare, mLinearDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.img_logo_news);
            mImgLike = (ImageView) itemView.findViewById(R.id.img_like_newx);
            mImgShare = (ImageView) itemView.findViewById(R.id.img_share_newx);
            mImgDelete = (ImageView) itemView.findViewById(R.id.img_delete_news);

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
}