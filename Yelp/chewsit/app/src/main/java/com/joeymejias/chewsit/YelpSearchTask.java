package com.joeymejias.chewsit;

import android.location.Location;
import android.os.AsyncTask;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;

/**
 * Created by joshuagoldberg on 8/17/16.
 */


public class YelpSearchTask extends AsyncTask<Void, Void, ArrayList<Business>> {

    @Override
    protected ArrayList<Business> doInBackground(Void... voids) {

        Location lastLocation = null;

        while (lastLocation == null) {
            lastLocation = LocationSingleton.getInstance().getCurrentLocation();
        }

        double lat = lastLocation.getLatitude();
        double lng = lastLocation.getLongitude();

        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(lat)
                .longitude(lng)
                .build();

        return YelpHelper.getInstance().businessSearch(coordinate, YelpHelper.getInstance().getRadius());
    }
}
