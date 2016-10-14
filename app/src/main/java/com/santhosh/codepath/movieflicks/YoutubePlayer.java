package com.santhosh.codepath.movieflicks;


import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

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

        String apiKey = getIntent().getStringExtra("API_KEY");
        final String youtubeVideo = getIntent().getStringExtra("YOUTUBE_LINK");

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
