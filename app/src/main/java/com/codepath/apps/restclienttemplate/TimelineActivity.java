package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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
    List<Tweet> tweets;
    TweetsAdapter tweetsAdapter;
    private static final String ACTIVITY_TAG = "TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        twitterClient = TwitterApp.getRestClient(this);

        // find recycler view
        tweetsRecyclerView = findViewById(R.id.tweets_recycler_view);

        // init list of tweets and adapter
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);

        // recycler view setup: layout manager and adapter
        tweetsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tweetsRecyclerView.setAdapter(tweetsAdapter);

        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(ACTIVITY_TAG, "onSuccess");
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    tweetsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(ACTIVITY_TAG, "JSON exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(ACTIVITY_TAG, "onFailure: " + response , throwable);
            }
        });
    }
}