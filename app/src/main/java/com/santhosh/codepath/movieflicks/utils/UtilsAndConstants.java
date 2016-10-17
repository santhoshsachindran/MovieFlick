package com.santhosh.codepath.movieflicks.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UtilsAndConstants {
    public static final int HIGH_RATED = 0;
    public static final int NOT_HIGH_RATED = 1;

    public static final String PARCELABLE = "PARCELABLE";
    public static final String CURRENT_STATE = "CURRENT_STATE";
    public static final String FORCE_FULLSCREEN = "force_fullscreen";
    public static final String YOUTUBE_HEADER = "vnd.youtube:";
    public static final String API_KEY_KEY = "API_KEY";
    public static final String YOUTUBE_LINK = "YOUTUBE_LINK";

    public static final String YOUTUBE = "youtube";
    public static final String TYPE = "type";
    public static final String TRAILER = "Trailer";
    public static final String SOURCE = "source";
    public static final String RESULTS = "results";
    public static final String POSTER_PATH = "poster_path";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String MOVIE_ID = "id";
    public static final String TITLE = "title";
    public static final String BACKDROP_PATH = "backdrop_path";
    public static final String VOTE_AVERAGE = "vote_average";


    public static final String BASE_IMAGE_PATH = "https://image.tmdb.org/t/p/w500";

    public static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    public static final String FETCH_TRAILER_URL = "https://api.themoviedb.org/3/movie/";
    public static final String TRAILER_URL = "/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    // TODO : Add own api_key to test this feature;
    public static final String API_KEY = "";

    public static final String FETCH_MOVIES_URL =
            "https://api.themoviedb"
                    + ".org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    public static boolean networkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
