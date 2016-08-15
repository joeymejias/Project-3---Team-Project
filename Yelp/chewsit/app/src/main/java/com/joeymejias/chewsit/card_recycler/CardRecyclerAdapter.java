package com.joeymejias.chewsit.card_recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.joeymejias.chewsit.R;
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

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        // Have to change the url path from /ms.jpg to /o.jpg to get full size images
        String imageUrl = mBusinesses.get(position).imageUrl();
        String updatedImageUrl = null;
        if(imageUrl != null) {
            updatedImageUrl = imageUrl.substring(0, imageUrl.length()-6) + "o.jpg";
        }
        Glide.with(holder.getBusinessImageView().getContext())
                .load(updatedImageUrl)
                .into(holder.getBusinessImageView());
        holder.getCategoryTextView().setText(mBusinesses.get(position).name());
                // categories().get(0).name());
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
        if(mBusinesses.size() == 0) {
            mItemDismissListener.onItemDismissListener();
        }
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

