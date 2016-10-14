package com.santhosh.codepath.movieflicks;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String FETCH_URL = "https://api.themoviedb.org/3/movie/";
    private static final String TRAILER_URL = "/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    // TODO : Add own api_key to test this feature;
    private static final String API_KEY = "";

    private String mTrailerUrl;
    private static int movieId;

    @BindView(R.id.movie_trailer)
    ImageView mMovieTrailer;
    @BindView(R.id.play_button)
    ImageView mPlayButton;
    @BindView(R.id.movie_title)
    TextView mMovieTitle;
    @BindView(R.id.movie_poster)
    ImageView mMoviePoster;
    @BindView(R.id.movie_release)
    TextView mMovieRelease;
    @BindView(R.id.movie_overview)
    TextView mMovieOverview;
    @BindView(R.id.details_layout)
    LinearLayout mDetailsLayout;
    @BindView(R.id.movie_rating)
    RatingBar mMovieRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Movie movie = getIntent().getParcelableExtra("PARCELABLE");

        String title = movie.getTitle();
        movieId = movie.getMovieId();
        getLoaderManager().initLoader(0, null, this);
        setTitle(title);

        mMovieTitle.setText(title);
        mMovieOverview.setText(movie.getOverview());
        mMovieRelease.setText(movie.getReleaseDate());
        mMovieRating.setRating((float) movie.getRating());

        Picasso.with(this)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation(10, 10))
                .placeholder(R.drawable.placeholder)
                .into(mMoviePoster);
        Picasso.with(this)
                .load("https://image.tmdb.org/t/p/w500" + movie.getBackdropPath())
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation(10, 10))
                .placeholder(R.drawable.placeholder)
                .into(mMovieTrailer);
    }

    @OnClick(R.id.play_button)
    public void onPlayClick(View view) {
        if (API_KEY.equals("")) {
            Log.d("Test", "Api key is empty");
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("vnd.youtube:" + mTrailerUrl));
            youtubeIntent.putExtra("force_fullscreen", true);
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(YOUTUBE_URL + mTrailerUrl));

            try {
                startActivity(youtubeIntent);
            } catch (ActivityNotFoundException e) {
                startActivity(webIntent);
            }
        } else {
            Intent intent = new Intent(this, YoutubePlayer.class);
            intent.putExtra("API_KEY", API_KEY);
            intent.putExtra("YOUTUBE_LINK", mTrailerUrl);
            startActivity(intent);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new FetchTrailers(this);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data != null) {
            mTrailerUrl = data;
            mPlayButton.setVisibility(View.VISIBLE);
        } else {
            mPlayButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        mPlayButton.setVisibility(View.GONE);
    }

    private static class FetchTrailers extends AsyncTaskLoader<String> {
        public FetchTrailers(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public String loadInBackground() {
            OkHttpClient client = new OkHttpClient();
            Response response;

            Request request = new Request.Builder()
                    .url(FETCH_URL + movieId + TRAILER_URL)
                    .build();

            try {
                response = client.newCall(request).execute();
                return getTrailerUrl(response.body().string());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String getTrailerUrl(String jsonFormat) throws JSONException {
            JSONObject root = new JSONObject(jsonFormat);
            JSONArray results = root.optJSONArray("youtube");

            for (int i = 0; i < results.length(); i++) {
                JSONObject eachResult = results.optJSONObject(i);
                if (eachResult != null) {
                    String type = eachResult.optString("type");
                    if (type.equalsIgnoreCase("Trailer")) {
                        return eachResult.optString("source");
                    }
                }
            }

            return null;
        }
    }
}
