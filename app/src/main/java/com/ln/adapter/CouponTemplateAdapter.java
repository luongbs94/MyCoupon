package com.ln.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
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

import com.bumptech.glide.Glide;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.mycoupon.R;
import com.ln.mycoupon.TestQRCode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nhahv on 5/21/2016.
 * Set coupon for show
 */

public class CouponTemplateAdapter extends RecyclerView.Adapter<CouponTemplateAdapter.ViewHolder> {

    private List<CouponTemplate> mListCoupon;
    private Context mContext;


    public CouponTemplateAdapter(Context context, List<CouponTemplate> coupons) {

        mContext = context;
        mListCoupon = coupons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_coupon_template, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final CouponTemplate itemCoupon = mListCoupon.get(position);
        if (itemCoupon != null) {
            Company company = SaveData.company;
            if (company != null && company.getLogo() != null) {
                holder.mTxtNameCoupon.setText(company.getName());
                Glide.with(mContext).load(MainApplication.convertToBytes(company.getLogo()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_profile)
                        .into(holder.mImgLogo);
            }
            holder.mTxtPriceCoupon.setText(itemCoupon.getValue());

            Date date = convertStringToDate(itemCoupon.getCreated_date());
            String dayLeft = dayLeft(date, itemCoupon.getDuration()) + "";
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
//
                    PopupMenu popupMenu = new PopupMenu(mContext, view);
                    final MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.menu_delete_coupon, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_delete:
                                    deleteCouponTemplate(itemCoupon.getCoupon_template_id(), view, position);
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
    }

    private void deleteCouponTemplate(String coupon_template_id, final View view, final int position) {
        CouponTemplate template = new CouponTemplate();
        template.setCoupon_template_id(coupon_template_id);

        //template.created_date= new Date();

        Call<CouponTemplate> call2 = MainApplication.getAPI().deleteCouponTemplate(template);
        call2.enqueue(new Callback<CouponTemplate>() {

            @Override
            public void onResponse(Call<CouponTemplate> arg0,
                                   Response<CouponTemplate> arg1) {

                getSnackBar(view, mContext.getString(R.string.delete_coupon_success));
                mListCoupon.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CouponTemplate> arg0, Throwable arg1) {

                getSnackBar(view, mContext.getString(R.string.delete_coupon_fail));
            }
        });
    }


    private void getSnackBar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public int getItemCount() {
        return mListCoupon.size();
    }

    private long dayLeft(Date created_date, int duration) {
        Calendar last_cal = Calendar.getInstance();
        last_cal.setTime(created_date);
        last_cal.add(Calendar.DAY_OF_YEAR, duration);
        Date last_date = last_cal.getTime();

        long diff = last_date.getTime() - new Date().getTime();
        long dayLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (dayLeft < 0) {
            dayLeft = 0;
        }

        return dayLeft;
    }

    private Date convertStringToDate(String date) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        try {
            startDate = df.parse(date);
            return startDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgLogo, mImageMore;
        private TextView mTxtNameCoupon, mTxtPriceCoupon, mTxtDescription, mTxtTimeCoupon;
        private Button mQRCode;

        public ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.app_icon);
            mImageMore = (ImageView) itemView.findViewById(R.id.image_more);
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
