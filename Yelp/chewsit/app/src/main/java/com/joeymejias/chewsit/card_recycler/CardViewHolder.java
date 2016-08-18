package com.joeymejias.chewsit.card_recycler;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joeymejias.chewsit.R;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class CardViewHolder extends RecyclerView.ViewHolder {

    private ImageView mBusinessImageView;
    private ImageView mRatingImageView;
    private TextView mRatingCount;
    private ImageView mYelpAttribution;
    private TextView mCategoryTextView;

    public CardViewHolder(View itemView) {
        super(itemView);

        mBusinessImageView = (ImageView) itemView.findViewById(R.id.business_image);
        mRatingImageView = (ImageView) itemView.findViewById(R.id.business_rating);
        mRatingCount = (TextView) itemView.findViewById(R.id.rating_count);
        mYelpAttribution = (ImageView) itemView.findViewById(R.id.yelp_logo);
        mCategoryTextView = (TextView) itemView.findViewById(R.id.business_category);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        itemView.setOnClickListener(onClickListener);
    }

    public ImageView getRatingImageView() {
        return mRatingImageView;
    }

    public TextView getRatingCount() {
        return mRatingCount;
    }

    public ImageView getYelpAttribution() {
        return mYelpAttribution;
    }

    public TextView getCategoryTextView() {
        return mCategoryTextView;
    }

    public ImageView getBusinessImageView() {
        return mBusinessImageView;
    }
}
