package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.model.ItemImage;
import com.ln.mycoupon.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nhahv on 5/11/2016.
 * <></>
 */
public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ViewHolder> {

    private Context mContext;
    private List<ItemImage> mListImages;

    public SelectedImageAdapter(Context mContext, List<ItemImage> mListImages) {
        this.mContext = mContext;
        this.mListImages = mListImages;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chose, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Glide.with(mContext)
                .load(new File(mListImages.get(position).getPath()))
                .centerCrop()
                .fitCenter()
                .into(holder.mImages);

        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
