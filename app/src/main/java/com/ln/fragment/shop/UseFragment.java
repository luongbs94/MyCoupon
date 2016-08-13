package com.ln.fragment.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class UseFragment extends Fragment {
    private LoveCouponAPI mApiServices;

    private String utc1 = "Mon, 6 Mar 2016 17:00:00 GMT";
    private String utc2 = "Mon, 17 Oct 2016 17:00:00 GMT";

    private View mView;
    private RecyclerView mRecyclerView;

    private CreateCouponAdapter mCouponAdapter;
    private List<Coupon> mListCoupons = new ArrayList<>();
    private String TAG = getClass().getSimpleName();
    private SwipeRefreshLayout swipeContainer;
    private Calendar calendar;
    private TextView textView;


    public UseFragment() {
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

        utc1 = date.getTime() + "";
        utc2 = (date.getTime() + 24 * 3600 * 1000) + "";

        getUseCoupon();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_create, container, false);
        textView = (TextView) mView.findViewById(R.id.text_no_data);


        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUseCoupon();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        initViews();

        setHasOptionsMenu(false);

        return mView;
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getUseCoupon() {
        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);

        mListCoupons.clear();

        Call<List<Coupon>> listCoupon = mApiServices.getUsedCoupon(company.getWeb_token(), company.getCompany_id() + "", utc1, utc2);
        listCoupon.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                mListCoupons = response.body();
                mCouponAdapter = new CreateCouponAdapter(getActivity(), mListCoupons);
                mRecyclerView.setAdapter(mCouponAdapter);
                swipeContainer.setRefreshing(false);

                if (mListCoupons.size() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void getData(long time) {
        utc1 = time + "";
        utc2 = (time + 24 * 3600 * 1000) + "";
        getUseCoupon();
    }
}
