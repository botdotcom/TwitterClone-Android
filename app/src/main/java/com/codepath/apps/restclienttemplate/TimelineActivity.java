package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient twitterClient;
    TweetDao tweetDao;
    RecyclerView tweetsRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Tweet> tweets;
    TweetsAdapter tweetsAdapter;
    EndlessRecyclerViewScrollListener scrollListener;
    FloatingActionButton tweetComposeFloatingButton;
    private static final String ACTIVITY_TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        twitterClient = TwitterApp.getRestClient(this);
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

        tweetComposeFloatingButton = (FloatingActionButton) findViewById(R.id.tweet_compose_floating_button);
        // find recycler view
        tweetsRecyclerView = findViewById(R.id.tweets_recycler_view);
//        DividerItemDecoration recyclerViewItemDecoration = new DividerItemDecoration(tweetsRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
//        tweetsRecyclerView.addItemDecoration(recyclerViewItemDecoration);
        tweetsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
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

        // query existing tweets in db
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(ACTIVITY_TAG, "Showing data from db");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();

                List<Tweet> tweetsFromDb = TweetWithUser.getTweetList(tweetWithUsers);
                tweetsAdapter.clear();
                tweetsAdapter.addAll(tweetsFromDb);
            }
        });

        populateHomeTimeline();

        tweetComposeFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                composeTweet(null);
            }
        });
    }

    private void composeTweet(String tweetText) {
        Intent intent = new Intent(this, ComposeActivity.class);
        intent.putExtra("text", tweetText);
        startActivityForResult(intent, REQUEST_CODE);
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
                    final List<Tweet> tweetFromNetwork = Tweet.fromJsonArray(jsonArray);
                    tweetsAdapter.clear();
                    tweetsAdapter.addAll(tweetFromNetwork);

                    // signal that refresh has finished
                    swipeRefreshLayout.setRefreshing(false);

                    // query existing tweets in db
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(ACTIVITY_TAG, "Saving data to db");
                            // insert users first
                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            // then insert tweets
                            tweetDao.insertModel(tweetFromNetwork.toArray(new Tweet[0]));
                        }
                    });
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // inflate the menu --- adds items to action bar if present
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.compose_menu_button) {
//            // compose icon has been tapped
//            Intent intent = new Intent(this, ComposeActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);
//            // navigate to compose activity
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // get data from intent i.e. the tweet
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // update recyclerview
            // modify data source
            tweets.add(0, tweet);
            // update adapter
            tweetsAdapter.notifyItemInserted(0);
            tweetsRecyclerView.smoothScrollToPosition(0);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}