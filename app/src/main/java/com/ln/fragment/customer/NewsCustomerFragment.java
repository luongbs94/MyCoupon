package com.ln.fragment.customer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ln.adapter.NewsCustomerAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.Message;
import com.ln.model.NewsOfLike;
import com.ln.mycoupon.R;
import com.ln.realm.DeleteNews;
import com.ln.realm.LikeNews;
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
public class NewsCustomerFragment extends Fragment {

    private LoveCouponAPI apiService;
    private String TAG = getClass().getSimpleName();

    private RecyclerView mRecyclerNews;
    private List<NewsOfLike> mListNewsOfLike = new ArrayList<>();
    private SwipeRefreshLayout mSwipeContainer;

    private RealmController mRealm;
    private NewsCustomerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = MainApplication.getAPI();
        mRealm = RealmController.with(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer_news, container, false);

        mRecyclerNews = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerNews.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerNews.setHasFixedSize(true);

        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerNews.setClickable(false);
                getMessage();
                mRecyclerNews.setClickable(true);
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeContainer.setRefreshing(true);

//        setListMessages();
        getMessage();
        setHasOptionsMenu(true);
        return view;
    }


    private void setListMessages() {

        List<Message> mListNews = mRealm.getListMessages();
        List<NewsOfLike> newsOfLikeList = new ArrayList<>();

        for (Message message : mListNews) {
            newsOfLikeList.add(new NewsOfLike(message, false));
        }

        // set like news
        List<LikeNews> listLike = mRealm.getListLikeNews();
        // list delete
        List<DeleteNews> listDeleteNews = mRealm.getListDeleteNews();


        for (LikeNews likeNews : listLike) {

            for (NewsOfLike newsOfLike : newsOfLikeList) {
                if (newsOfLike.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdUser().equals(MainApplication.sDetailUser.getId())) {
                    newsOfLike.setLike(true);
                }
            }
        }

        //set delete news

        for (DeleteNews deleteNews : listDeleteNews) {
            for (NewsOfLike newsOfLike : newsOfLikeList) {
                if (newsOfLike.getMessage_id().equals(deleteNews.getIdNews())
                        && deleteNews.getIdNews().equals(MainApplication.sDetailUser.getId())) {
                    newsOfLike.setDelete(true);
                }
            }

            int size = newsOfLikeList.size() - 1;

            for (int i = size; i >= 0; i--) {
                if (newsOfLikeList.get(i).getMessage_id().equals(deleteNews.getIdNews())) {
                    newsOfLikeList.remove(i);
                }
            }
        }


        mListNewsOfLike.clear();
        mListNewsOfLike.addAll(newsOfLikeList);

        Log.d(TAG, "Size : " + mListNewsOfLike.size());
        adapter = new NewsCustomerAdapter(getActivity(),
                mListNewsOfLike, NewsCustomerFragment.this);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);


    }

    public void getMessage() {

        final List<NewsOfLike> newsOfLikeList = new ArrayList<>();
        Call<List<Message>> call = apiService.getNewsByUserId(MainApplication.sDetailUser.getId());
        call.enqueue(new Callback<List<Message>>() {

            @Override
            public void onResponse(Call<List<Message>> arg0, Response<List<Message>> arg1) {

                List<Message> mListNews = arg1.body();
                for (Message message : mListNews) {
                    newsOfLikeList.add(new NewsOfLike(message, false));
                }

                // set like news
                List<LikeNews> listLike = mRealm.getListLikeNews();
                // list delete
                List<DeleteNews> listDeleteNews = mRealm.getListDeleteNews();


                for (LikeNews likeNews : listLike) {

                    for (NewsOfLike newsOfLike : newsOfLikeList) {
                        if (newsOfLike.getMessage_id().equals(likeNews.getIdNews())
                                && likeNews.getIdUser().equals(MainApplication.sDetailUser.getId())) {
                            newsOfLike.setLike(true);
                        }
                    }
                }

                //set delete news

                for (DeleteNews deleteNews : listDeleteNews) {
                    for (NewsOfLike newsOfLike : newsOfLikeList) {
                        if (newsOfLike.getMessage_id().equals(deleteNews.getIdNews())
                                && deleteNews.getIdNews().equals(MainApplication.sDetailUser.getId())) {
                            newsOfLike.setDelete(true);
                        }
                    }

                    int size = newsOfLikeList.size() - 1;

                    for (int i = size; i >= 0; i--) {
                        if (newsOfLikeList.get(i).getMessage_id().equals(deleteNews.getIdNews())) {
                            newsOfLikeList.remove(i);
                        }
                    }
                }


                mListNewsOfLike.clear();
                mListNewsOfLike.addAll(newsOfLikeList);

                Log.d(TAG, "Size : " + mListNewsOfLike.size());
                NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(),
                        mListNewsOfLike, NewsCustomerFragment.this);
                mRecyclerNews.setAdapter(adapter);
                mSwipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Message>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
                mSwipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_news_customer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_all_news:
                getSnackBar(getString(R.string.all_news));
                getMessage();
                return true;
            case R.id.menu_near_news:
                getSnackBar(getString(R.string.near_news));
                return true;
            case R.id.menu_like_news:
                getSnackBar(getString(R.string.like_news));
                likeNews();
                return true;
            case R.id.menu_delete_news:
                getSnackBar(getString(R.string.news_delete));
                deleteNews();
                return true;
            default:
                getMessage();
                return super.onOptionsItemSelected(item);
        }
    }

    private void likeNews() {


        List<LikeNews> listLike = mRealm.getListLikeNews();
        List<NewsOfLike> listNews = new ArrayList<>();

        for (LikeNews likeNews : listLike) {

            for (NewsOfLike newsOfLike : mListNewsOfLike) {
                if (newsOfLike.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdUser().equals(MainApplication.sDetailUser.getId())) {
                    newsOfLike.setLike(true);
                    listNews.add(newsOfLike);
                }
            }
        }

        mListNewsOfLike.clear();
        mListNewsOfLike.addAll(listNews);

        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(),
                mListNewsOfLike, NewsCustomerFragment.this);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);
    }

    private void deleteNews() {


        List<LikeNews> listLike = mRealm.getListLikeNews();
        List<NewsOfLike> listNews = new ArrayList<>();

        for (LikeNews likeNews : listLike) {

            for (NewsOfLike newsOfLike : mListNewsOfLike) {
                if (newsOfLike.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdUser().equals(MainApplication.sDetailUser.getId())) {
                    newsOfLike.setLike(true);
                    listNews.add(newsOfLike);
                }
            }
        }

        mListNewsOfLike.clear();

        List<DeleteNews> listDeleteNews = mRealm.getListDeleteNews();

        for (DeleteNews deleteNews : listDeleteNews) {

            for (NewsOfLike newsOfLike : listNews) {
                if (newsOfLike.getMessage_id().equals(deleteNews.getIdNews())
                        && deleteNews.getIdUser().equals(MainApplication.sDetailUser.getId())) {
                    mListNewsOfLike.add(newsOfLike);
                }
            }
        }

        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(),
                mListNewsOfLike, NewsCustomerFragment.this);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);
    }

    private void getSnackBar(String s) {
        Snackbar.make(mRecyclerNews, s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
