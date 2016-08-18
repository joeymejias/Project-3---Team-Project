package com.joeymejias.chewsit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.joeymejias.chewsit.detail_pager.DetailPagerAdapter;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestActivityBehavior;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class DetailActivity extends AppCompatActivity {

    private DetailPagerAdapter mDetailPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Button mYelpAttribution;
    private Button mShareButton;

    private int mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mSelectedPosition = getIntent().getIntExtra(MainActivity.SELECTED_POSITION, 0);

        mViewPager = (ViewPager) findViewById(R.id.category_container);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mDetailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), mSelectedPosition);
        mViewPager.setAdapter(mDetailPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        /**
         * All the uber logic!
         */
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("N2ZYNVI99lGYzEaxS6RbiHM3cyjGSm6S") //This is necessary
                .setRedirectUri("com.joeymejias.ubertest") //This is necessary if you'll be using implicit grant
                .setEnvironment(SessionConfiguration.Environment.SANDBOX) //Useful for testing your app in the sandbox environment
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS)) //Your scopes for authentication here
                .build();

        //This is a convenience method and will set the default config to be used in other components without passing it directly.
        UberSdk.initialize(config);

        RideRequestButton rideRequestButton = new RideRequestButton(this);

        Activity activity = this; // If you're in a fragment you must get the containing Activity!
        int requestCode = 1234;
        rideRequestButton.setRequestBehavior(new RideRequestActivityBehavior(activity, requestCode));
        // Optional, default behavior is to use current location for pickup
        RideParameters rideParams = new RideParameters.Builder()
                //        .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                //        .setPickupLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
                //        .setDropoffLocation(37.795079, -122.4397805, "Embarcadero", "One Embarcadero Center, San Francisco")
                .build();
        rideRequestButton.setRideParameters(rideParams);



    }
}