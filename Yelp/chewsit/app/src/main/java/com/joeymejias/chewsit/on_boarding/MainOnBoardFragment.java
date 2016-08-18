package com.joeymejias.chewsit.on_boarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.joeymejias.chewsit.R;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class MainOnBoardFragment extends Fragment {

    private OnBoardPagerAdapter mOnBoardPagerAdapter;
    private ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_on_board, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.on_board_container);
        // on some click or some loading we need to wait for...
        return rootView;

    }

    // TODO: Add a view that will be a visual cue showing the user how many pages there are to scroll though
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOnBoardPagerAdapter = new OnBoardPagerAdapter(getFragmentManager(), 3);
        mViewPager.setAdapter(mOnBoardPagerAdapter);
    }
}
