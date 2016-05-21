package com.ln.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ln.adapter.MessageAdapter;
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

    private LoveCouponAPI apiService;
    private ListView listview;
    private List<Message> listMessage = new ArrayList<>();
    private String TAG = "Coupon";

    public NewsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = MainApplication.getAPI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);
        listview = (ListView) view.findViewById(R.id.listview_news);

        getMessage();

        return view;
    }

    public void getMessage() {

        listMessage = new ArrayList<>();
        Call<List<Message>> call = apiService.getNewsByCompanyId(7);
        call.enqueue(new Callback<List<Message>>() {

            @Override
            public void onResponse(Call<List<Message>> arg0,
                                   Response<List<Message>> arg1) {
                listMessage = arg1.body();

                Log.d(TAG, listMessage.size() + "");

                MessageAdapter adapter = new MessageAdapter(getActivity(), listMessage);
                listview.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Message>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        getMessage();
    }
}
