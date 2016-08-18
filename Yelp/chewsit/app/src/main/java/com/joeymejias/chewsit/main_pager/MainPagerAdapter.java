package com.joeymejias.chewsit.main_pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {


    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return new RecyclerFragment();
            case 1:
                return new SettingsFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "";
            case 1:
                return "";
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
