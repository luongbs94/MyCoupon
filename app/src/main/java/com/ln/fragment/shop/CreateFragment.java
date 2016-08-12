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
import com.ln.adapter.CreateCouponAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.Coupon;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.Calendar;
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
    private CreateCouponAdapter mCouponAdapter;

    private String TAG = getClass().getSimpleName();
    private long utc1;
    private long utc2;
    private static boolean isInitRecyclerView;
    private Calendar calendar;

    private SwipeRefreshLayout swipeContainer;
    private TextView textView;


    public CreateFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        calendar = Calendar.getInstance();
        Date date = new Date();

        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        utc1 = date.getTime();
        utc2 = (date.getTime() + 24 * 3600 * 1000);

        getCreateCoupon();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_create, container, false);

        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);
        textView = (TextView) mView.findViewById(R.id.text_no_data);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCreateCoupon();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        initViews();
        getCreateCoupon();

        return mView;
    }

    private void initViews() {
        mRecyclerCreate = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerCreate.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getCreateCoupon() {

        Log.d(TAG, "getCreateCoupon" + utc1 + " - " + utc2);
        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);
        Call<List<Coupon>> listCoupon = mApiServices.getCreatedCoupon(company.getWeb_token(), company.getCompany_id() + "", utc1, utc2);
        listCoupon.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                mListCoupon = new ArrayList<>();
                mListCoupon = response.body();

                mCouponAdapter = new CreateCouponAdapter(getActivity(), mListCoupon);
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
    }

    public void getData(long time) {
        utc1 = time;
        utc2 = (time + 24 * 3600 * 1000);
        getCreateCoupon();

        Log.d(TAG, "getData" + utc1 + " - " + utc2);
    }
}
