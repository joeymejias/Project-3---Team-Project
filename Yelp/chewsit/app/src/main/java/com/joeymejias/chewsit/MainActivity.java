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
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.joeymejias.chewsit.card_recycler.CardRecyclerAdapter;
import com.joeymejias.chewsit.main_pager.MainPagerAdapter;
import com.joeymejias.chewsit.main_pager.NonSwipeableViewPager;
import com.joeymejias.chewsit.main_pager.RecyclerFragment;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements CardRecyclerAdapter.ItemDismissListener, CardRecyclerAdapter.ItemSelectListener {

    public static final int NOTIFICATION_AVAILABLE = 1;
    public static final int NOTIFICATION_NOT_AVAILABLE = 2;

    private static final String TAG = "MainActivity";
    public static final String SELECTED_POSITION = "selected_position";
    public static final String SETTINGS_RADIUS = "settings_radius";

    private double mRadiusSetting;

    private RelativeLayout mSplashColor;
    private ProgressBar mSplash;
    private ImageView mLogo;

    private MainPagerAdapter mMainPagerAdapter;
    private NonSwipeableViewPager mViewPager;
    private TabLayout mTabLayout;

    private JobScheduler mScheduler;

    int onStarted = 0;

    public static final String SHARED_PREFS = "com.joeymejias.chewsit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSplashColor = (RelativeLayout) findViewById(R.id.splash);
        mSplashColor.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        mLogo = (ImageView) findViewById(R.id.chewsit_logo);
        mLogo.setVisibility(View.VISIBLE);

        // on some click or some loading we need to wait for...
        mSplash = (ProgressBar) findViewById(R.id.progressBar);
        mSplash.setVisibility(View.VISIBLE);

        mRadiusSetting = Double.longBitsToDouble(getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
                .getLong(SETTINGS_RADIUS, Double.doubleToLongBits(1.0)));
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
            }
            LocationSingleton.getInstance(this).getGoogleApiClient().connect();

            // if the adapter is not null a Yelp API call has already been made, so just update it.
            // if it's null, then make the Yelp API call
            if (mMainPagerAdapter != null) {
                mMainPagerAdapter.notifyDataSetChanged();
                mTabLayout.getTabAt(0).setIcon(R.drawable.local_dining_white);
                mTabLayout.getTabAt(1).setIcon(R.drawable.settings_white);
            } else {
                new YelpSearchTask() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                        mSplashColor = (RelativeLayout) findViewById(R.id.splash);
                        mSplashColor.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        mLogo = (ImageView) findViewById(R.id.chewsit_logo);
                        mLogo.setVisibility(View.VISIBLE);

                        // on some click or some loading we need to wait for...
                        mSplash = (ProgressBar) findViewById(R.id.progressBar);
                        mSplash.setVisibility(View.VISIBLE);


                    }

                    @Override
                    protected void onPostExecute(ArrayList<Business> businesses) {
                        super.onPostExecute(businesses);

                        mViewPager = (NonSwipeableViewPager) findViewById(R.id.main_container);
                        mTabLayout = (TabLayout) findViewById(R.id.tabs);
                        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
                        mViewPager.setAdapter(mMainPagerAdapter);
                        mTabLayout.setupWithViewPager(mViewPager);
                        mTabLayout.getTabAt(0).setIcon(R.drawable.local_dining_white);
                        mTabLayout.getTabAt(1).setIcon(R.drawable.settings_white);

                        mSplashColor.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        mSplash.setVisibility(ProgressBar.GONE);
                        mLogo.setVisibility(View.GONE);
                    }
                }.execute();
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
            if (mScheduler != null) {
                mScheduler.cancelAll();
            }
        }
        getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
                .edit()
                .putLong(SETTINGS_RADIUS, Double.doubleToLongBits(YelpHelper.getInstance().getRadius()))
                .commit();
    }

    @Override
    public void onItemSelectListener(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(SELECTED_POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onItemDismissListener() {
        if (YelpHelper.getInstance().getBusinesses().size() < 5) {
            YelpSearchTask task = new YelpSearchTask();
            if (!(task.getStatus() == AsyncTask.Status.RUNNING)) {
                task.execute();
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

