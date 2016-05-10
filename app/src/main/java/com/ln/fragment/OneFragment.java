package com.ln.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ln.adapter.CreateCouponAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.model.Coupon;
import com.ln.mycoupon.MainApplication;
import com.ln.mycoupon.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OneFragment extends Fragment {

    ListView listview;
    List<Coupon> listCoupon = new ArrayList<>();
    LoveCouponAPI apiService;
    String TAG = "Coupon";
    String utc1 = "Mon, 6 Mar 2016 17:00:00 GMT";
    String utc2 = "Mon, 17 Oct 2016 17:00:00 GMT";


    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = MainApplication.getAPI();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coupon, container, false);
        listview = (ListView) view.findViewById(R.id.listview);
        getCreateCoupon(SaveData.getCompany().getCompany_id() + "", utc1, utc2);

        // Inflate the layout for this fragment
        return view;
    }

    public void getCreateCoupon(String company_id,String utc1, String utc2){
        Call<List<Coupon>> call = apiService.getCreatedCoupon(company_id, utc1, utc2);

        call.enqueue(new Callback<List<Coupon>>() {

            @Override
            public void onResponse(Call<List<Coupon>> arg0,
                                   Response<List<Coupon>> arg1) {

                listCoupon = arg1.body();
                CreateCouponAdapter adapter = new CreateCouponAdapter(getActivity(), listCoupon);
                listview.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Coupon>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");

            }
        });
    }


}
