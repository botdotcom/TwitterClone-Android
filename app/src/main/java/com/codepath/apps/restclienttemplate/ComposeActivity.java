package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final int MAX_TWEET_LENGTH = 280;
    private static final String ACTIVITY_TAG = "ComposeActivity";
    TwitterClient twitterClient;

    TextInputLayout tweetBodyTextInputLayout;
    TextInputEditText tweetBodyComposeEditText;
    Button tweetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        twitterClient = TwitterApp.getRestClient(this);

        tweetBodyTextInputLayout = findViewById(R.id.tweet_body_compose_textlayout);
        tweetBodyComposeEditText = findViewById(R.id.tweet_body_compose_edittext);
        tweetButton = findViewById(R.id.tweet_button);

        // set click listener on the button
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check tweet body for errors
                final String tweetBodyContent = tweetBodyComposeEditText.getText().toString();


                if (tweetBodyContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetBodyContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                // make API call to Twitter to publish the tweet
                twitterClient.publishTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(ACTIVITY_TAG, "onSuccess: publishTweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(ACTIVITY_TAG, "onSuccess: publishTweet: " + tweet.getBody());
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            Log.e(ACTIVITY_TAG, "JSON exception: publishTweet", e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(ACTIVITY_TAG, "onFailure: publishTweet:" + response , throwable);
                    }
                }, tweetBodyContent);
            }
        });
    }
}