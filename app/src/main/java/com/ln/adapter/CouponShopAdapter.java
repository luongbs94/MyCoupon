package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.app.MainApplication;
import com.ln.model.CompanyOfCustomer;
import com.ln.mycoupon.R;

import java.util.List;

/**
 * Created by luongnguyen on 6/7/16.
 * <></>
 */
public class CouponShopAdapter extends RecyclerView.Adapter<CouponShopAdapter.ViewHolder> {

    private List<CompanyOfCustomer> mListCompanyOfCustomer;
    private Context mContext;

    public CouponShopAdapter(Context context, List<CompanyOfCustomer> company) {
        mContext = context;
        mListCompanyOfCustomer = company;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_company, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CompanyOfCustomer item = mListCompanyOfCustomer.get(position);
        if (item != null) {
            if (item.getName() != null) {
                holder.companyName.setText(item.getName());
            }
            if (item.getAddress() != null) {
                holder.companyAddress.setText(item.getAddress());
            }

            if (item.getLogo() != null) {
                Glide.with(mContext)
                        .load(MainApplication.convertToBytes(item.getLogo()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_logo_blank)
                        .into(holder.mImgLogo);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListCompanyOfCustomer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgLogo;
        private TextView companyName, companyAddress;

        public ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.app_icon);
            companyName = (TextView) itemView.findViewById(R.id.company_name);
            companyAddress = (TextView) itemView.findViewById(R.id.company_address);
        }
    }
}