package com.ln.fragment.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;
import com.ln.adapter.CreateCouponAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.model.Coupon;
import com.ln.mycoupon.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private LoveCouponAPI mApiServices;

    private View mView;
    private RecyclerView mRecyclerCreate;
    private ArrayList<Coupon> mListCoupon = new ArrayList<>();
    private CreateCouponAdapter mCouponAdapter;

    private String TAG = getClass().getSimpleName();
    private String utc1 = "Mon, 6 Mar 2016 17:00:00 GMT";
    private String utc2 = "Mon, 17 Oct 2016 17:00:00 GMT";
    private static boolean isInitRecyclerView;
    private Calendar calendar;

    private SwipeRefreshLayout swipeContainer;
    private Menu menu1;
    TextView textView;


    public CreateFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiServices = MainApplication.getAPI();
        calendar = Calendar.getInstance();
        Date date = new Date();

        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        utc1 = date.getTime() + "";
        utc2 = (date.getTime() + 24 * 3600 * 1000) + "";

        getCreateCoupon();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_create, container, false);

        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);
        textView = (TextView) mView.findViewById(R.id.text_no_data);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCreateCoupon();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        initViews();
        getCreateCoupon();

        setHasOptionsMenu(true);
        return mView;
    }

    private void initViews() {
        mRecyclerCreate = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerCreate.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getCreateCoupon() {

        String strCompany = MainApplication.getSharedPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company company = new Gson().fromJson(strCompany, Company.class);
        mListCoupon.clear();
        Call<ArrayList<Coupon>> listCoupon = mApiServices.getCreatedCoupon(company.getCompany_id() + "", utc1, utc2);
        listCoupon.enqueue(new Callback<ArrayList<Coupon>>() {
            @Override
            public void onResponse(Call<ArrayList<Coupon>> call, Response<ArrayList<Coupon>> response) {
                mListCoupon = new ArrayList<>();
                mListCoupon = response.body();

                mCouponAdapter = new CreateCouponAdapter(getActivity(), mListCoupon);
                mRecyclerCreate.setAdapter(mCouponAdapter);
                swipeContainer.setRefreshing(false);

                if (mListCoupon.size() > 0) {
                    mRecyclerCreate.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                } else {
                    mRecyclerCreate.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Coupon>> call, Throwable t) {
                swipeContainer.setRefreshing(false);

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_coupon, menu);

        MenuItem item = menu.findItem(R.id.date);

        Date date = new Date();

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        item.setTitle(fmt.format(date));
        this.menu1 = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendar:
                DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getActivity().getFragmentManager(), "datePicker");
                return true;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        utc1 = calendar.getTimeInMillis() + "";
        utc2 = (calendar.getTimeInMillis() + 24 * 3600 * 1000) + "";


        MenuItem item = menu1.findItem(R.id.date);

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        item.setTitle(fmt.format(calendar.getTime()));

        getCreateCoupon();


    }

}
