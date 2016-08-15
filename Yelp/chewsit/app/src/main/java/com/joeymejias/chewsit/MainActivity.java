package com.joeymejias.chewsit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.joeymejias.chewsit.card_recycler.CardRecyclerAdapter;
import com.joeymejias.chewsit.card_recycler.ItemTouchHelperCallBack;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CardRecyclerAdapter.ItemSelectListener{

    private static final String TAG = "MainActivity";
    public static final String SELECTED_POSITION = "selected_position";

    RecyclerView mCategoryRecycler;
    CardRecyclerAdapter mAdapter;
    Location mLastLocation = null;

    // TODO: change this to app package name
    public static final String SHARED_PREFS = "com.joeymejias.chewsit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationSingleton.getInstance(this).getGoogleApiClient().connect();

        // Checks to see if the user has seen the onBoarding yet; if not, it jumps to the OnBoardActivity
        if (!getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
                .getBoolean(OnBoardActivity.SEEN_ON_BOARD, false)) {
            startActivity(new Intent(this, OnBoardActivity.class));
        }
        startActivity(new Intent(this, OnBoardActivity.class));

        setContentView(R.layout.activity_main);

        // Get permission for fine location
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.
                    ACCESS_FINE_LOCATION}, 300);
            return;
        }

        mCategoryRecycler = (RecyclerView) findViewById(R.id.category_recycler);

        // Remove the ability to scroll by overriding the linearlayout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mCategoryRecycler.setLayoutManager(layoutManager);

        new AsyncTask<Double, Void, ArrayList<Business>>() {

            @Override
            protected ArrayList<Business> doInBackground(Double... doubles) {

                while(mLastLocation == null) {
                    mLastLocation = LocationSingleton.getInstance(MainActivity.this).getCurrentLocation();
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

                mAdapter = new CardRecyclerAdapter(businesses);
                mCategoryRecycler.setAdapter(mAdapter);

                // Add swiping to the recyclerview
                ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(mAdapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(mCategoryRecycler);
            }
        }.execute(1.0); //TODO: Relate this input(radius in miles) to a user input in settings

//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.category_content_container,
//                        new MainDetailFragment())
//                .commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!LocationSingleton.getInstance(this).getGoogleApiClient().isConnected()) {
            LocationSingleton.getInstance(this).getGoogleApiClient().connect();
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (LocationSingleton.getInstance(this).getGoogleApiClient().isConnected()) {
            LocationSingleton.getInstance(this).getGoogleApiClient().disconnect();
        }
    }

    @Override
    public void onItemSelectListener(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(SELECTED_POSITION, position);
        startActivity(intent);
    }
}

