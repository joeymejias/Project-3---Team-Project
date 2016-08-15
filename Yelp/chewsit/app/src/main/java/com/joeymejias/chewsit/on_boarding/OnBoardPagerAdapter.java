package com.joeymejias.chewsit.on_boarding;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class OnBoardPagerAdapter extends FragmentPagerAdapter {

    private int mPageCount;

    public OnBoardPagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        mPageCount = pageCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                return OnBoardFragment.newInstance(position);
            case 1:
                return OnBoardFragment.newInstance(position);
            default:
            case 2:
                return OnBoardFragment.newInstance(position);
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }
}
