package com.joeymejias.chewsit.detail_pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.joeymejias.chewsit.YelpHelper;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class DetailPagerAdapter extends FragmentPagerAdapter {

    private int mBusinessListNumber;

    public DetailPagerAdapter(FragmentManager fm, int businessListNumber) {
        super(fm);
        mBusinessListNumber = businessListNumber;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailFragment.newInstance(position, mBusinessListNumber);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "DETAILS";
            case 1:
                return "SHARE";
            case 2:
                return "MAP";
        }
        return null;
    }


}
