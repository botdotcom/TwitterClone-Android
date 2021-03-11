package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    private String body;
    private String createdAt;
    private long id;
    private User user;

    // for Parceler
    public Tweet() {}

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public long getId() {
        return id;
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.setBody(jsonObject.getString("text"));
        tweet.setCreatedAt(getRelativeTimeAgo(jsonObject.getString("created_at")));
        tweet.setUser(User.fromJson(jsonObject.getJSONObject("user")));
        tweet.setId(jsonObject.getLong("id"));

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterDateTimeFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(twitterDateTimeFormat, Locale.ENGLISH);
        simpleDateFormat.setLenient(true);

        String relativeDateTime = "";
        try {
            long dateInMillis = simpleDateFormat.parse(rawJsonDate).getTime();
            relativeDateTime = DateUtils.getRelativeTimeSpanString(dateInMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDateTime;
    }
}
