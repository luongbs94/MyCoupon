package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ln.mycoupon.R;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mListImages = new ArrayList<>();

    public GridAdapter(Context mContext, ArrayList<String> mListImages) {
        this.mContext = mContext;
        this.mListImages = mListImages;
    }

    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridAdapter.ViewHolder holder, int position) {
        byte[] bytes = Base64.decode(mListImages.get(position), Base64.NO_WRAP);
        Glide.with(mContext)
                .load(bytes)
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(holder.imageView);

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