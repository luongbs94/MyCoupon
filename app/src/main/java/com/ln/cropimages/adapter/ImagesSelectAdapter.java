package com.ln.cropimages.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ln.mycoupon.R;
import com.yongchun.library.model.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nhahv on 7/27/2016.
 * <></>
 */
public class ImagesSelectAdapter extends RecyclerView.Adapter<ImagesSelectAdapter.ViewHolder> {

    private Context mContext;
    private List<LocalMedia> mListImages = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();

    private OnImageSelectChangedListener imageSelectChangedListener;

    public ImagesSelectAdapter(Context context) {
        mContext = context;
    }

    public void bindImages(List<LocalMedia> images) {
        this.mListImages = images;
        notifyDataSetChanged();
    }

    public void bindSelectImages(List<LocalMedia> images) {
        this.selectImages = images;
        notifyDataSetChanged();
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
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
                .placeholder(com.yongchun.library.R.drawable.image_placeholder)
                .error(com.yongchun.library.R.drawable.image_placeholder)
                .dontAnimate()
                .into(holder.picture);

        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // su  kien
                imageSelectChangedListener.onPictureClick(image, holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mListImages.size();
    }


    public List<LocalMedia> getSelectedImages() {
        return selectImages;
    }

    public List<LocalMedia> getmListImages() {
        return mListImages;
    }

    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView picture;
        private View contentView;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            picture = (ImageView) itemView.findViewById(R.id.image_picture);
        }

    }

    public interface OnImageSelectChangedListener {
        void onChange(List<LocalMedia> selectImages);

        void onPictureClick(LocalMedia media, int position);
    }

    public void setOnImageSelectChangedListener(OnImageSelectChangedListener imageSelectChangedListener) {
        this.imageSelectChangedListener = imageSelectChangedListener;
    }
}
