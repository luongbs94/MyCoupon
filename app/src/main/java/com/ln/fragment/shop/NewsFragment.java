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

import com.ln.adapter.NewsShopAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfCompanyLike;
import com.ln.mycoupon.R;
import com.ln.realm.RealmController;
import com.ln.realm.ShopLikeNews;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/6/16.
 * show news in shop
 */
public class NewsFragment extends Fragment {

    private String TAG = getClass().getSimpleName();

    private LoveCouponAPI mApiServices;
    private RealmController mRealmController;

    private SwipeRefreshLayout swipeContainer;


    private RecyclerView mRecNews;
    private Company mCompany;

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        mRealmController = MainApplication.mRealmController;
        mCompany = mRealmController.getAccountShop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_news, container, false);

        initViews(mView);
        setNewsOfCompany();
        setHasOptionsMenu(false);
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

    private void setNewsOfCompany() {

        List<NewsOfCompany> mListNewsOfCompany = mRealmController.getListNewsOfCompany();
        List<NewsOfCompanyLike> listNews = new ArrayList<>();

        for (NewsOfCompany newsOfCompany : mListNewsOfCompany) {
            listNews.add(new NewsOfCompanyLike(newsOfCompany));
        }

        List<ShopLikeNews> listLike = mRealmController.getListShopLikeNews();

        for (ShopLikeNews likeNews : listLike) {
            for (NewsOfCompanyLike news : listNews) {
                if (news.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdCompany().equals(mCompany.getCompany_id())) {
                    news.setLike(true);
                }
            }
        }
        NewsShopAdapter adapter = new NewsShopAdapter(getActivity(), listNews, this);
        mRecNews.setAdapter(adapter);
        Log.d(TAG, "Size : " + mListNewsOfCompany.size());
    }

    private void getNewsByCompanyId() {


        if (mCompany != null) {

            Call<List<NewsOfCompany>> call = mApiServices.getNewsByCompanyId(mCompany.getCompany_id());
            call.enqueue(new Callback<List<NewsOfCompany>>() {
                @Override
                public void onResponse(Call<List<NewsOfCompany>> call, Response<List<NewsOfCompany>> response) {


                    if (response.body() != null) {

                        mRealmController.deleteListNewsOfCompany();
                        mRealmController.addListNewsOfCompany(response.body());
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
