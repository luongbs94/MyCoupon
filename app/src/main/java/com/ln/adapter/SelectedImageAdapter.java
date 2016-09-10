package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ln.images.models.LocalMedia;
import com.ln.mycoupon.R;

import java.util.List;

/**
 * Created by Nhahv on 5/11/2016.
 * <></>
 */
public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ViewHolder> {

    private Context mContext;
    private List<LocalMedia> mListImages;
    private OnClickRemoveImages mOnClick;

    public SelectedImageAdapter(Context mContext, List<LocalMedia> mListImages) {
        this.mContext = mContext;
        this.mListImages = mListImages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_chose, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        LocalMedia item = mListImages.get(position);
        Glide.with(mContext)
                .load(item.getPath())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImages);
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClick != null) {
                    mOnClick.remove(holder.getAdapterPosition());
                }
                mListImages.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImages;
        private TextView mDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            mImages = (ImageView) itemView.findViewById(R.id.picture);
            mDelete = (TextView) itemView.findViewById(R.id.check);
        }
    }

    public void setOnClickRemoveImages(OnClickRemoveImages onClick) {
        mOnClick = onClick;
    }

    public interface OnClickRemoveImages {
        void remove(int position);
    }
}
