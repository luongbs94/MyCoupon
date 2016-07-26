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
import com.ln.model.Coupon;
import com.ln.mycoupon.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nhahv on 5/21/2016.
 * <></>
 */

public class CouponTemplateClientAdapter extends RecyclerView.Adapter<CouponTemplateClientAdapter.ViewHolder> {

    private List<Coupon> mListCoupon;
    private Context mContext;
    private CompanyOfCustomer mCompanyOfCustomer;

    public CouponTemplateClientAdapter(Context context, CompanyOfCustomer company) {
        mContext = context;
        mListCoupon = company.getCoupon();
        mCompanyOfCustomer = company;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_coupon_customer, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Coupon item = mListCoupon.get(position);
        if (item != null) {


            if (item.getValue() != null) {
                holder.textPrice.setText(item.getValue());
            }

            if (mCompanyOfCustomer.getName() != null) {
                holder.textNameCompany.setText(mCompanyOfCustomer.getName());
            }

            if (item.getContent() != null) {
                holder.textDescription.setText(item.getContent());
            }


            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            if (MainApplication.getLanguage()) {
                fmt = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            }

            holder.textTimeShelf.setText(fmt.format
                    (MainApplication
                            .convertDate(item.getCreated_date(),
                                    item.getDuration())));

            String dayLeft = MainApplication.dayLeft(item.getCreated_date(), item.getDuration()) + "";
            holder.textDayShelf.setText(dayLeft);

            if (mCompanyOfCustomer.getLogo() != null) {
                Glide.with(mContext).load(MainApplication
                        .convertToBytes(mCompanyOfCustomer.getLogo()))
                        .asBitmap()
                        .into(holder.mImageLogo);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListCoupon.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageLogo;
        private TextView textNameCompany, textTimeShelf, textDayShelf,
                textPrice, textDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageLogo = (ImageView) itemView.findViewById(R.id.app_icon);
            textNameCompany = (TextView) itemView.findViewById(R.id.text_name_company);
            textTimeShelf = (TextView) itemView.findViewById(R.id.text_time_shelf);
            textDayShelf = (TextView) itemView.findViewById(R.id.text_day_shelf);
            textPrice = (TextView) itemView.findViewById(R.id.text_price_);
            textDescription = (TextView) itemView.findViewById(R.id.text_description);
        }
    }

}
