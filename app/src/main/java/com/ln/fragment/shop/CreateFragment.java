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
import com.ln.model.Company;
import com.ln.model.Coupon;
import com.ln.app.MainApplication;
import com.ln.mycoupon.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateFragment extends Fragment {

    private LoveCouponAPI mApiServices;

    private View mView;
    private RecyclerView mRecyclerCreate;
    private ArrayList<Coupon> mListCoupon = new ArrayList<>();
    private CreateCouponAdapter mCouponAdapter;

    private String TAG = getClass().getSimpleName();
    private String utc1 = "Mon, 6 Mar 2016 17:00:00 GMT";
    private String utc2 = "Mon, 17 Oct 2016 17:00:00 GMT";
    private static boolean isInitRecyclerView;


    public CreateFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        getCreateCoupon();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_create, container, false);
        initViews();
        getCreateCoupon();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getCreateCoupon();
    }

    private void initViews() {
        mRecyclerCreate = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerCreate.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getCreateCoupon() {

        Company company = SaveData.company;
        mListCoupon.clear();
        Call<ArrayList<Coupon>> listCoupon = mApiServices.getCreatedCoupon(company.getCompany_id() + "", utc1, utc2);
        listCoupon.enqueue(new Callback<ArrayList<Coupon>>() {
            @Override
            public void onResponse(Call<ArrayList<Coupon>> call, Response<ArrayList<Coupon>> response) {
                mListCoupon = response.body();

                mCouponAdapter = new CreateCouponAdapter(getActivity(), mListCoupon);
                mRecyclerCreate.setAdapter(mCouponAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Coupon>> call, Throwable t) {

            }
        });
    }
}
