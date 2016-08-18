package com.joeymejias.chewsit.card_recycler;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class ItemTouchHelperCallBack extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    public ItemTouchHelperCallBack(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    public interface ItemTouchHelperAdapter {
        void onItemDismiss(int position);
        void onItemSelect(int position);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //   | ItemTouchHelper.START | ItemTouchHelper.END
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(direction == ItemTouchHelper.UP) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        } else if (direction == ItemTouchHelper.DOWN){
            mAdapter.onItemSelect(viewHolder.getAdapterPosition());
        }
    }
}
