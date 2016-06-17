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
import com.ln.mycoupon.PreviewImagesActivity;
import com.ln.mycoupon.R;

import java.io.Serializable;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mListImages;

    public GridAdapter(Context mContext, List<String> listImages) {
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

        String strImage = mListImages.get(position);
        if (strImage != null) {
            Glide.with(mContext).load(strImage).centerCrop().into(holder.imageView);
        }
        final int positionImage = position;

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PreviewImagesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(MainApplication.POSITION, positionImage); // vi tri hien thi anh
                bundle.putSerializable(MainApplication.LIST_IMAGES, (Serializable) mListImages); // list anh
                intent.putExtra(MainApplication.DATA, bundle);
                Toast.makeText(mContext, "Log toast" + positionImage, Toast.LENGTH_SHORT).show();
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mListImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}