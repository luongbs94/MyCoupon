package com.ln.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ln.adapter.NewsAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.model.Message;
import com.ln.mycoupon.MainApplication;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/6/16.
 */
public class NewsFragment extends Fragment {

    private LoveCouponAPI mApiServices;

    private View mView;
    private RecyclerView mRecNews;
    private List<Message> mListNews = new ArrayList<>();
    private String TAG = getClass().getSimpleName();


    public NewsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_news, container, false);
        initViews();
        getNewsByCompanyId();
        Log.d(TAG, "onCreate");
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        getNewsByCompanyId();
    }

    private void initViews() {
        mRecNews = (RecyclerView) mView.findViewById(R.id.rec_coupon);
        mRecNews.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void getNewsByCompanyId() {

        mListNews = new ArrayList<>();
        Call<List<Message>> call = mApiServices.getNewsByCompanyId(7);
        call.enqueue(new Callback<List<Message>>() {

            @Override
            public void onResponse(Call<List<Message>> arg0,
                                   Response<List<Message>> arg1) {
                mListNews = arg1.body();

                Log.d(TAG, mListNews.size() + "");
                NewsAdapter adapter = new NewsAdapter(getActivity(), mListNews);
                mRecNews.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Message>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }
}
