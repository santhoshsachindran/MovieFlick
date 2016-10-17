package com.santhosh.codepath.movieflicks.activity;

import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.API_KEY;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.API_KEY_KEY;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.BASE_IMAGE_PATH;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.FETCH_TRAILER_URL;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.FORCE_FULLSCREEN;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.PARCELABLE;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.SOURCE;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.TRAILER;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.TRAILER_URL;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.TYPE;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.YOUTUBE;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.YOUTUBE_HEADER;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.YOUTUBE_LINK;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.YOUTUBE_URL;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.networkAvailable;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.santhosh.codepath.movieflicks.R;
import com.santhosh.codepath.movieflicks.custom.Movie;
import com.santhosh.codepath.movieflicks.views.YoutubePlayer;
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

        Movie movie = getIntent().getParcelableExtra(PARCELABLE);

        String title = movie.getTitle();
        movieId = movie.getMovieId();
        if (networkAvailable(this)) {
            getLoaderManager().initLoader(0, null, this);
        }
        setTitle(title);

        mMovieTitle.setText(title);
        mMovieOverview.setText(movie.getOverview());
        mMovieRelease.setText(movie.getReleaseDate());
        mMovieRating.setRating((float) movie.getRating());

        Picasso.with(this)
                .load(BASE_IMAGE_PATH + movie.getPosterPath())
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation(10, 10))
                .placeholder(R.drawable.placeholder)
                .into(mMoviePoster);
        Picasso.with(this)
                .load(BASE_IMAGE_PATH + movie.getBackdropPath())
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation(10, 10))
                .placeholder(R.drawable.placeholder)
                .into(mMovieTrailer);
    }

    @OnClick(R.id.play_button)
    public void onPlayClick(View view) {
        if (networkAvailable(this)) {
            if (API_KEY.equals("")) {
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(YOUTUBE_HEADER + mTrailerUrl));
                youtubeIntent.putExtra(FORCE_FULLSCREEN, true);
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(YOUTUBE_URL + mTrailerUrl));

                try {
                    startActivity(youtubeIntent);
                } catch (ActivityNotFoundException e) {
                    startActivity(webIntent);
                }
            } else {
                Intent intent = new Intent(this, YoutubePlayer.class);
                intent.putExtra(API_KEY_KEY, API_KEY);
                intent.putExtra(YOUTUBE_LINK, mTrailerUrl);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
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
                    .url(FETCH_TRAILER_URL + movieId + TRAILER_URL)
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
            JSONArray results = root.optJSONArray(YOUTUBE);

            for (int i = 0; i < results.length(); i++) {
                JSONObject eachResult = results.optJSONObject(i);
                if (eachResult != null) {
                    String type = eachResult.optString(TYPE);
                    if (type.equalsIgnoreCase(TRAILER)) {
                        return eachResult.optString(SOURCE);
                    }
                }
            }

            return null;
        }
    }
}
