package com.ln.fragment.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.ln.adapter.CompanyAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.ItemClickSupport;
import com.ln.app.MainApplication;
import com.ln.model.Company1;
import com.ln.model.CouponTemplate;
import com.ln.mycoupon.R;
import com.ln.mycoupon.customer.CouponCompanyOfClientActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/8/16.
 *
 */
public class CouponFragment extends Fragment {
    private LoveCouponAPI mApiServices;

    private View mView;
    private LinearLayout mLnLayout;
    private RecyclerView mRecCoupon;
    private List<CouponTemplate> mListCoupon = new ArrayList<>();
    private String TAG = getClass().getSimpleName();

    private SwipeRefreshLayout swipeContainer;


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
        mView = inflater.inflate(R.layout.fragment_company_of_client, container, false);

        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCompanyByUserId();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        initViews();

        return mView;
    }

    private void initViews() {
        mRecCoupon = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecCoupon.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLnLayout = (LinearLayout) mView.findViewById(R.id.ln_fragment_coupon);


        CompanyAdapter adapter = new CompanyAdapter(getActivity(), SaveData.listCompany);
        mRecCoupon.setAdapter(adapter);

        ItemClickSupport.addTo(mRecCoupon).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(getActivity(), CouponCompanyOfClientActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);


            }
        });
    }


    private void getSnackBar(String text) {
        Snackbar.make(mLnLayout, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void getCompanyByUserId() {

        Call<List<Company1>> call3 = MainApplication.apiService.getCompaniesByUserId(SaveData.USER_ID);
        call3.enqueue(new Callback<List<Company1>>() {

            @Override
            public void onResponse(Call<List<Company1>> arg0,
                                   Response<List<Company1>> arg1) {
                List<Company1> templates = arg1.body();
//                System.out.println(templates.size());
                SaveData.listCompany = templates;

                swipeContainer.setRefreshing(false);
                CompanyAdapter adapter = new CompanyAdapter(getActivity(), SaveData.listCompany);
                mRecCoupon.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Company1>> arg0, Throwable arg1) {
                swipeContainer.setRefreshing(false);

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        Gson gson = new Gson();

        String data = gson.toJson(SaveData.listCompany);
        MainApplication.editor.putString(MainApplication.CLIENT_DATA, data);
        MainApplication.editor.commit();
    }
}
