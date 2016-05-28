package com.ln.fragment.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ln.adapter.CreateCouponAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.Coupon;
import com.ln.mycoupon.R;

import java.util.ArrayList;

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
    private ArrayList<Coupon> mListCoupons = new ArrayList<>();
    private String TAG = getClass().getSimpleName();

    public UseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        getUseCoupon();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_create, container, false);
        initViews();
        return mView;
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getUseCoupon() {

        Company company = SaveData.company;
        Call<ArrayList<Coupon>> listCoupon = mApiServices.getUsedCoupon(company.getCompany_id() + "", utc1, utc2);
        listCoupon.enqueue(new Callback<ArrayList<Coupon>>() {
            @Override
            public void onResponse(Call<ArrayList<Coupon>> call, Response<ArrayList<Coupon>> response) {
                mListCoupons = response.body();
                mCouponAdapter = new CreateCouponAdapter(getActivity(), mListCoupons);
                mRecyclerView.setAdapter(mCouponAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Coupon>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUseCoupon();
    }
}
