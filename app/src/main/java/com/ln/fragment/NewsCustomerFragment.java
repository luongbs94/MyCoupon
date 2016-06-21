package com.ln.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ln.adapter.NewsCustomerAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Message;
import com.ln.model.NewsOfUser;
import com.ln.mycoupon.R;
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
    private List<Message> mListNews = new ArrayList<>();
    private List<NewsOfUser> mListNewsOfUser = new ArrayList<>();
    private SwipeRefreshLayout mSwipeContainer;

    private RealmController mRealm;

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
                getMessage();
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getMessage();
        return view;
    }

    public void getMessage() {

        mListNews.clear();
        mListNewsOfUser.clear();
        Call<List<Message>> call = apiService.getNewsByUserId(SaveData.USER_ID);
        call.enqueue(new Callback<List<Message>>() {

            @Override
            public void onResponse(Call<List<Message>> arg0, Response<List<Message>> arg1) {

                mListNews = arg1.body();
                for (Message message : mListNews) {
                    mListNewsOfUser.add(new NewsOfUser(message, false));
                }

                List<LikeNews> listLike = mRealm.getListLike();

                for (LikeNews likeNews : listLike) {
                    for (NewsOfUser newsOfUser : mListNewsOfUser) {
                        if (newsOfUser.getMessage_id().equals(likeNews.getIdNews())) {
                            newsOfUser.setLike(true);
                        }
                    }
                }

                Log.d(TAG, mListNews.size() + "");

                NewsCustomerAdapter adapter = new NewsCustomerAdapter(getActivity(), mListNewsOfUser);
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
}
