package com.joeymejias.chewsit.on_boarding;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.joeymejias.chewsit.R;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class OnBoardFragment extends Fragment {

    // TODO: Replace these URLs with the images we want to use to introduce Chewsit
    // or update the layout entirely
    private static final String IMAGE_URL_1 =
            "https://s-media-cache-ak0.pinimg.com/236x/64/70/fc/6470fcea2b4893fa18699c9276e0eff6.jpg";
    private static final String IMAGE_URL_2 =
            "https://s-media-cache-ak0.pinimg.com/236x/fd/1d/83/fd1d83f9d75e211da80a4616ca3d3461.jpg";
    private static final String IMAGE_URL_3 =
            "https://s-media-cache-ak0.pinimg.com/236x/e6/9a/c3/e69ac369eef7d444b4ee3ffb022fdbd9.jpg";

    private OnBoardingInteractionListener mListener;
    private ImageView mImageView;

    // TODO: Make this a floating action button
    private Button mButton;

    public OnBoardFragment() {}

    public static OnBoardFragment newInstance(int position) {
        OnBoardFragment fragment = new OnBoardFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_on_board, container, false);
        mImageView = (ImageView) viewRoot.findViewById(R.id.image);
        mButton = (Button) viewRoot.findViewById(R.id.pager_button);
        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int tabNumber = getArguments().getInt("position", 1);
        switch (tabNumber) {
            default:
            case 0:
                // TODO: Make sure the images are fitting the view perfectly
                // Might have to look up how to do it with Glide
                Glide.with(this).load(IMAGE_URL_1).into(mImageView);
                break;
            case 1:
                Glide.with(this).load(IMAGE_URL_2).into(mImageView);
                break;
            case 2:
                Glide.with(this).load(IMAGE_URL_3).into(mImageView);
                mButton.setVisibility(View.VISIBLE);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onOnBoardInteraction();
                    }
                });
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBoardingInteractionListener) {
            mListener = (OnBoardingInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBoardingInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnBoardingInteractionListener {
        void onOnBoardInteraction();
    }
}
