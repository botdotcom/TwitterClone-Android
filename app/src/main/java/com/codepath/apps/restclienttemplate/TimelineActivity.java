package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient twitterClient;
    RecyclerView tweetsRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Tweet> tweets;
    TweetsAdapter tweetsAdapter;
    EndlessRecyclerViewScrollListener scrollListener;
    private static final String ACTIVITY_TAG = "TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        twitterClient = TwitterApp.getRestClient(this);

        // find recycler view
        tweetsRecyclerView = findViewById(R.id.tweets_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(ACTIVITY_TAG, "onRefresh: success");
                populateHomeTimeline();
            }
        });

        // init list of tweets and adapter
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);

        // recycler view setup: layout manager and adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        tweetsRecyclerView.setLayoutManager(layoutManager);
        tweetsRecyclerView.setAdapter(tweetsAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(ACTIVITY_TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };

        // add scroll listener to recycler view
        tweetsRecyclerView.addOnScrollListener(scrollListener);

        populateHomeTimeline();
    }

    private void loadMoreData() {
        // send API request to receive appropriate paginated tweets data
        twitterClient.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(ACTIVITY_TAG, "onSuccess: loadMoreData");
                // deserialize and construct new model objects from API response
                JSONArray jsonArray = json.jsonArray;
                try {
                    // append new data object tweets to existing list
                    // notify adapter
                    tweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                } catch (JSONException e) {
                    Log.e(ACTIVITY_TAG, "JSON exception: loadMoreData", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(ACTIVITY_TAG, "onFailure: loadMoreData:" + response , throwable);
            }
        }, tweets.get(tweets.size() - 1).getId());
    }

    private void populateHomeTimeline() {
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(ACTIVITY_TAG, "onSuccess: populateHomeTimeline");
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweetsAdapter.clear();
                    tweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));

                    // signal that refresh has finished
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(ACTIVITY_TAG, "JSON exception: populateHomeTimeline", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(ACTIVITY_TAG, "onFailure: populateHomeTimeline:" + response , throwable);
            }
        });
    }
}