package com.joeymejias.chewsit.main_pager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joeymejias.chewsit.R;
import com.joeymejias.chewsit.YelpHelper;
import com.joeymejias.chewsit.card_recycler.CardRecyclerAdapter;
import com.joeymejias.chewsit.card_recycler.ItemTouchHelperCallBack;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class RecyclerFragment extends Fragment {

    private static final String TAG = "RecyclerFragment";

    private RecyclerView mCardRecycler;
    private CardRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ItemTouchHelper mTouchHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_main_recycler, container, false);
        mCardRecycler = (RecyclerView) viewRoot.findViewById(R.id.main_recycler);

        // Remove the ability to scroll by overriding the linearlayout manager
        mLayoutManager = new LinearLayoutManager(viewRoot.getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mAdapter = new CardRecyclerAdapter(YelpHelper.getInstance().getBusinesses());
        mTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallBack(mAdapter));
        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCardRecycler.setLayoutManager(mLayoutManager);
        mCardRecycler.setAdapter(mAdapter);

        // Add swiping to the recyclerview
        mTouchHelper.attachToRecyclerView(mCardRecycler);
    }
}
