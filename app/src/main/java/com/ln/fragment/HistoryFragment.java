package com.ln.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ln.adapter.ViewPagerAdapter;
import com.ln.mycoupon.R;

/**
 * Created by luongnguyen on 4/14/16.
 */
public class HistoryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CreateFragment createFragment;
    private UseFragment useFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        createFragment = new CreateFragment();
        useFragment = new UseFragment();
        adapter.addFragment(createFragment, "CREATE");
        adapter.addFragment(useFragment, "USE");
        viewPager.setAdapter(adapter);
    }
}
