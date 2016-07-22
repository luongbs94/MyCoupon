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
import com.ln.adapter.CompanyAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.ItemClickSupport;
import com.ln.app.MainApplication;
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

    private LoveCouponAPI mApiServices;
    private RealmController mRealmController;

    private String TAG = getClass().getSimpleName();

    private View mView;
    private RecyclerView mRecCoupon;

    private SwipeRefreshLayout swipeContainer;

    private List<CompanyOfCustomer> mListCompanyCustomer = new ArrayList<>();

    public CouponFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        mRealmController = MainApplication.mRealmController;
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
        setHasOptionsMenu(true);
        return mView;
    }


    private void initViews() {

        mRecCoupon = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecCoupon.setLayoutManager(new LinearLayoutManager(getActivity()));

        setListCompanyCustomer();

        ItemClickSupport.addTo(mRecCoupon).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(getActivity(), CouponCompanyOfClientActivity.class);

                Bundle bundle = new Bundle();
                CompanyOfCustomer companyOfCustomer = mListCompanyCustomer.get(position);
                String idCompany = companyOfCustomer.getCompany_id();
                bundle.putString(MainApplication.ID_COMPANY, idCompany);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    private void setListCompanyCustomer() {


        mListCompanyCustomer = mRealmController.getListCompanyCustomer();
        CompanyAdapter adapter = new CompanyAdapter(getActivity(), mListCompanyCustomer);
        mRecCoupon.setAdapter(adapter);
//        mRecCoupon.setAdapter(adapter);
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

//                        mRealmController.deleteListCompanyCustomer();
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
