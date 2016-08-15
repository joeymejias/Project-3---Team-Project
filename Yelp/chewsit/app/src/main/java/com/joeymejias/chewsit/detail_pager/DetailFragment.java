package com.joeymejias.chewsit.detail_pager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.joeymejias.chewsit.R;
import com.yelp.clientlib.entities.Business;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";

    private static final String CATEGORY_KEY = "category";
    private static final String IMAGE_URL_KEY = "imageUrl";
    private static final String POSITION_KEY = "position";

    private String mCategoryName;
    private String mImageUrl;
    private int mPosition;

    private ImageView mBusinessImageView;
    private TextView mCategoryTextView;

    private OnFragmentInteractionListener mListener;

    public DetailFragment() {}

    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(int position, Business business) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        args.putString(CATEGORY_KEY, business.categories().get(0).name());
        args.putString(IMAGE_URL_KEY, business.imageUrl());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(POSITION_KEY);
            mCategoryName = getArguments().getString(CATEGORY_KEY);
            mImageUrl = getArguments().getString(IMAGE_URL_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_detail, container, false);
        mBusinessImageView = (ImageView) viewRoot.findViewById(R.id.business_image);
        mCategoryTextView = (TextView) viewRoot.findViewById(R.id.business_category);
        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String imageUrl = null;

        // Have to change the url path from /ms.jpg to /o.jpg to get the full size images
        if(mImageUrl != null) {
            imageUrl = mImageUrl.substring(0, mImageUrl.length()-6) + "o.jpg";
        }
        Log.i(TAG, "onViewCreated: " + imageUrl);
        Glide.with(getContext()).load(imageUrl).into(mBusinessImageView);
        mCategoryTextView.setText(mCategoryName);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
