package com.joeymejias.chewsit;

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

    public ArrayList<Business> businessSearch (CoordinateOptions coordinate, double radius, int offset) {

        Map<String, String> params = new HashMap<>();

        params.put("category_filter", "restaurants");
        params.put("offset", Integer.toString(offset));
        params.put("radius_filter", Double.toString(radius * 1600));
        params.put("sort", "1");
        params.put("lang", "en");

        Call<SearchResponse> call = yelpAPI.search(coordinate, params);

        try {
            Response<SearchResponse> response = call.execute();
            for(Business business : response.body().businesses()) {
                businesses.add(business);
            }
            return businesses;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return businesses;
    }

    public ArrayList<Business> getBusinesses() {
        return businesses;
    }
}
