package com.ln.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ln.adapter.Coupon1Adapter;
import com.ln.api.SaveData;
import com.ln.mycoupon.R;
import com.ln.mycoupon.ShopActivity;

/**
 * Created by luongnguyen on 4/8/16.
 */
public class CouponFragment2 extends Fragment{

    ListView listView;
    Coupon1Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_coupon2, container, false);

        listView = (ListView) view.findViewById(R.id.listview);

        adapter = new Coupon1Adapter(getActivity(), SaveData.listCompany);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });




        return view;
    }
}
