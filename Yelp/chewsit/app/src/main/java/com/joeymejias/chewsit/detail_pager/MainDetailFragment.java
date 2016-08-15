package com.joeymejias.chewsit.detail_pager;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

    private DetailPagerAdapter mDetailPagerAdapter;
    private ViewPager mViewPager;
    private Location mLastLocation = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_detail, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.category_container);
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: This Async Task isn't needed; remove it without breaking stuff
        new AsyncTask<Double, Void, ArrayList<Business>>() {

            @Override
            protected ArrayList<Business> doInBackground(Double... doubles) {

                while(mLastLocation == null) {
                    mLastLocation = LocationSingleton.getInstance(getContext()).getCurrentLocation();
                }
                double lat = mLastLocation.getLatitude();
                double lng = mLastLocation.getLongitude();

                CoordinateOptions coordinate = CoordinateOptions.builder()
                        .latitude(lat)
                        .longitude(lng)
                        .build();
                return YelpHelper.getInstance().businessSearch(coordinate, doubles[0]);
            }

            @Override
            protected void onPostExecute(ArrayList<Business> businesses) {
                mDetailPagerAdapter = new DetailPagerAdapter(getFragmentManager(), businesses);
                mViewPager.setAdapter(mDetailPagerAdapter);
            }
        }.execute(1.0);
    }
}
