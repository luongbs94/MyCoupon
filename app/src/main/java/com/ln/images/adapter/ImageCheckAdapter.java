package com.ln.images.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ln.app.MainApplication;
import com.ln.images.models.ImagesManager;
import com.ln.images.models.LocalMedia;
import com.ln.mycoupon.PreviewImagesActivity;
import com.ln.mycoupon.R;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nhahv on 7/27/2016.
 * <></>
 */
public class ImageCheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_PICTURE = 2;
    private static final int maxSelectNum = 9;

    private Context mContext;
    private boolean showCamera = true;

    private List<LocalMedia> mListImages = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();

    private OnImageSelectChangedListener imageSelectChangedListener;

    public ImageCheckAdapter(Context context) {
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
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA) {
            return new HeaderViewHolder(LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.item_chose_camera, parent, false));
        } else {
            return new ViewHolder(LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.item_chose_picture_2, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_CAMERA) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageSelectChangedListener != null) {
                        imageSelectChangedListener.onTakePhoto();
                    }
                }
            });
        } else {
            final ViewHolder contentHolder = (ViewHolder) holder;
            final LocalMedia image = mListImages.get(showCamera ? position - 1 : position);

            Glide.with(mContext)
                    .load(new File(image.getPath()))
                    .centerCrop()
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .dontAnimate()
                    .into(contentHolder.picture);

            selectImage(contentHolder, isSelected(image));

            contentHolder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeCheckboxState(contentHolder, image);
                }
            });

            contentHolder.picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PreviewImagesActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(MainApplication.POSITION, holder.getAdapterPosition());
                    bundle.putParcelable(MainApplication.LIST_IMAGES, Parcels.wrap(mListImages));
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return showCamera ? mListImages.size() + 1 : mListImages.size();
    }

    private void changeCheckboxState(ViewHolder contentHolder, LocalMedia image) {
        boolean isChecked = contentHolder.check.isSelected();
        if (selectImages.size() >= maxSelectNum && !isChecked) {
            Toast.makeText(mContext, mContext.getString(R.string.message_max_num, maxSelectNum + ""), Toast.LENGTH_LONG).show();
            return;
        }
        if (isChecked) {
            for (LocalMedia media : selectImages) {
                if (media.getPath().equals(image.getPath())) {
                    selectImages.remove(media);
                    break;
                }
            }
        } else {
            selectImages.add(image);
        }
        selectImage(contentHolder, !isChecked);
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }

    public List<LocalMedia> getSelectedImages() {
        return selectImages;
    }


    private boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    private void selectImage(ViewHolder holder, boolean isChecked) {
        holder.check.setSelected(isChecked);
        if (isChecked) {
            holder.picture.setColorFilter(mContext.getResources().getColor(R.color.image_overlay2), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.picture.setColorFilter(mContext.getResources().getColor(R.color.image_overlay), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        View headerView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = itemView;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;
        ImageView check;

        View contentView;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            picture = (ImageView) itemView.findViewById(R.id.picture);
            check = (ImageView) itemView.findViewById(R.id.check);
        }

    }

    public interface OnImageSelectChangedListener {
        void onChange(List<LocalMedia> selectImages);

        void onTakePhoto();
    }

    public void setOnImageSelectChangedListener(OnImageSelectChangedListener imageSelectChangedListener) {
        this.imageSelectChangedListener = imageSelectChangedListener;
    }
}
