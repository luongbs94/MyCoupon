package com.ln.fragment.customer;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.ln.adapter.NewsCustomerAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.AccountOflUser;
import com.ln.model.Message;
import com.ln.model.NewsOfCustomer;
import com.ln.model.NewsOfMore;
import com.ln.mycoupon.R;
import com.ln.realm.DeleteNews;
import com.ln.realm.LikeNews;
import com.ln.realm.RealmController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/8/16.
 * <></>
 */
public class NewsCustomerFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private LoveCouponAPI apiService;
    private RealmController mRealmController;

    private RecyclerView mRecyclerNews;
    private SwipeRefreshLayout mSwipeContainer;
    private AccountOflUser account;

    private int mType;
    private int mTypeNews = MainApplication.TYPE_NEWS;

    public static NewsCustomerFragment getInstances(int typeNews) {
        NewsCustomerFragment instances = new NewsCustomerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MainApplication.ID_NEWS, typeNews);
        instances.setArguments(bundle);
        return instances;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = MainApplication.getAPI();
        mRealmController = RealmController.with(this);
        account = new Gson()
                .fromJson(MainApplication
                        .getPreferences()
                        .getString(MainApplication.ACCOUNT_CUSTOMER, ""), AccountOflUser.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer_news, container, false);

        mTypeNews = getArguments().getInt(MainApplication.ID_NEWS);

        mRecyclerNews = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerNews.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerNews.setHasFixedSize(true);

        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerNews.setClickable(false);
                if (mType == 0) {
                    getNewsOfCustomer();

                } else if (mType == 1) {
                    getNewsLike();
                }
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

        List<Message> listMessage = new ArrayList<>();
        Log.d(TAG, mTypeNews + "");
        if (mTypeNews == MainApplication.TYPE_NEWS) {
            List<NewsOfCustomer> listNews = mRealmController.getListNewsOfCustomer();
            for (NewsOfCustomer news : listNews) {
                listMessage.add(new Message(news));
            }
        } else if (mTypeNews == MainApplication.TYPE_NEWS_MORE) {
            List<NewsOfMore> mListNews = mRealmController.getListNewsOfMore();
            for (NewsOfMore news : mListNews) {
                listMessage.add(new Message(news));
                Log.d(TAG, "setListMessages " + news.getLogo_link());
                Log.d(TAG, "setListMessages " + news.getMessage_id());
                Log.d(TAG, "setListMessages " + " ========================= ");
            }
        }


        List<LikeNews> listLike = mRealmController.getListLikeNews();
        List<DeleteNews> listDeleteNews = mRealmController.getListDeleteNews();


        for (LikeNews likeNews : listLike) {

            for (Message item : listMessage) {
                if (item.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdUser().equals(account.getId())) {
                    item.setLike(true);
                }
            }
        }

        //set delete news
        for (DeleteNews deleteNews : listDeleteNews) {
            for (Message item : listMessage) {
                if (item.getMessage_id().equals(deleteNews.getIdNews())) {
                    item.setDelete(true);
                }
            }

            int size = listMessage.size() - 1;

            for (int i = size; i >= 0; i--) {
                if (listMessage.get(i).isDelete()) {
                    listMessage.remove(i);
                }
            }
        }

        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(), listMessage, this);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);

    }

    private void getNewsOfCustomer() {


        Call<List<NewsOfCustomer>> newsCustomer = apiService.getNewsByUserId(account.getId());

        newsCustomer.enqueue(new Callback<List<NewsOfCustomer>>() {
            @Override
            public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {
                if (response.body() != null) {
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
                mType = 0;
                getNewsOfCustomer();
                return true;
            case R.id.menu_like_news:
                mType = 1;
                getNewsLike();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getNewsLike() {

        List<LikeNews> listLikeNews = mRealmController.getListLikeNews();
        List<NewsOfCustomer> listNews = mRealmController.getListNewsOfCustomer();
        List<NewsOfMore> listNewsOfMores = mRealmController.getListNewsOfMore();
        List<Message> listMessage = new ArrayList<>();
        List<DeleteNews> listDeleteNews = mRealmController.getListDeleteNews();

        for (LikeNews likeNews : listLikeNews) {
            for (NewsOfCustomer news : listNews) {
                if (likeNews.getIdNews().equals(news.getMessage_id())) {
                    listMessage.add(new Message(news, true));
                }
            }

            for (NewsOfMore newsOfMore : listNewsOfMores) {
                if (likeNews.getIdNews().equals(newsOfMore.getMessage_id())) {
                    listMessage.add(new Message(newsOfMore, true));
                }
            }
        }
        //set delete news
        for (DeleteNews deleteNews : listDeleteNews) {
            for (Message item : listMessage) {
                if (item.getMessage_id().equals(deleteNews.getIdNews())) {
                    item.setDelete(true);
                }
            }

            int size = listMessage.size() - 1;

            for (int i = size; i >= 0; i--) {
                if (listMessage.get(i).isDelete()) {
                    listMessage.remove(i);
                }
            }
        }
        Collections.sort(listMessage);
        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(), listMessage, this);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);
    }

    private void getSnackBar(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}
