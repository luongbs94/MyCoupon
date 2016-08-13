package com.ln.fragment.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.datetimepicker.date.DatePickerDialog;
import com.ln.app.MainApplication;
import com.ln.mycoupon.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HistoryFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private CreateFragment createFragment;
    private CreateFragment useFragment;
    private Menu menu1;
    private Calendar calendar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setHasOptionsMenu(true);

        return v;
    }

    private void setupViewPager(ViewPager viewPager) {

        List<Fragment> fragments = new ArrayList<>();
        createFragment = CreateFragment.getInstances(MainApplication.TYPE_CREATE);
        useFragment = CreateFragment.getInstances(MainApplication.TYPE_USE);
        fragments.add(createFragment);
        fragments.add(useFragment);

        String[] title = getActivity().getResources().getStringArray(R.array.title_history);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), fragments, title);

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_coupon, menu);
        MenuItem item = menu.findItem(R.id.date);
        Date date = new Date();
        menu1 = menu;
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        item.setTitle(fmt.format(date));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.calendar) {
            DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getActivity().getFragmentManager(), "datePicker");
        }
        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long utc1 = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        MenuItem item = menu1.findItem(R.id.date);

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        item.setTitle(fmt.format(calendar.getTime()));

        createFragment.getData(utc1);
        useFragment.getData(utc1);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {


        private List<Fragment> mFragmentList;
        private String[] mTitle;

        ViewPagerAdapter(FragmentManager manager, List<Fragment> fragments, String[] title) {
            super(manager);
            mFragmentList = fragments;
            mTitle = title;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitle[position];
        }
    }
}
