package com.joeymejias.chewsit;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.joeymejias.chewsit.detail_pager.DetailPagerAdapter;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class DetailActivity extends AppCompatActivity {

    private DetailPagerAdapter mDetailPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private int mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mSelectedPosition = getIntent().getIntExtra(MainActivity.SELECTED_POSITION, 0);

        mViewPager = (ViewPager) findViewById(R.id.category_container);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mDetailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), mSelectedPosition);
        mViewPager.setAdapter(mDetailPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}