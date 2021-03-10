package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ComposeActivity extends AppCompatActivity {
    public static final int MAX_TWEET_LENGTH = 280;

    TextInputLayout tweetBodyTextInputLayout;
    TextInputEditText tweetBodyComposeEditText;
    Button tweetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        tweetBodyTextInputLayout = findViewById(R.id.tweet_body_compose_textlayout);
        tweetBodyComposeEditText = findViewById(R.id.tweet_body_compose_edittext);
        tweetButton = findViewById(R.id.tweet_button);

        // set click listener on the button
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check tweet body for errors
                String tweetBodyContent = tweetBodyComposeEditText.getText().toString();


                if (tweetBodyContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetBodyContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                // make API call to Twitter to publish the tweet
                Toast.makeText(ComposeActivity.this, tweetBodyContent, Toast.LENGTH_LONG).show();
            }
        });
    }
}