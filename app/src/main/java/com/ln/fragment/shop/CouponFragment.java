package com.ln.fragment.shop;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ln.adapter.CouponTemplateAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.CouponTemplate;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/6/16.
 */
public class CouponFragment extends Fragment {

    private LoveCouponAPI mApiServices;

    private View mView;
    private LinearLayout mLnLayout;
    private RecyclerView mRecCoupon;
    private SwipeRefreshLayout swipeContainer;

    private List<CouponTemplate> mListCoupon = new ArrayList<>();
    private String TAG = getClass().getSimpleName();

    public CouponFragment() {
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

        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCouponTemplate();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        initViews();
        getCouponTemplate();

        return mView;
    }

    private void initViews() {
        mRecCoupon = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecCoupon.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLnLayout = (LinearLayout) mView.findViewById(R.id.ln_fragment_coupon);
    }

    private void getSnackBar(String text) {
        Snackbar.make(mLnLayout, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void getCouponTemplate() {

        mListCoupon.clear();

        int idCompany;
        if (SaveData.company == null) {
            idCompany = MainApplication.sIdCompany;
        } else {
            idCompany = SaveData.company.getCompany_id();
        }

        //  Call<List<CouponTemplate>> call = mApiServices.getCouponTemplates(SaveData.web_token, SaveData.company.getCompany_id());
        Call<List<CouponTemplate>> call = mApiServices.getCouponTemplates("abc", idCompany);
        call.enqueue(new Callback<List<CouponTemplate>>() {

            @Override
            public void onResponse(Call<List<CouponTemplate>> arg0,
                                   Response<List<CouponTemplate>> arg1) {
                mListCoupon = arg1.body();

                CouponTemplateAdapter adapter = new CouponTemplateAdapter(getActivity(), mListCoupon);
                mRecCoupon.setAdapter(adapter);
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<List<CouponTemplate>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
                swipeContainer.setRefreshing(false);

            }
        });
    }
}
