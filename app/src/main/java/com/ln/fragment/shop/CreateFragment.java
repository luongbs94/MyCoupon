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
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.Coupon;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateFragment extends Fragment {

    private LoveCouponAPI mApiServices;

    private View mView;
    private RecyclerView mRecyclerCreate;
    private List<Coupon> mListCoupon = new ArrayList<>();
    private HistoryAdapter mCouponAdapter;

    private String TAG = getClass().getSimpleName();
    private long utc1;
    private long utc2;

    private SwipeRefreshLayout swipeContainer;
    private TextView textView;
    private int mType = MainApplication.TYPE_CREATE;


    public static CreateFragment getInstances(int type) {
        CreateFragment fragment = new CreateFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MainApplication.TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

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
        mType = getArguments().getInt(MainApplication.TYPE);

        initViews();
        getCreateCoupon();

        return mView;
    }

    private void initViews() {
        mRecyclerCreate = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerCreate.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);
        textView = (TextView) mView.findViewById(R.id.text_no_data);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCreateCoupon();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getCreateCoupon() {

        if (!MainApplication.sIsAdmin) {
            ((TextView) mView.findViewById(R.id.text_no_data)).setText(R.string.only_admin);
            return;
        }
        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);

        if (mType == MainApplication.TYPE_CREATE) {

            Call<List<Coupon>> listCoupon = mApiServices.getCreatedCoupon(company.getWeb_token(), company.getCompany_id() + "", utc1, utc2);
            listCoupon.enqueue(new Callback<List<Coupon>>() {
                @Override
                public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                    mListCoupon = new ArrayList<>();
                    mListCoupon = response.body();

                    mCouponAdapter = new HistoryAdapter(getActivity(), mListCoupon);
                    mRecyclerCreate.setAdapter(mCouponAdapter);
                    swipeContainer.setRefreshing(false);

                    if (mListCoupon.size() > 0) {
                        mRecyclerCreate.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                    } else {
                        mRecyclerCreate.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<List<Coupon>> call, Throwable t) {
                    swipeContainer.setRefreshing(false);

                }
            });
        } else if (mType == MainApplication.TYPE_USE) {
            Call<List<Coupon>> listCoupon = mApiServices.getUsedCoupon(company.getWeb_token(), company.getCompany_id() + "", utc1, utc2);
            listCoupon.enqueue(new Callback<List<Coupon>>() {
                @Override
                public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                    mListCoupon = response.body();
                    mCouponAdapter = new HistoryAdapter(getActivity(), mListCoupon);
                    mRecyclerCreate.setAdapter(mCouponAdapter);
                    swipeContainer.setRefreshing(false);

                    if (mListCoupon.size() > 0) {
                        mRecyclerCreate.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                    } else {
                        mRecyclerCreate.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<List<Coupon>> call, Throwable t) {
                    swipeContainer.setRefreshing(false);
                }
            });
        }

    }

    public void getData(long time) {
        utc1 = time;
        utc2 = (time + 24 * 3600 * 1000);
        getCreateCoupon();
        Log.d(TAG, "getData" + utc1 + " - " + utc2);
    }
}
