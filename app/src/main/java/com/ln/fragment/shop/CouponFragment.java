package com.ln.fragment.shop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.ln.adapter.CouponTemplateAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.model.Company;
import com.ln.model.CouponTemplate;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponFragment extends Fragment {

    private String TAG = getClass().getSimpleName();

    private LoveCouponAPI mApiServices;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView mRecCoupon;
    private Company mCompany;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        mCompany = new Gson().fromJson(strCompany, Company.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_coupon, container, false);

        initViews(mView);
        setCouponTemplate();
        setHasOptionsMenu(false);

        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initViews(View mView) {

        mRecCoupon = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecCoupon.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecCoupon.setHasFixedSize(true);

        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getCouponTemplate();
                    }
                });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    public void setCouponTemplate() {

        List<CouponTemplate> mListCoupon = new ArrayList<>();
        mListCoupon.addAll(DatabaseManager.getListCouponTemplate());
        CouponTemplateAdapter adapter = new CouponTemplateAdapter(getActivity(), mListCoupon);
        mRecCoupon.setAdapter(adapter);
        swipeContainer.setRefreshing(false);
    }


    public void getCouponTemplate() {


        if (mCompany != null) {

            Call<List<CouponTemplate>> couponShop = mApiServices.getCouponTemplates("abc", mCompany.getCompany_id());
            couponShop.enqueue(new Callback<List<CouponTemplate>>() {
                @Override
                public void onResponse(Call<List<CouponTemplate>> call, Response<List<CouponTemplate>> response) {
                    if (response.body() != null) {
                        DatabaseManager.addListCouponTemplate(response.body());
                        setCouponTemplate();
                        swipeContainer.setRefreshing(false);
                        Log.d(TAG, "getCouponTemplate " + response.body().size());
                    } else {
                        Log.d(TAG, "getCouponTemplate " + "null");
                    }
                }

                @Override
                public void onFailure(Call<List<CouponTemplate>> call, Throwable t) {
                    Log.d(TAG, "getCouponTemplate " + "onFailure");
                    swipeContainer.setRefreshing(false);
                }
            });
        }
    }
}
