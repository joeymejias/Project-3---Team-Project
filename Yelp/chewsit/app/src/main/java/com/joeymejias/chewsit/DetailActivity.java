package com.joeymejias.chewsit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.joeymejias.chewsit.main_pager.MainPagerAdapter;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestActivityBehavior;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.yelp.clientlib.entities.Business;

import java.util.Arrays;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class DetailActivity extends AppCompatActivity {

    private int mSelectedPosition;

    private Business mBusiness;

    private ImageView mBusinessImageView;
    private ImageView mYelpAttribution;
    private TextView mNameTv, mAddressTv, mPhoneTv;
    private ImageView mRatingImage;
    private TextView mRatingCount;
    private Button mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mSelectedPosition = getIntent().getIntExtra(MainActivity.SELECTED_POSITION, 2);
        if (mSelectedPosition != 0) {
            mBusiness = YelpHelper.getInstance().getRecommendedBusiness();
        } else {
            mBusiness = YelpHelper.getInstance().getBusinesses().get(mSelectedPosition);
        }

        mBusinessImageView = (ImageView) findViewById(R.id.image_detail);
        mYelpAttribution = (ImageView) findViewById(R.id.yelp_button);
        mNameTv = (TextView) findViewById(R.id.name_detail);
        mPhoneTv = (TextView) findViewById(R.id.phone_detail);
        mAddressTv = (TextView) findViewById(R.id.address_detail);
        mRatingImage = (ImageView) findViewById(R.id.rating_detail);
        mRatingCount = (TextView) findViewById(R.id.rating_count_detail);
        mShareButton = (Button) findViewById(R.id.share_button);

        // Have to change the url path from /ms.jpg to /o.jpg to get full size images
        String imageUrl = mBusiness.imageUrl();
        String updatedImageUrl = null;
        if(imageUrl != null) {
            updatedImageUrl = imageUrl.substring(0, imageUrl.length()-6) + "o.jpg";
        }
        Glide.with(this).load(updatedImageUrl).into(mBusinessImageView);
        mNameTv.setText(mBusiness.name());
        String address = "";
        for(String string : mBusiness.location().displayAddress()) {
            address += string + "\n";
        }

        mRatingCount.setText(mBusiness.reviewCount() + " ratings");

        mAddressTv.setText(address.trim());
        mPhoneTv.setText(mBusiness.phone());

        Glide.with(this)
                .load(mBusiness.ratingImgUrlLarge())
                .into(mRatingImage);

        // Yelp Rating attribution
        mRatingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusiness.url()));
                view.getContext().startActivity(browserIntent);
            }
        });

        // Yelp Attribution
        mYelpAttribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusiness.url()));
                view.getContext().startActivity(browserIntent);
            }
        });

        //Button shareButton = new Button(this);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, mBusiness.url());
                startActivity(Intent.createChooser(sendIntent, "How do you want to share?"));
            }
        });

        // TODO:ADD FACEBOOK BUTTON


        // UBER BUTTON in DetailActivity

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