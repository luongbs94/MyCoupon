package com.ln.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ln.adapter.CouponAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.model.CouponTemplate;
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
public class CouponFragment extends Fragment {

    private LoveCouponAPI apiService;
    private ListView listview;
    private List<CouponTemplate> listCoupons = new ArrayList<>();
    private String TAG = "Coupon";
    private LinearLayout layoutView;

    public CouponFragment() {
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
        layoutView = (LinearLayout) view.findViewById(R.id.layout_fragment_coupon);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.title_delete_coupon)
                        .content(R.string.content_delete_coupon)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                deleteCouponTemplate(listCoupons.get(i).getCoupon_template_id());
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return false;
            }
        });

        getCouponTemplate();

        return view;
    }

    public void deleteCouponTemplate(String coupon_template_id) {
        CouponTemplate template = new CouponTemplate();
        template.setCoupon_template_id(coupon_template_id);

        //template.created_date= new Date();

        Call<CouponTemplate> call2 = apiService.deleteCouponTemplate(template);
        call2.enqueue(new Callback<CouponTemplate>() {

            @Override
            public void onResponse(Call<CouponTemplate> arg0,
                                   Response<CouponTemplate> arg1) {

                Snackbar.make(layoutView, R.string.delete_coupon_success, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                getCouponTemplate();

            }

            @Override
            public void onFailure(Call<CouponTemplate> arg0, Throwable arg1) {
                // TODO Auto-generated method stub
                Log.d(TAG, "fail");
                Snackbar.make(layoutView, R.string.delete_coupon_fail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    public void getCouponTemplate() {

        listCoupons = new ArrayList<>();
        Call<List<CouponTemplate>> call = apiService.getCouponTemplatesByCompanyId(7);
        call.enqueue(new Callback<List<CouponTemplate>>() {

            @Override
            public void onResponse(Call<List<CouponTemplate>> arg0,
                                   Response<List<CouponTemplate>> arg1) {
                listCoupons = arg1.body();

                Log.d(TAG, listCoupons.size() + "");


                CouponAdapter adapter = new CouponAdapter(getActivity(), listCoupons);
                listview.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<CouponTemplate>> arg0, Throwable arg1) {
                Log.d(TAG, "Failure");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        getCouponTemplate();
    }
}
