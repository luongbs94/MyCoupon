package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.mycoupon.R;
import com.ln.mycoupon.TestQRCode;
import com.ln.views.IconTextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nhahv on 5/21/2016.
 * Set coupon for show
 */

public class CouponTemplateAdapter
        extends RecyclerView.Adapter<CouponTemplateAdapter.ViewHolder> {

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

        final CouponTemplate itemCoupon = mListCoupon.get(position);

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);

        if (company != null && company.getLogo() != null) {
            holder.mTxtNameCoupon.setText(company.getName());
            Glide.with(mContext).load(MainApplication.convertToBytes(company.getLogo()))
                    .asBitmap()
                    .placeholder(R.drawable.ic_logo_blank)
                    .into(holder.mImgLogo);
        }

        holder.mTxtPriceCoupon.setText(itemCoupon.getValue());

        Date date = convertStringToDate(itemCoupon.getCreated_date());
        String dayLeft = MainApplication.dayLeft(date, itemCoupon.getDuration()) + "";
        String day = dayLeft + " ngày";
        holder.mTxtTimeCoupon.setText(day);
        holder.mTxtDescription.setText(itemCoupon.getContent());

        holder.mQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, TestQRCode.class);
                intent.putExtra(MainApplication.VALUE, itemCoupon.getValue());
                intent.putExtra(MainApplication.DURATION, itemCoupon.getDuration());
                intent.putExtra(MainApplication.COUPON_TEMpLATE_ID, itemCoupon.getCoupon_template_id());
                mContext.startActivity(intent);
            }
        });

        holder.mImageMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                final MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_delete_coupon, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                deleteCouponTemplate(itemCoupon.getCoupon_template_id());
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void deleteCouponTemplate(final String idCoupon) {
        CouponTemplate coupon = new CouponTemplate(idCoupon);
        Call<Integer> delete = MainApplication.getAPI().deleteCouponTemplate(coupon);
        delete.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.body() == MainApplication.SUCCESS) {
                    getShowMessages(mContext.getString(R.string.delete_coupon_success));
                    MainApplication.mRealmController.deleteCouponTemplateById(idCoupon);
                    notifyDataSetChanged();
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


    private Date convertStringToDate(String date) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate;
        try {
            startDate = df.parse(date);
            return startDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgLogo;
        private IconTextView mImageMore;
        private TextView mTxtNameCoupon, mTxtPriceCoupon, mTxtDescription, mTxtTimeCoupon;
        private Button mQRCode;

        ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.app_icon);
            mImageMore = (IconTextView) itemView.findViewById(R.id.image_more);
            mTxtNameCoupon = (TextView) itemView.findViewById(R.id.txt_company_name);
            mTxtPriceCoupon = (TextView) itemView.findViewById(R.id.txt_price_coupon);
            mTxtTimeCoupon = (TextView) itemView.findViewById(R.id.txt_time);
            mTxtDescription = (TextView) itemView.findViewById(R.id.txt_description);
            mQRCode = (Button) itemView.findViewById(R.id.btn_qr_code);

            if (!MainApplication.sIsAdmin) {
                mImageMore.setVisibility(View.INVISIBLE);
            }
        }
    }
}
