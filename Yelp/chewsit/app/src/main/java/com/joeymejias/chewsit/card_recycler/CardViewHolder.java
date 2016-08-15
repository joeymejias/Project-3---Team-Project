package com.joeymejias.chewsit.card_recycler;

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
    private TextView mCategoryTextView;

    public CardViewHolder(View itemView) {
        super(itemView);

        mBusinessImageView = (ImageView) itemView.findViewById(R.id.business_image);
        mCategoryTextView = (TextView) itemView.findViewById(R.id.business_category);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        itemView.setOnClickListener(onClickListener);
    }

    public TextView getCategoryTextView() {
        return mCategoryTextView;
    }

    public ImageView getBusinessImageView() {
        return mBusinessImageView;
    }
}
