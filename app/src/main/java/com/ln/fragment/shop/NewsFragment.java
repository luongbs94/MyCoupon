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
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.NewsOfCompany;
import com.ln.model.NewsOfLike;
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

    private LoveCouponAPI mApiServices;

    private View mView;
    private RecyclerView mRecNews;
    private String TAG = getClass().getSimpleName();
    private SwipeRefreshLayout swipeContainer;
    private List<NewsOfLike> mListNewsOfLike = new ArrayList<>();

    private RealmController mRealm;
    private NewsShopAdapter adapter;

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        mRealm = MainApplication.mRealmController;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_news, container, false);

        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewsByCompanyId();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        initViews();
        setNewsOfCompany();
        Log.d(TAG, "onCreate");
        return mView;
    }

    private void initViews() {
        mRecNews = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecNews.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setNewsOfCompany() {

        List<NewsOfCompany> mListNews = mRealm.getListNewsOfCompany();
        List<NewsOfLike> newsOfLikeList = new ArrayList<>();


        for (NewsOfCompany message : mListNews) {
            newsOfLikeList.add(new NewsOfLike(message, false));
        }

        // set like news
        List<ShopLikeNews> listLike = mRealm.getListShopLikeNews();

        for (ShopLikeNews likeNews : listLike) {
            for (NewsOfLike newsOfLike : newsOfLikeList) {
                if (newsOfLike.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdCompany().equals(SaveData.company.getCompany_id())) {
                    newsOfLike.setLike(true);
                }
            }
        }
        mListNewsOfLike.clear();
        mListNewsOfLike.addAll(newsOfLikeList);

        Log.d(TAG, "Size : " + mListNewsOfLike.size());
        adapter = new NewsShopAdapter(getActivity(), mListNewsOfLike, NewsFragment.this);
        mRecNews.setAdapter(adapter);

    }

    private void getNewsByCompanyId() {

        String idCompany;
        if (SaveData.company == null) {
            idCompany = MainApplication.sIdCompany;
        } else {
            idCompany = SaveData.company.getCompany_id();
        }

        Call<List<NewsOfCompany>> call = mApiServices.getNewsByCompanyId(idCompany);
        call.enqueue(new Callback<List<NewsOfCompany>>() {
            @Override
            public void onResponse(Call<List<NewsOfCompany>> call, Response<List<NewsOfCompany>> response) {

                List<NewsOfCompany> mListNews = response.body();

                mRealm.deleteAllNewsOfCompany();
                mRealm.addListNewsOfCompany(mListNews);

                setNewsOfCompany();
//                mListNewsOfLike = new ArrayList<>();
//
//                for (NewsOfCompany message : mListNews) {
//                    mListNewsOfLike.add(new NewsOfLike(message, false));
//                }
//
//                // set like news
//                List<ShopLikeNews> listLike = mRealm.getListShopLikeNews();
//
//                for (ShopLikeNews likeNews : listLike) {
//                    for (NewsOfLike newsOfLike : mListNewsOfLike) {
//                        if (newsOfLike.getMessage_id().equals(likeNews.getIdNews())
//                                && likeNews.getIdCompany().equals(SaveData.company.getCompany_id())) {
//                            newsOfLike.setLike(true);
//                        }
//                    }
//                }
//
//                Log.d(TAG, mListNews.size() + "");
//                adapter = new NewsShopAdapter(getActivity(), mListNewsOfLike, NewsFragment.this);
//                mRecNews.setAdapter(adapter);
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<List<NewsOfCompany>> call, Throwable t) {
                Log.d(TAG, "Error " + t.toString());
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
