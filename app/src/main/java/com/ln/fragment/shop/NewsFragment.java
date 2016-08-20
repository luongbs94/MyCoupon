package com.ln.fragment.shop;

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
import com.ln.adapter.NewsShopAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.model.Company;
import com.ln.model.NewsOfCompany;
import com.ln.model.OptionNews;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nha on 4/6/16.
 * show news in shop
 */
public class NewsFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private LoveCouponAPI mApiServices;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView mRecNews;
    private Company mCompany;

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        String strCompany = MainApplication
                .getPreferences()
                .getString(MainApplication.COMPANY_SHOP, "");
        mCompany = new Gson().fromJson(strCompany, Company.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_news, container, false);

        initViews(mView);
        setHasOptionsMenu(false);
        setNewsOfCompany();
        return mView;
    }

    private void initViews(View mView) {
        mRecNews = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecNews.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewsByCompanyId();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setRefreshing(false);
    }

    public void setNewsOfCompany() {

        List<NewsOfCompany> news = new ArrayList<>();
        if (DatabaseManager.getListNewsOfCompany() != null) {
            news.addAll(DatabaseManager.getListNewsOfCompany());
        }

        Log.d(TAG, "======================");

        for (NewsOfCompany item : news) {
            Log.d(TAG, item.getCreated_date() + "");
        }


        List<OptionNews> listLike = new ArrayList<>();
        listLike.addAll(DatabaseManager.getListOptionNews(MainApplication.SHOP));

        for (OptionNews likeNews : listLike) {
            for (NewsOfCompany item : news) {
                if (item.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdUser().equals(mCompany.getCompany_id())) {
                    item.setLike(true);
                }
            }
        }
        NewsShopAdapter adapter = new NewsShopAdapter(getActivity(), news, this);
        mRecNews.setAdapter(adapter);
        Log.d(TAG, "Size : " + news.size());
    }

    private void getNewsByCompanyId() {

        if (mCompany != null) {

            Call<List<NewsOfCompany>> call = mApiServices.getNewsByCompanyId(mCompany.getCompany_id());
            call.enqueue(new Callback<List<NewsOfCompany>>() {
                @Override
                public void onResponse(Call<List<NewsOfCompany>> call, Response<List<NewsOfCompany>> response) {


                    if (response.body() != null) {
                        DatabaseManager.addListNewsOfCompany(response.body());
                        setNewsOfCompany();
                        Log.d(TAG, "getNewsByCompanyId " + response.body().size());
                        swipeContainer.setRefreshing(false);

                    } else {
                        Log.d(TAG, "getNewsByCompanyId " + "null");
                    }
                }

                @Override
                public void onFailure(Call<List<NewsOfCompany>> call, Throwable t) {
                    Log.d(TAG, "getNewsByCompanyId " + "onFailure " + t.toString());
                    swipeContainer.setRefreshing(false);
                }
            });
        }
    }
}
