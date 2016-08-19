package com.joeymejias.chewsit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checks to see if the user has seen the onBoarding yet; if not, go to the OnBoardActivity
        if (!getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE)
                .getBoolean(OnBoardActivity.SEEN_ON_BOARD, false)) {

            startActivity(new Intent(this, OnBoardActivity.class));
            finish();

        // If they have seen the onBoarding, go the main activity instead
        } else {

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
