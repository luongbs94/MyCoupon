package com.ln.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ln.adapter.Message2Adapter;
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
 * Created by luongnguyen on 4/8/16.
 */
public class NewsFragment2 extends Fragment {

    private LoveCouponAPI apiService;
    private ListView listview;
    private List<Message> listMessage = new ArrayList<>();
    private String TAG = "Coupon";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = MainApplication.getAPI();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_news2, container, false);

        listview = (ListView) view.findViewById(R.id.recycler_view);

        getMessage();
        return view;
    }

    public void getMessage() {

        listMessage = new ArrayList<>();
        Call<List<Message>> call = apiService.getNewsByUserId("10205539341392320");
        call.enqueue(new Callback<List<Message>>() {

            @Override
            public void onResponse(Call<List<Message>> arg0,
                                   Response<List<Message>> arg1) {
                listMessage = arg1.body();

                Log.d(TAG, listMessage.size() + "");

                Message2Adapter adapter = new Message2Adapter(getActivity(), listMessage);
                listview.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Message>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }
}
