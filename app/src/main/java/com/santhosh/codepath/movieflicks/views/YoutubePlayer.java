package com.santhosh.codepath.movieflicks.views;


import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.API_KEY_KEY;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.YOUTUBE_LINK;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.santhosh.codepath.movieflicks.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubePlayer extends YouTubeBaseActivity {
    @BindView(R.id.player)
    YouTubePlayerView mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_player);
        ButterKnife.bind(this);

        String apiKey = getIntent().getStringExtra(API_KEY_KEY);
        final String youtubeVideo = getIntent().getStringExtra(YOUTUBE_LINK);

        mPlayer.initialize(apiKey, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                    YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(youtubeVideo);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                    YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }
}
