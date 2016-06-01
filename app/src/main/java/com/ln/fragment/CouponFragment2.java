package com.ln.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ln.adapter.CouponTemplateAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.CouponTemplate;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/8/16.
 */
public class CouponFragment2 extends Fragment {
    private LoveCouponAPI mApiServices;

    private View mView;
    private LinearLayout mLnLayout;
    private RecyclerView mRecCoupon;
    private List<CouponTemplate> mListCoupon = new ArrayList<>();
    private String TAG = getClass().getSimpleName();

    public CouponFragment2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coupon, container, false);

        initViews();

        /*
          MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.title_delete_coupon)
                        .content(R.string.content_delete_coupon)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                deleteCouponTemplate(listCoupons.get(i).getCoupon_template_id());
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

         */

        getCouponTemplate();

        return mView;
    }

    private void initViews() {
        mRecCoupon = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecCoupon.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLnLayout = (LinearLayout) mView.findViewById(R.id.ln_fragment_coupon);
    }

    public void deleteCouponTemplate(String coupon_template_id) {
        CouponTemplate template = new CouponTemplate();
        template.setCoupon_template_id(coupon_template_id);

        //template.created_date= new Date();

        Call<CouponTemplate> call2 = mApiServices.deleteCouponTemplate(template);
        call2.enqueue(new Callback<CouponTemplate>() {

            @Override
            public void onResponse(Call<CouponTemplate> arg0,
                                   Response<CouponTemplate> arg1) {

                getSnackBar(getString(R.string.delete_coupon_success));
                getCouponTemplate();
            }

            @Override
            public void onFailure(Call<CouponTemplate> arg0, Throwable arg1) {
                Log.d(TAG, "fail");
                getSnackBar(getString(R.string.delete_coupon_fail));
            }
        });
    }

    private void getSnackBar(String text) {
        Snackbar.make(mLnLayout, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void getCouponTemplate() {

        mListCoupon.clear();
        Call<List<CouponTemplate>> call = mApiServices.getCouponTemplatesByCompanyId(7);
        call.enqueue(new Callback<List<CouponTemplate>>() {

            @Override
            public void onResponse(Call<List<CouponTemplate>> arg0,
                                   Response<List<CouponTemplate>> arg1) {
                mListCoupon = arg1.body();

                Log.d(TAG, mListCoupon.size() + "");

                CouponTemplateAdapter adapter = new CouponTemplateAdapter(getActivity(), mListCoupon);
                mRecCoupon.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<CouponTemplate>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        getCouponTemplate();
    }
}
