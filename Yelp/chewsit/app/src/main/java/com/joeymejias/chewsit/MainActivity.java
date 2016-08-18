package com.joeymejias.chewsit;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.joeymejias.chewsit.card_recycler.CardRecyclerAdapter;
import com.joeymejias.chewsit.card_recycler.ItemTouchHelperCallBack;
import com.joeymejias.chewsit.detail_pager.DetailPagerAdapter;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements CardRecyclerAdapter.ItemSelectListener, CardRecyclerAdapter.ItemDismissListener {

    public static final int NOTIFICATION_AVAILABLE = 1;
    public static final int NOTIFICATION_NOT_AVAILABLE = 2;

    private static final String TAG = "MainActivity";
    public static final String SELECTED_POSITION = "selected_position";

    private View mDetailContainer;
    private DetailPagerAdapter mDetailPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private RecyclerView mCardRecycler;
    private CardRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private JobScheduler mScheduler;

    public static final String SHARED_PREFS = "com.joeymejias.chewsit";

    private boolean mScreenIsLageEnoughForTwoPanes = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
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

        } else {
            showNetworkNotAvailableNotification();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
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
                        mAdapter = new CardRecyclerAdapter(businesses);
                        mCardRecycler.setAdapter(mAdapter);

                        // Add swiping to the recyclerview
                        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelperCallBack(mAdapter));
                        touchHelper.attachToRecyclerView(mCardRecycler);

                        // Check to see if we're on a tablet. If so, use a master-detail format
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

            // Checks to see if the user is running Lollipop or above; if so, schedules a job
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                JobInfo recommendJob = new JobInfo.Builder(66, new ComponentName(getPackageName(),
                        RecommendJobService.class.getName()))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setMinimumLatency(30_000)
                        .build();
                mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                mScheduler.schedule(recommendJob);
            }

        } else {
            showNetworkNotAvailableNotification();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationSingleton.getInstance(this).getGoogleApiClient().disconnect();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScheduler.cancelAll();
        }
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
        if (YelpHelper.getInstance().getBusinesses().size() < 5) {
            YelpSearchTask task = new YelpSearchTask();
            if (!(task.getStatus() == AsyncTask.Status.RUNNING)) {
                task.execute(1.0); //TODO: Let user input radius
            }
        }
        if (mScreenIsLageEnoughForTwoPanes) {
            if (!YelpHelper.getInstance().getBusinesses().isEmpty()) {
                mDetailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), 0);
                mViewPager.setAdapter(mDetailPagerAdapter);
                mDetailPagerAdapter.notifyDataSetChanged();
            } else {
                //TODO: Tell user that there are no more businesses nearby
            }
        }
    }

    private void showNetworkNotAvailableNotification() {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.no_network)).build();
        Intent intent = new Intent(this, NoInternetActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.icon);
        mBuilder.setContentTitle("Internet unavailable!");
        mBuilder.setContentText("The network in your location is not available");
        mBuilder.setContentIntent(pIntent);
        mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        mBuilder.setStyle(bigPictureStyle);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_NOT_AVAILABLE, mBuilder.build());
        startActivity(intent);
    }
}

