package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.until.Until;
import com.ln.mycoupon.R;
import com.ln.mycoupon.TestQRCode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponTemplateAdapter
        extends RecyclerView.Adapter<CouponTemplateAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private List<CouponTemplate> mListCoupon;
    private Context mContext;

    public CouponTemplateAdapter(Context context, List<CouponTemplate> coupons) {
        mContext = context;
        mListCoupon = coupons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.item_coupon_template, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CouponTemplate item = mListCoupon.get(position);

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);

        if (company != null && company.getLogo() != null) {
            holder.mTxtNameCoupon.setText(company.getName());
            byte[] bytes = MainApplication.convertToBytes(company.getLogo());
            Glide.with(mContext).load(bytes)
                    .asBitmap()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_logo_blank)
                    .into(holder.mImgLogo);
        }

        if (item.getValue() != null) {
            holder.mTxtPriceCoupon.setText(item.getValue());
        }
        holder.mTxtTimeCoupon.setText(
                mContext.getString(
                        R.string.time_coupon_template,
                        item.getDuration() + ""
                )
        );

        if (item.getContent() != null) {
            holder.mTxtDescription.setText(item.getContent());
        }
    }

    private void deleteCouponTemplate(final String idCoupon, final int position) {
        Until coupon = new Until();
        coupon.setCoupon_template_id(idCoupon);

        final String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company mCompany = new Gson().fromJson(strCompany, Company.class);

        Log.d(TAG, "idnews: " + idCoupon + " - token: " + mCompany.getWeb_token());
        Call<Integer> delete = MainApplication.getAPI().deleteCouponTemplate(mCompany.getWeb_token(), coupon);
        delete.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.body() == MainApplication.SUCCESS) {
                    getShowMessages(mContext.getString(R.string.delete_coupon_success));
                    DatabaseManager.deleteCouponTemplate(idCoupon);
                    mListCoupon.remove(position);
                    notifyItemRemoved(position);
                } else {
                    getShowMessages(mContext.getString(R.string.delete_coupon_fail));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getShowMessages(mContext.getString(R.string.delete_coupon_fail));
            }
        });
    }

    private void getShowMessages(String messages) {
        Toast.makeText(mContext, messages, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mListCoupon.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImgLogo;
        private TextView mTxtNameCoupon, mTxtPriceCoupon, mTxtDescription, mTxtTimeCoupon;

        ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.app_icon);

            mTxtNameCoupon = (TextView) itemView.findViewById(R.id.txt_company_name);
            mTxtPriceCoupon = (TextView) itemView.findViewById(R.id.txt_price_coupon);
            mTxtTimeCoupon = (TextView) itemView.findViewById(R.id.txt_time);
            mTxtDescription = (TextView) itemView.findViewById(R.id.txt_description);

            (itemView.findViewById(R.id.btn_qr_code)).setOnClickListener(this);
            (itemView.findViewById(R.id.image_more)).setOnClickListener(this);
            (itemView.findViewById(R.id.txt_company_name)).setOnClickListener(this);
            (itemView.findViewById(R.id.txt_price_coupon)).setOnClickListener(this);
            (itemView.findViewById(R.id.txt_description)).setOnClickListener(this);
            (itemView.findViewById(R.id.linear_time)).setOnClickListener(this);
            mImgLogo.setOnClickListener(this);

            if (!MainApplication.getPreferences().getBoolean(MainApplication.ADMIN, false)) {
                (itemView.findViewById(R.id.image_more)).setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_qr_code:
                case R.id.linear_time:
                case R.id.txt_company_name:
                case R.id.txt_price_coupon:
                case R.id.txt_description:
                case R.id.app_icon:
                    onClickBtnQRCode(this.getAdapterPosition());
                    break;
                case R.id.image_more:
                    onClickMore(this.getAdapterPosition(), v);
                    break;
                default:
                    break;
            }
        }
    }

    private void onClickBtnQRCode(int position) {

        CouponTemplate item = mListCoupon.get(position);
        Intent intent = new Intent(mContext, TestQRCode.class);

        Bundle bundle = new Bundle();
        bundle.putString(MainApplication.VALUE, item.getValue());
        bundle.putInt(MainApplication.DURATION, item.getDuration());
        bundle.putString(MainApplication.COUPON_TEMpLATE_ID, item.getCoupon_template_id());
        bundle.putString(MainApplication.CONTENT_COUPON, item.getContent());
        intent.putExtras(bundle);

        mContext.startActivity(intent);
    }

    private void onClickMore(final int position, View view) {

        final CouponTemplate itemCoupon = mListCoupon.get(position);

        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu
                .getMenuInflater()
                .inflate(R.menu.menu_delete_coupon, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        deleteCouponTemplate(itemCoupon.getCoupon_template_id(), position);
                        break;
                    case R.id.menu_qrCode:
                        onClickBtnQRCode(position);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
