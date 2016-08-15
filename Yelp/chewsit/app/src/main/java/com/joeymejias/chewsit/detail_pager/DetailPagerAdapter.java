package com.joeymejias.chewsit.detail_pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class DetailPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Business> mBusinesses;

    public DetailPagerAdapter(FragmentManager fm, ArrayList<Business> businesses) {
        super(fm);
        mBusinesses = businesses;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailFragment.newInstance(position, mBusinesses.get(position));
    }

    @Override
    public int getCount() {
        return mBusinesses.size();
    }


}
