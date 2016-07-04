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
import com.ln.model.NewsOfCustomer;
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
    private SwipeRefreshLayout mSwipeContainer;

    private RealmController mRealmController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = MainApplication.getAPI();
        mRealmController = RealmController.with(this);
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
                getNewsOfCustomer();
                mRecyclerNews.setClickable(true);
            }
        });
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeContainer.setRefreshing(true);

        setListMessages();
        setHasOptionsMenu(true);
        return view;
    }


    private void setListMessages() {

        List<NewsOfCustomer> mListNews = mRealmController.getListNewsOfCustomer();

        // set like news
        List<LikeNews> listLike = mRealmController.getListLikeNews();
        // list delete
        List<DeleteNews> listDeleteNews = mRealmController.getListDeleteNews();
        List<Message> listMessage = new ArrayList<>();
        for (NewsOfCustomer news : mListNews) {
            listMessage.add(new Message(news));
        }

        for (LikeNews likeNews : listLike) {

            for (Message newsOfLike : listMessage) {
                if (newsOfLike.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdUser().equals(MainApplication.sDetailUser.getId())) {
                    newsOfLike.setLike(true);
                }
            }
        }

        //set delete news

        for (DeleteNews deleteNews : listDeleteNews) {
            for (Message newsOfLike : listMessage) {
                if (newsOfLike.getMessage_id().equals(deleteNews.getIdNews())
                        && deleteNews.getIdNews().equals(MainApplication.sDetailUser.getId())) {
                    newsOfLike.setDelete(true);
                }
            }

            int size = mListNews.size() - 1;

            for (int i = size; i >= 0; i--) {
                if (mListNews.get(i).getMessage_id().equals(deleteNews.getIdNews())) {
                    mListNews.remove(i);
                }
            }
        }

        Log.d(TAG, "Size : " + mListNews.size());
        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(), listMessage, this);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);


    }

    public void getNewsOfCustomer() {

        Call<List<NewsOfCustomer>> newsCustomer = apiService.getNewsByUserId(MainApplication.sDetailUser.getId());

        newsCustomer.enqueue(new Callback<List<NewsOfCustomer>>() {
            @Override
            public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {
                if (response.body() != null) {
                    mRealmController.deleteAllNewsOfCustomer();
                    mRealmController.addListNewsOfCustomer(response.body());
                    setListMessages();
                    mSwipeContainer.setRefreshing(false);
                    Log.d(TAG, "getNewsOfCustomer " + response.body().size());
                } else {
                    Log.d(TAG, "getNewsOfCustomer null");
                }
            }

            @Override
            public void onFailure(Call<List<NewsOfCustomer>> call, Throwable t) {
                Log.d(TAG, "getNewsOfCustomer onFailure");
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
                getNewsOfCustomer();
                return true;

            case R.id.menu_like_news:
                getSnackBar(getString(R.string.like_news));
                likeNews();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void likeNews() {

        List<NewsOfCustomer> mListNewsOfCustomer = mRealmController.getListNewsOfCustomer();
        List<LikeNews> listLike = mRealmController.getListLikeNews();
        List<Message> listMessage = new ArrayList<>();

        for (NewsOfCustomer newsOfCustomer : mListNewsOfCustomer) {
            listMessage.add(new Message(newsOfCustomer));
        }

        for (LikeNews likeNews : listLike) {
            for (Message message : listMessage) {
                if (message.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdUser().equals(MainApplication.sDetailUser.getId())) {
                    message.setLike(true);
                }
            }
        }

        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(), listMessage, this);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);
    }

    private void getSnackBar(String s) {
        Snackbar.make(mRecyclerNews, s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
