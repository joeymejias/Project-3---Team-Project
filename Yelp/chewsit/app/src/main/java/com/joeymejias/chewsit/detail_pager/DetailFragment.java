package com.joeymejias.chewsit.detail_pager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.joeymejias.chewsit.R;
import com.joeymejias.chewsit.YelpHelper;
import com.joeymejias.chewsit.card_recycler.CardViewHolder;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Review;

import java.util.ArrayList;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";

    private static final String POSITION_KEY = "position";
    private static final String BUSINESS_KEY = "businessListNumber";

    private Business mBusiness;

    private ImageView mBusinessImageView;
    private ImageView mYelpAttribution;
    private TextView mNameTv, mAddressTv, mPhoneTv, mSnippetTv;
    private ImageView mRatingImage;
    private Button mShareButton;

    private OnFragmentInteractionListener mListener;

    public DetailFragment() {}

    public static DetailFragment newInstance(int position, int businessListNumber) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(BUSINESS_KEY, businessListNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getInt(BUSINESS_KEY) != 0) {
                mBusiness = YelpHelper.getInstance().getRecommendedBusiness();
            }
            else {
                mBusiness = YelpHelper.getInstance().getBusinesses().get(getArguments().getInt(BUSINESS_KEY));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_detail_business, container, false);
        mBusinessImageView = (ImageView) viewRoot.findViewById(R.id.image_detail);
        mYelpAttribution = (ImageView) viewRoot.findViewById(R.id.yelp_button);
        mNameTv = (TextView) viewRoot.findViewById(R.id.name_detail);
        mPhoneTv = (TextView) viewRoot.findViewById(R.id.phone_detail);
        mAddressTv = (TextView) viewRoot.findViewById(R.id.address_detail);
        mRatingImage = (ImageView) viewRoot.findViewById(R.id.rating_detail);
        //mSnippetTv = (TextView) viewRoot.findViewById(R.id.snippet_detail);
        mShareButton = (Button) viewRoot.findViewById(R.id.share_button);
        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Have to change the url path from /ms.jpg to /o.jpg to get full size images
        String imageUrl = mBusiness.imageUrl();
        String updatedImageUrl = null;
        if(imageUrl != null) {
            updatedImageUrl = imageUrl.substring(0, imageUrl.length()-6) + "o.jpg";
        }
        Glide.with(view.getContext())
                .load(updatedImageUrl)
                .into(mBusinessImageView);
        mNameTv.setText(mBusiness.name());
        String address = "";
        for(String string : mBusiness.location().displayAddress()) {
            address += string + "\n";
        }
        mAddressTv.setText(address.trim());
        mPhoneTv.setText(mBusiness.phone());

        Glide.with(mRatingImage.getContext())
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

        //mSnippetTv.setText(mBusiness.snippetText());

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

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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
