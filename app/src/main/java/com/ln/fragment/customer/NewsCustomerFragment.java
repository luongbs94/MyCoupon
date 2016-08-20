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

import com.google.gson.Gson;
import com.ln.adapter.NewsCustomerAdapter;
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.databases.DatabaseManager;
import com.ln.model.AccountOfUser;
import com.ln.model.NewsOfCustomer;
import com.ln.model.OptionNews;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsCustomerFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private LoveCouponAPI apiService;

    private RecyclerView mRecyclerNews;
    private SwipeRefreshLayout mSwipeContainer;
    private AccountOfUser account;

    private int mType;
    private int mTypeNews;

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
        account = new Gson()
                .fromJson(MainApplication
                        .getPreferences()
                        .getString(MainApplication.ACCOUNT_CUSTOMER, ""), AccountOfUser.class);

        mTypeNews = getArguments().getInt(MainApplication.ID_NEWS);
        Log.d(TAG, " onCreate: " + mTypeNews);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

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

        List<NewsOfCustomer> news = new ArrayList<>();
        news.addAll(DatabaseManager.getListNewsOfCustomer(mTypeNews));

        List<OptionNews> listLike = new ArrayList<>();
        listLike.addAll(DatabaseManager.getListOptionNews(MainApplication.NEW_LIKE, MainApplication.CUSTOMER));

        List<OptionNews> listDeleteNews = new ArrayList<>();
        listDeleteNews.addAll(DatabaseManager.getListOptionNews(MainApplication.NEW_DELETE, MainApplication.CUSTOMER));

        for (OptionNews item : listDeleteNews) {
            Log.d(TAG, "delete  " + item.getIdNews());
        }

        for (OptionNews likeNews : listLike) {

            for (NewsOfCustomer item : news) {
                if (item.getMessage_id().equals(likeNews.getIdNews())
                        && likeNews.getIdUser().equals(account.getId())) {
                    item.setLike(true);
                }
            }
        }

        //set delete news
        for (OptionNews deleteNews : listDeleteNews) {
            for (NewsOfCustomer item : news) {
                if (item.getMessage_id().equals(deleteNews.getIdNews())) {
                    item.setDelete(true);
                }
            }

            int size = news.size() - 1;

            for (int i = size; i >= 0; i--) {
                if (news.get(i).isDelete()) {
                    news.remove(i);
                }
            }
        }

        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(), news, this);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);

    }

    private void getNewsOfCustomer() {

        if (mTypeNews == MainApplication.TYPE_NEWS) {
            Call<List<NewsOfCustomer>> newsCustomer = apiService.getNewsByUserId(account.getId());

            newsCustomer.enqueue(new Callback<List<NewsOfCustomer>>() {
                @Override
                public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {
                    if (response.body() != null) {

                        String account = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
                        String user = new Gson().fromJson(account, AccountOfUser.class).getId();

                        DatabaseManager.addListNewsOfCustomer(response.body(), MainApplication.TYPE_NEWS, user);

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
        } else {

            String city = MainApplication
                    .getPreferences()
                    .getString(MainApplication.CITY_OF_USER, "");
            Call<List<NewsOfCustomer>> newsMore = apiService.getNewsMoreByUserId(account.getId(), city);
            newsMore.enqueue(new Callback<List<NewsOfCustomer>>() {
                @Override
                public void onResponse(Call<List<NewsOfCustomer>> call, Response<List<NewsOfCustomer>> response) {
                    if (response.body() != null) {


                        String account = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
                        String user = new Gson().fromJson(account, AccountOfUser.class).getId();

                        DatabaseManager.addListNewsOfCustomer(response.body(), MainApplication.TYPE_NEWS_MORE, user);
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

        List<OptionNews> listLike = new ArrayList<>();
        listLike.addAll(DatabaseManager.getListOptionNews(MainApplication.NEW_LIKE, MainApplication.CUSTOMER));

        List<OptionNews> listDeleteNews = new ArrayList<>();
        listDeleteNews.addAll(DatabaseManager.getListOptionNews(MainApplication.NEW_DELETE, MainApplication.CUSTOMER));

        List<NewsOfCustomer> listNews = new ArrayList<>();
        listNews.addAll(DatabaseManager.getListNewsOfCustomer(mTypeNews));

        for (OptionNews likeNews : listLike) {
            for (NewsOfCustomer item : listNews) {
                if (likeNews.getIdNews().equals(item.getMessage_id())) {
                    item.setLike(true);
                }
            }
        }
        //set delete news
        for (OptionNews deleteNews : listDeleteNews) {
            for (NewsOfCustomer item : listNews) {
                if (item.getMessage_id().equals(deleteNews.getIdNews())) {
                    item.setDelete(true);
                }
            }

            int size = listNews.size() - 1;

            for (int i = size; i >= 0; i--) {
                if (listNews.get(i).isDelete()) {
                    listNews.remove(i);
                }
            }
        }
        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(), listNews, this);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);
    }
}
