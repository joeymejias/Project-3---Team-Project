package com.joeymejias.chewsit.main_pager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joeymejias.chewsit.EasterEggActivity;
import com.joeymejias.chewsit.R;
import com.joeymejias.chewsit.YelpHelper;

/**
 * Created by joshuagoldberg on 8/18/16.
 */
public class SettingsFragment extends Fragment {

    private SeekBar mDistanceSeekBar;
    private TextView mRadiusChoice;
    private ImageView mEasterEgg;

    private double mDistanceChosen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_main_settings, container, false);
        mDistanceSeekBar = (SeekBar) viewRoot.findViewById(R.id.distance_seekbar);
        mRadiusChoice = (TextView) viewRoot.findViewById(R.id.settings_radius);
        mEasterEgg = (ImageView) viewRoot.findViewById(R.id.easter_egg);
        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDistanceSeekBar.setProgress((int) ((YelpHelper.getInstance().getRadius() - 0.25) * 4));
        mRadiusChoice.setText("Searching within " + YelpHelper.getInstance().getRadius() + " miles");
        mRadiusChoice.setTextSize(20);
        mDistanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mDistanceChosen = (((long) i)/4.0) + 0.25;
                mRadiusChoice.setText("Searching within " + mDistanceChosen + " miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                YelpHelper.getInstance().setRadius(mDistanceChosen);
            }
        });
        mEasterEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EasterEggActivity.class);
                startActivity(intent);
            }
        });
    }
}
