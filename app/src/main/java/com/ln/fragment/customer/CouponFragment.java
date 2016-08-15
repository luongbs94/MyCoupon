package com.ln.fragment.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.ln.adapter.CouponShopAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.interfaces.RecyclerViewListener;
import com.ln.model.AccountOflUser;
import com.ln.model.CompanyOfCustomer;
import com.ln.mycoupon.R;
import com.ln.mycoupon.customer.CouponCompanyOfClientActivity;
import com.ln.realm.RealmController;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/8/16.
 * <></>
 */
public class CouponFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private LoveCouponAPI mApiServices;
    private RealmController mRealmController;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeContainer;
    private List<CompanyOfCustomer> mListCompanyCustomer = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        mRealmController = MainApplication.mRealmController;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_coupon, container, false);
        initViews(mView);
        setHasOptionsMenu(true);
        return mView;
    }

    private void initViews(View mView) {

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setListCompanyCustomer();

        mRecyclerView.addOnItemTouchListener(new RecyclerViewListener(getActivity(), new RecyclerViewListener.OnClickRecyclerView() {
            @Override
            public void onClick(View view, int position) {

                Intent intent = new Intent(getActivity(), CouponCompanyOfClientActivity.class);
                CompanyOfCustomer item = mListCompanyCustomer.get(position);
                String idCompany = item.getCompany_id();
                Bundle bundle = new Bundle();
                bundle.putString(MainApplication.ID_COMPANY, idCompany);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }));

//        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//            @Override
//            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                Intent intent = new Intent(getActivity(), CouponCompanyOfClientActivity.class);
//
//                Bundle bundle = new Bundle();
//                CompanyOfCustomer companyOfCustomer = mListCompanyCustomer.get(position);
//                String idCompany = companyOfCustomer.getCompany_id();
//                bundle.putString(MainApplication.ID_COMPANY, idCompany);
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });

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
    }


    public void setListCompanyCustomer() {

        mListCompanyCustomer = new ArrayList<>();
        mListCompanyCustomer.addAll(mRealmController.getListCompanyCustomer());
        CouponShopAdapter adapter = new CouponShopAdapter(getActivity(), mListCompanyCustomer);
        mRecyclerView.setAdapter(adapter);
        Log.d(TAG, "setListCompanyCustomer " + mListCompanyCustomer.size());
    }

    private void getCompanyByUserId() {

        AccountOflUser account = new Gson().fromJson(
                MainApplication.getPreferences()
                        .getString(MainApplication
                                .ACCOUNT_CUSTOMER, ""),
                AccountOflUser.class);

        if (account != null) {
            Call<List<CompanyOfCustomer>> call3 = mApiServices.getCompaniesByUserId(account.getId());
            call3.enqueue(new Callback<List<CompanyOfCustomer>>() {
                @Override
                public void onResponse(Call<List<CompanyOfCustomer>> call, Response<List<CompanyOfCustomer>> response) {
                    if (response.body() != null) {

                        mRealmController.addListCompanyCustomer(response.body());
                        setListCompanyCustomer();
                        swipeContainer.setRefreshing(false);
                        Log.d(TAG, "CompanyCustomer " + response.body().size());
                    } else {
                        Log.d(TAG, "CompanyCustomer " + "null");
                    }
                }

                @Override
                public void onFailure(Call<List<CompanyOfCustomer>> call, Throwable t) {
                    Log.d(TAG, "CompanyCustomer " + t.toString());
                    swipeContainer.setRefreshing(false);
                }
            });
        }
    }
}
