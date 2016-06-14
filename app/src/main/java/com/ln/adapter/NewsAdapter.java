package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.ItemImage;
import com.ln.model.ListItemImages;
import com.ln.model.Message;
import com.ln.mycoupon.R;
import com.ln.views.MyTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nhahv on 5/21/2016.
 *
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {


    private Context mContext;
    private List<Message> mListNews;


    private List<ListItemImages> mListImages = new ArrayList<>();
    private List<String> mListIdNews = new ArrayList<>();
    private List<GridAdapter> mListGridAdapters;

    private String TAG = getClass().getSimpleName();

    private List<ItemImage> mListItemImages = new ArrayList<>();
    private int mPosition;

    private ListItemImages mList[];

    public NewsAdapter(Context context, List<Message> listNews) {
        mContext = context;
        mListNews = listNews;

        mList = new ListItemImages[mListNews.size()];

        for (Message message : mListNews) {
            mListIdNews.add(message.getMessage_id());
        }

        for (int i = 0; i < mList.length; i++) {
            mList[i] = new ListItemImages();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Message news = mListNews.get(position);
        mPosition = position;
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
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        }


        private boolean isExists(ItemImage itemImage, List<ItemImage> mListImages) {
            for (ItemImage image : mListImages) {
                if (image.getImages().equals(itemImage.getImages())) {
                    return true;
                }
            }

            return false;
        }
    }
}
