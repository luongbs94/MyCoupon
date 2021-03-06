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
import com.ln.model.NewMore;
import com.ln.model.NewsOfCustomer;
import com.ln.model.OptionNews;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.Collections;
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
        news.addAll(DatabaseManager.getListNewsOfCustomer());

        if (mTypeNews == MainApplication.TYPE_NEWS_MORE) {
            news.clear();
            List<NewMore> listNewMore = DatabaseManager.getListNewMore();
            for (NewMore item : listNewMore) {
                NewsOfCustomer newsOfCustomer = new NewsOfCustomer(item);
                news.add(newsOfCustomer);
            }
        }

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

        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(), news, this, mTypeNews);
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

                        DatabaseManager.addListNewsOfCustomer(response.body());

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
            Call<List<NewMore>> newsMore = apiService.getNewsMoreByUserId(account.getId(), city);
            newsMore.enqueue(new Callback<List<NewMore>>() {
                @Override
                public void onResponse(Call<List<NewMore>> call, Response<List<NewMore>> response) {
                    if (response.body() != null) {


                        String account = MainApplication.getPreferences().getString(MainApplication.ACCOUNT_CUSTOMER, "");
                        String user = new Gson().fromJson(account, AccountOfUser.class).getId();

                        DatabaseManager.addListNewMore(response.body());
                        setListMessages();
                        mSwipeContainer.setRefreshing(false);
                        Log.d(TAG, "getNewsOfCustomer " + response.body().size());
                    } else {
                        Log.d(TAG, "getNewsOfCustomer null");
                    }
                }

                @Override
                public void onFailure(Call<List<NewMore>> call, Throwable t) {
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

        Log.d(TAG, "new like: " + listLike.size() + " = " + listLike);


        List<OptionNews> listDeleteNews = new ArrayList<>();
        listDeleteNews.addAll(DatabaseManager.getListOptionNews(MainApplication.NEW_DELETE, MainApplication.CUSTOMER));

        Log.d(TAG, "new delete: " + listDeleteNews.size() + " = " + listDeleteNews);

        List<NewsOfCustomer> news = new ArrayList<>();
        news.addAll(DatabaseManager.getListNewsOfCustomer());

        if (mTypeNews == MainApplication.TYPE_NEWS_MORE) {
            news.clear();
            List<NewMore> listNewMore = DatabaseManager.getListNewMore();
            for (NewMore item : listNewMore) {
                NewsOfCustomer newsOfCustomer = new NewsOfCustomer(item);
                news.add(newsOfCustomer);
            }
        }
//        mTypeNews
        Log.d(TAG, "new new: " + news.size() + " = " + news);

        for (OptionNews likeNews : listLike) {
            for (NewsOfCustomer item : news) {
                if (likeNews.getIdNews().equals(item.getMessage_id())) {
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
        List<NewsOfCustomer> listNew = new ArrayList<>();
        int size = news.size();
        Log.d(TAG, "size: " + size);
        for (NewsOfCustomer item : news) {
            if (item.isLike()) {
                listNew.add(item);
                Log.d(TAG, "item like: " + item.getMessage_id());
            }
        }

        Collections.sort(listNew);
        NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(), listNew, this, mTypeNews);
        mRecyclerNews.setAdapter(adapter);
        mSwipeContainer.setRefreshing(false);
    }
}
