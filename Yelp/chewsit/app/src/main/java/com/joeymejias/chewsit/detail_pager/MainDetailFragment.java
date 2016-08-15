package com.joeymejias.chewsit.detail_pager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joeymejias.chewsit.LocationSingleton;
import com.joeymejias.chewsit.R;
import com.joeymejias.chewsit.YelpHelper;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class MainDetailFragment extends Fragment {

    private static final String POSITION_KEY = "position";
    private static final String BUSINESS_KEY = "businessListNumber";

    private DetailPagerAdapter mDetailPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private int mBusinessListNumber;

    public MainDetailFragment() {}

    public static MainDetailFragment newInstance(int businessListNumber) {
        MainDetailFragment fragment = new MainDetailFragment();
        Bundle args = new Bundle();
        args.putInt(BUSINESS_KEY, businessListNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBusinessListNumber = getArguments().getInt(BUSINESS_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_detail, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.category_container);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDetailPagerAdapter = new DetailPagerAdapter(getFragmentManager(), mBusinessListNumber);
        mViewPager.setAdapter(mDetailPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
