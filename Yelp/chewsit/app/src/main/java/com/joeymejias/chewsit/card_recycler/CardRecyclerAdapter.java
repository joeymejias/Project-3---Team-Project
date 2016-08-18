package com.joeymejias.chewsit.card_recycler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.joeymejias.chewsit.R;
import com.joeymejias.chewsit.YelpHelper;
import com.joeymejias.chewsit.YelpSearchTask;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class CardRecyclerAdapter extends RecyclerView.Adapter<CardViewHolder>
        implements ItemTouchHelperCallBack.ItemTouchHelperAdapter {

    private static final String TAG = "CardRecyclerAdapter";

    private ArrayList<Business> mBusinesses;

    private ItemSelectListener mItemSelectListener;
    private ItemDismissListener mItemDismissListener;

    public CardRecyclerAdapter(ArrayList<Business> businesses) {
        mBusinesses = businesses;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mItemSelectListener = (ItemSelectListener) parent.getContext();
        mItemDismissListener = (ItemDismissListener) parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View parentView = inflater.inflate(R.layout.card_main, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(parentView);
        return cardViewHolder;
    }

    /*****
     * binder for business image and cuisine type
     */
    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        // Have to change the url path from /ms.jpg to /o.jpg to get full size images
        String imageUrl = mBusinesses.get(position).imageUrl();
        String updatedImageUrl = null;

        if (imageUrl != null) {
            updatedImageUrl = imageUrl.substring(0, imageUrl.length() - 6) + "o.jpg";
        }
        Glide.with(holder.getBusinessImageView().getContext())
                .load(updatedImageUrl)
                .into(holder.getBusinessImageView());

        // Gets the yelp rating bar (note: no URL transformation required)
        String ratingImage = mBusinesses.get(position).ratingImgUrlLarge();
        Glide.with(holder.getRatingImageView().getContext())
                .load(ratingImage)
                .into(holder.getRatingImageView());

        // Goes to the yelp page for the restaurant in the card
        holder.getRatingImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinesses.get(position).url()));
                view.getContext().startActivity(browserIntent);
            }
        });
        holder.getRatingCount().setText(mBusinesses.get(position).reviewCount().toString() + " ratings");

        // Goes to the yelp page for the restaurant in the card
        holder.getYelpAttribution().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBusinesses.get(position).url()));
                view.getContext().startActivity(browserIntent);
            }
        });
        holder.getCategoryTextView().setText(mBusinesses.get(position).categories().get(0).name());
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemSelectListener.onItemSelectListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBusinesses.size();
    }

    @Override
    public void onItemDismiss(int position) {
        mBusinesses.remove(position);
        notifyItemRemoved(position);
        mItemDismissListener.onItemDismissListener();
    }

    @Override
    public void onItemSelect(int position) {
        mItemSelectListener.onItemSelectListener(position);
    }

    public interface ItemSelectListener {
        void onItemSelectListener(int position);
    }

    public interface ItemDismissListener {
        void onItemDismissListener();
    }
}

