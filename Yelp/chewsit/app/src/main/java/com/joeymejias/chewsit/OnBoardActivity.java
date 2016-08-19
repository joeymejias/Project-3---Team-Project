package com.joeymejias.chewsit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.joeymejias.chewsit.on_boarding.MainOnBoardFragment;
import com.joeymejias.chewsit.on_boarding.OnBoardFragment;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class OnBoardActivity extends AppCompatActivity implements OnBoardFragment.OnBoardingInteractionListener {

    public static final String SEEN_ON_BOARD = "HasSeenOnBoard";

    private RelativeLayout mSplashColor;
    private ProgressBar mSplash;
    private ImageView mLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_on_board);

//
//        mSplashColor = (RelativeLayout) findViewById(R.id.splash);
//        mSplashColor.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//
//        mLogo = (ImageView) findViewById(R.id.chewsit_logo);
//        mLogo.setVisibility(View.VISIBLE);

        // on some click or some loading we need to wait for...
        mSplash = (ProgressBar) findViewById(R.id.boardingProgressBar);
        mSplash.setVisibility(View.VISIBLE);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_board);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.on_board_content_container,
                        new MainOnBoardFragment())
                .commit();

    }


    @Override
    public void onOnBoardInteraction() {
        if(!getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE)
                .getBoolean(SEEN_ON_BOARD, false)) {
            getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE)
                    .edit()
                    .putBoolean(SEEN_ON_BOARD, true)
                    .commit();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
