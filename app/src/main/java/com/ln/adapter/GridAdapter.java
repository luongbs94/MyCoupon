package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ln.app.MainApplication;
import com.ln.model.ItemImage;
import com.ln.model.ListItemImages;
import com.ln.mycoupon.PreviewImagesActivity;
import com.ln.mycoupon.R;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private Context mContext;
    private ListItemImages mListImages = new ListItemImages();

    public GridAdapter(Context mContext, List<ItemImage> listImages) {
        this.mContext = mContext;
        mListImages.setListImages(listImages);
    }

    public GridAdapter(Context mContext, ListItemImages listImages) {
        this.mContext = mContext;
        mListImages = listImages;
    }

    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridAdapter.ViewHolder holder, final int position) {
        byte[] bytes = MainApplication.convertToBytes(mListImages.getListImages().get(position).getImages());
        Glide.with(mContext)
                .load(bytes)
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PreviewImagesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(MainApplication.POSITION, position); // vi tri hien thi anh
                bundle.putSerializable(MainApplication.LIST_IMAGES, mListImages); // list anh
                intent.putExtra(MainApplication.DATA, bundle);
                Toast.makeText(mContext, "Log toast", Toast.LENGTH_SHORT).show();
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mListImages.getListImages().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}