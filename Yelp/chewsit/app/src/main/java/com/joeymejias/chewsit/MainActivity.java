package com.joeymejias.chewsit;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.joeymejias.chewsit.card_recycler.CardRecyclerAdapter;
import com.joeymejias.chewsit.card_recycler.ItemTouchHelperCallBack;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements CardRecyclerAdapter.ItemSelectListener, CardRecyclerAdapter.ItemDismissListener {

    public static final int NOTIFICATION_AVAILABLE = 1;
    public static final int NOTIFICATION_NOT_AVAILABLE = 2;

    private static final String TAG = "MainActivity";
    public static final String SELECTED_POSITION = "selected_position";

    RecyclerView mCardRecycler;
    CardRecyclerAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Location mLastLocation = null;

    public static final String SHARED_PREFS = "com.joeymejias.chewsit";

    private int mOffset = 0;

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
                    }
                }.execute(1.0); //TODO: Relate this input(radius in miles) to a user input in settings
            }
        } else {
            showNetworkNotAvailableNotification();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationSingleton.getInstance(this).getGoogleApiClient().disconnect();
    }

    @Override
    public void onItemSelectListener(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(SELECTED_POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onItemDismissListener() {
        new YelpSearchTask() {
            @Override
            protected void onPostExecute(ArrayList<Business> businesses) {
                super.onPostExecute(businesses);
                mOffset += businesses.size();
                mAdapter.notifyDataSetChanged();
            }
        }.execute(1.0);
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

    /**
     * This logic is not required.
     */
//    private void showNetworkAvailableNotification() {
//        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
//        bigPictureStyle.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.network_available)).build();
//        Intent intent = new Intent(this, NoInternetActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
//        mBuilder.setSmallIcon(R.drawable.icon);
//        mBuilder.setContentTitle("Notification Alert!");
//        mBuilder.setContentText("The network in your location is available");
//        mBuilder.setContentIntent(pIntent);
//        mBuilder.setStyle(bigPictureStyle);
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(NOTIFICATION_AVAILABLE, mBuilder.build());
//    }
}

