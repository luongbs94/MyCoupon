package com.ln.images.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ln.images.models.ImagesManager;
import com.ln.images.models.LocalMedia;
import com.ln.mycoupon.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nhahv on 7/27/2016.
 * <></>
 */
public class ImagesCropAdapter extends RecyclerView.Adapter<ImagesCropAdapter.ViewHolder> {

    private Context mContext;
    private List<LocalMedia> mListImages = new ArrayList<>();
    private OnClickCropImages mOnClickCropImages;

    public ImagesCropAdapter(Context context) {
        mContext = context;
        setListImages(ImagesManager.TYPE_ALL_IMAGE);

    }

    public void setListImages(int type) {
        switch (type) {
            case ImagesManager.TYPE_ALL_IMAGE:
                mListImages = ImagesManager.getListImageAll();
                break;
            case ImagesManager.TYPE_INTERNAL:
                mListImages = ImagesManager.getListImageInternal();
                break;
            case ImagesManager.TYPE_SD_CARD:
                mListImages = ImagesManager.getListImageExternal();
                break;
            default:
                mListImages = ImagesManager.getListImageExternal();
                break;
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_chose_picture, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final LocalMedia image = mListImages.get(position);

        Glide.with(mContext)
                .load(new File(image.getPath()))
                .centerCrop()
                .thumbnail(0.5f)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .dontAnimate()
                .into(holder.picture);

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickCropImages != null) {
                    mOnClickCropImages.clickNextCropImages(mListImages.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListImages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView picture;

        public ViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.image_picture);
        }
    }

    public void setOnClickCropImages(OnClickCropImages onClickCropImages) {
        mOnClickCropImages = onClickCropImages;
    }

    public interface OnClickCropImages {
        void clickNextCropImages(LocalMedia position);
    }
}
