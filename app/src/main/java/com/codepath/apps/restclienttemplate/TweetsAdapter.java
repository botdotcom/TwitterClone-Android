package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    // pass context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // inflate layout for each tweet row
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // bind values based on position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // clear tweets in the recycler view
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // add new tweets to the list
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    // define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView tweetBodyTextView;
        TextView nameTextView;
        TextView screenNameTextView;
        TextView createdAtTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image_view);
            tweetBodyTextView = (TextView) itemView.findViewById(R.id.tweet_body_text_view);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            screenNameTextView = (TextView) itemView.findViewById(R.id.screenname_text_view);
            createdAtTextView = (TextView) itemView.findViewById(R.id.createdat_text_view);
        }

        public void bind(Tweet tweet) {
            tweetBodyTextView.setText(tweet.getBody());
            nameTextView.setText(tweet.getUser().getName());
            screenNameTextView.setText("@" + tweet.getUser().getScreenName());
            createdAtTextView.setText(" . " + tweet.getCreatedAt());
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(profileImageView);
        }
    }
}
