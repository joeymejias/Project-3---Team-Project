package com.joeymejias.chewsit;

import android.os.AsyncTask;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Category;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by joshuagoldberg on 8/15/16.
 */
public class YelpHelper {

    private static final String TAG = "YelpHelper";

    private static final String CONSUMER_KEY = "POMEu486tnSM-mVn05MTqw";
    private static final String CONSUMER_SECRET = "Kr7rmCTe1ES8xZvdnPrG6af8-90";
    private static final String TOKEN = "Im7zSBmZdxQxo4BwXvvcF5flXZmitQrs";
    private static final String TOKEN_SECRET = "BKcLj0AF2TxTcg1E9qANg7zdUSQ";

    private static YelpHelper sInstance = null;

    private YelpAPIFactory apiFactory;
    private YelpAPI yelpAPI;
    private ArrayList<Business> businesses;

    private int mSearches = 0;
    private int mOffset = 0;
    private int mSort = 1;

    private YelpHelper() {
        apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        yelpAPI = apiFactory.createAPI();
        businesses = new ArrayList<>();
    }

    public static YelpHelper getInstance() {
        if (sInstance == null) {
            sInstance = new YelpHelper();
        }
        return sInstance;
    }

    public ArrayList<Business> businessSearch (CoordinateOptions coordinate, double radius) {

        // Yelp can only return 40 results max (2 searches with 20 results each), so after 2 searches the offset
        // should be set back to 0. The sorting method is also changed in the hopes of different results showing up
        if (mSearches > 2) {
            mSearches = 0;
            mOffset = 0;
            switch (mSort) {
                default:
                case 0:
                    mSort = 1;
                    break;
                case 1:
                    mSort = 2;
                    break;
                case 2:
                    mSort = 0;
                    break;
            }
        }

        Map<String, String> params = new HashMap<>();

        params.put("category_filter", "restaurants");
        params.put("offset", Integer.toString(mOffset));
        params.put("radius_filter", Double.toString(radius * 1600));
        params.put("sort", Integer.toString(mSort));
        params.put("lang", "en");

        Call<SearchResponse> call = yelpAPI.search(coordinate, params);

        try {
            Response<SearchResponse> response = call.execute();
            for(Business business : response.body().businesses()) {
                businesses.add(business);
            }
            mSearches += 1;
            if (!businesses.isEmpty()) {
                mOffset += businesses.size();
                return businesses;
            } else {
                return businessSearch(coordinate, radius + 1);  // If the business list is empty, search a larger radius
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Business> getBusinesses() {
        return businesses;
    }

    public void resetSearchCounts() {
        mSearches = 0;
        mOffset = 0;
    }
}
