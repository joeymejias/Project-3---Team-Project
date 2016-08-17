package com.joeymejias.chewsit;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;

import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

/**
 * Created by joshuagoldberg on 8/17/16.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DistanceJobService extends JobService {

    YelpSearchTask mTask;
    Location mLastSearchLocation;
    Location mCurrentLocation;
    DistanceJobServiceFinished mJobFinished;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        mLastSearchLocation = LocationSingleton.getInstance().getLastSearchLocation();
        mCurrentLocation = LocationSingleton.getInstance().getCurrentLocation();

        if (mLastSearchLocation != null && mCurrentLocation != null) {
            // Checks to see if the user is over half a mile away (800 meters) from their last search
            if (mCurrentLocation.distanceTo(mLastSearchLocation) > 800) {
                // Reset the search counts before getting new data
                YelpHelper.getInstance().resetSearchCounts();
                mTask = new YelpSearchTask(){
                    @Override
                    protected void onPostExecute(ArrayList<Business> businesses) {
                        super.onPostExecute(businesses);
                        mJobFinished = (DistanceJobServiceFinished) getApplicationContext();
                        mJobFinished.onDistanceJobServiceFinished();
                    }
                };
                mTask.execute(1.0);
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        if(mTask != null) {
            if(mTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                mTask.cancel(false);
            }
        }
        return false;
    }

    public interface DistanceJobServiceFinished{
        void onDistanceJobServiceFinished();
    }
}
