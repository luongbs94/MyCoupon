package com.ln.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.Message;
import com.ln.mycoupon.R;
import com.ln.views.MyTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nhahv on 5/21/2016.
 * adapter connect news fragments
 */
public class NewsShopAdapter extends RecyclerView.Adapter<NewsShopAdapter.ViewHolder> {

    private Context mContext;
    private List<Message> mListNews;

    public NewsShopAdapter(Context context, List<Message> listNews) {
        mContext = context;
        mListNews = listNews;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Message news = mListNews.get(position);
        final int positionNews = position;

        Company company = SaveData.company;
        if (company != null) {
            holder.mTxtCompanyName.setText(company.getName());
            if (company.getLogo() != null) {
                Glide.with(mContext).load(MainApplication.convertToBytes(company.getLogo()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_profile)
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


        holder.mImgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (holder.mImgLike.getDrawable().equals(mContext.getResources().getDrawable(R.drawable.ic_heart))) {
//                    holder.mImgLike.setImageResource(R.drawable.ic_heart_color);
//                } else {
//                    holder.mImgLike.setImageResource(R.drawable.ic_heart);
//                }
                holder.mImgLike.setImageResource(R.drawable.ic_heart_color);

            }
        });

        holder.mImgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.mImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Integer> call = MainApplication.getAPI().deleteMessage(news.getMessage_id());
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        mListNews.remove(positionNews);
                        notifyDataSetChanged();
                        getSnackBar(holder.mImgDelete, mContext.getString(R.string.delete_news_success));

                        Log.d("NewsShopAdapter", "Delete : News Success");
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.d("NewsShopAdapter", "Delete : News fails");
                    }
                });
//
//                MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext);
//                dialog.content(R.string.delete_news)
//                        .positiveText(R.string.agree)
//                        .negativeText(R.string.disagree)
//                        .positiveColor(mContext.getResources().getColor(R.color.title_bg))
//                        .negativeColor(mContext.getResources().getColor(R.color.title_bg))
//                        .show();
//
//                dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//
//                    }
//                });
//
//                dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialog.dismiss();
//                    }
//                });


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

        MyTextView mTxtTime, mTxtContent;
        TextView mTxtCompanyName;

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
        }
    }

    private void getSnackBar(View view, String messages) {
        Snackbar.make(view, messages, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}