package com.joeymejias.chewsit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.joeymejias.chewsit.card_recycler.CardRecyclerAdapter;
import com.joeymejias.chewsit.card_recycler.ItemTouchHelperCallBack;
import com.joeymejias.chewsit.detail_pager.DetailPagerAdapter;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements CardRecyclerAdapter.ItemSelectListener, CardRecyclerAdapter.ItemDismissListener {

    private static final String TAG = "MainActivity";
    public static final String SELECTED_POSITION = "selected_position";

    private View mDetailContainer;
    private DetailPagerAdapter mDetailPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private RecyclerView mCardRecycler;
    private CardRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Location mLastLocation = null;

    public static final String SHARED_PREFS = "com.joeymejias.chewsit";

    private boolean mScreenIsLageEnoughForTwoPanes = false;
    private int mOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checks to see if the user has seen the onBoarding yet; if not, it jumps to the OnBoardActivity
        if (!getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
                .getBoolean(OnBoardActivity.SEEN_ON_BOARD, false)) {
            startActivity(new Intent(this, OnBoardActivity.class));
        }

        setContentView(R.layout.activity_main);
        mDetailContainer = findViewById(R.id.detail_content_container);
        mCardRecycler = (RecyclerView) findViewById(R.id.category_recycler);

        // Remove the ability to scroll by overriding the linearlayout manager
        mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mCardRecycler.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get permission for fine location
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.
                    ACCESS_FINE_LOCATION}, 300);
            return;
        }
        LocationSingleton.getInstance(this).getGoogleApiClient().connect();

        // if the adapter is not null, a Yelp API call has already been made. So just update the adapter
        // if it's null, then make the Yelp API call
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            new YelpSearchTask() {
                @Override
                protected void onPostExecute(ArrayList<Business> businesses) {
                    super.onPostExecute(businesses);
                    mOffset += businesses.size();
                    mAdapter = new CardRecyclerAdapter(businesses);
                    mCardRecycler.setAdapter(mAdapter);

                    // Add swiping to the recyclerview
                    ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelperCallBack(mAdapter));
                    touchHelper.attachToRecyclerView(mCardRecycler);

                    // Check to see if we're on a tablet. If so, launch the detail view
                    if (mDetailContainer != null && mDetailContainer.getVisibility() == View.VISIBLE) {
                        mScreenIsLageEnoughForTwoPanes = true;

                        mViewPager = (ViewPager) findViewById(R.id.category_container);
                        mTabLayout = (TabLayout) findViewById(R.id.tabs);
                        mDetailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), 0);
                        mViewPager.setAdapter(mDetailPagerAdapter);
                        mTabLayout.setupWithViewPager(mViewPager);
                    }
                }
            }.execute(1.0); //TODO: Relate this input(radius in miles) to a user input in settings
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationSingleton.getInstance(this).getGoogleApiClient().disconnect();
    }

    @Override
    public void onItemSelectListener(int position) {
        if (mScreenIsLageEnoughForTwoPanes) {
            mDetailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), position);
            mViewPager.setAdapter(mDetailPagerAdapter);
            mDetailPagerAdapter.notifyDataSetChanged();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(SELECTED_POSITION, position);
            startActivity(intent);
        }
    }

    @Override
    public void onItemDismissListener() {
        if (YelpHelper.getInstance().getBusinesses().size() == 0) {
            new YelpSearchTask() {
                @Override
                protected void onPostExecute(ArrayList<Business> businesses) {
                    super.onPostExecute(businesses);
                    mOffset += businesses.size();
                    mAdapter.notifyDataSetChanged();
                }
            }.execute(1.0);
        }
        if (mScreenIsLageEnoughForTwoPanes) {
            while(YelpHelper.getInstance().getBusinesses().isEmpty()) {
            }
            mDetailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), 0);
            mViewPager.setAdapter(mDetailPagerAdapter);
            mDetailPagerAdapter.notifyDataSetChanged();
        }
    }

    private class YelpSearchTask extends AsyncTask<Double, Void, ArrayList<Business>> {

        @Override
        protected ArrayList<Business> doInBackground(Double... doubles) {
            while (mLastLocation == null) {
                mLastLocation = LocationSingleton.getInstance(MainActivity.this).getCurrentLocation();
            }
            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();

            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(lat)
                    .longitude(lng)
                    .build();
            return YelpHelper.getInstance().businessSearch(coordinate, doubles[0], mOffset);
        }
    }
}

