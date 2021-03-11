package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {
    @ColumnInfo
    @PrimaryKey
    private long id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String screenName;

    @ColumnInfo
    private String profileImageUrl;

    // for Parceler
    public User() {}

    public static List<User> fromJsonTweetArray(List<Tweet> tweetFromNetwork) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < tweetFromNetwork.size(); i++) {
            users.add(tweetFromNetwork.get(i).getUser());
        }

        return users;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.setId(jsonObject.getLong("id"));
        user.setName(jsonObject.getString("name"));
        user.setScreenName(jsonObject.getString("screen_name"));
        user.setProfileImageUrl(jsonObject.getString("profile_image_url_https"));

        return user;
    }
}
