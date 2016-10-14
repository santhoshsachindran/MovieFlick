package com.santhosh.codepath.movieflicks;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private String mPosterPath;
    private String mOverview;
    private String mReleaseDate;
    private int mMovieId;
    private String mTitle;
    private String mBackdropPath;
    private double mRating;

    public Movie(String posterPath, String overview, String releaseDate, int movieId,
            String title, String backdropPath, double rating) {
        mPosterPath = posterPath;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mMovieId = movieId;
        mTitle = title;
        mBackdropPath = backdropPath;
        mRating = rating;
    }

    protected Movie(Parcel in) {
        mPosterPath = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mMovieId = in.readInt();
        mTitle = in.readString();
        mBackdropPath = in.readString();
        mRating = in.readDouble();
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getMovieId() {
        return mMovieId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public double getRating() {
        return mRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeInt(mMovieId);
        dest.writeString(mTitle);
        dest.writeString(mBackdropPath);
        dest.writeDouble(mRating);
    }
}
