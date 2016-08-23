package com.ln.fragment.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ln.adapter.HistoryAdapter;
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.mycoupon.R;
import com.ln.until.UntilCoupon;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UseFragment extends Fragment {

    private LoveCouponAPI mApiServices;

    private String TAG = getClass().getSimpleName();
    private View mView;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView mRecyclerView;
    private long utc1;
    private long utc2;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApiServices = MainApplication.getAPI();
        Date date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        utc1 = date.getTime();
        utc2 = (date.getTime() + 24 * 3600 * 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_create, container, false);

        initViews();
        getListCoupon();
        return mView;
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListCoupon();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getListCoupon() {

        if (!MainApplication.getPreferences().getBoolean(MainApplication.ADMIN, false)) {
            ((TextView) mView.findViewById(R.id.text_no_data)).setText(R.string.only_admin);
            mRecyclerView.setVisibility(View.GONE);
            return;
        }
        mRecyclerView.setVisibility(View.VISIBLE);

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);

        Call<List<UntilCoupon>> listCoupon = mApiServices.getUsedCoupon(company.getWeb_token(), company.getCompany_id() + "", utc1, utc2);
        listCoupon.enqueue(new Callback<List<UntilCoupon>>() {
            @Override
            public void onResponse(Call<List<UntilCoupon>> call, Response<List<UntilCoupon>> response) {
                HistoryAdapter mAdapter = new HistoryAdapter(getActivity(), response.body());
                mRecyclerView.setAdapter(mAdapter);
                swipeContainer.setRefreshing(false);
                for (UntilCoupon item : response.body()) {
                    Log.d(TAG, "getListCoupon" + item.getCoupon_id() + "\n");
                }

                Log.d(TAG, "getListCoupon" + response.body().size() + " ");
            }

            @Override
            public void onFailure(Call<List<UntilCoupon>> call, Throwable t) {
                Log.d(TAG, t.toString());
                swipeContainer.setRefreshing(false);
            }
        });

    }

    public void getData(long time) {
        utc1 = time;
        utc2 = (time + 24 * 3600 * 1000);
        getListCoupon();
        Log.d(TAG, "getData" + utc1 + " - " + utc2);
    }
}
