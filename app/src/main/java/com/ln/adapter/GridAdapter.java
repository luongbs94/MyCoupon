package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ln.app.MainApplication;
import com.ln.images.models.LocalMedia;
import com.ln.mycoupon.PreviewImagesActivity;
import com.ln.mycoupon.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private Context mContext;
    private List<LocalMedia> mListImages;

    GridAdapter(Context mContext, List<LocalMedia> listImages) {
        this.mContext = mContext;
        mListImages = listImages;
    }

    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GridAdapter.ViewHolder holder, int position) {

        LocalMedia item = mListImages.get(position);
        if (item != null) {
            Picasso.with(mContext)
                    .load(item.getPath())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mListImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image) {
                Intent intent = new Intent(mContext, PreviewImagesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(MainApplication.POSITION, this.getAdapterPosition());
                bundle.putSerializable(MainApplication.LIST_IMAGES, (Serializable) mListImages);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        }
    }
}