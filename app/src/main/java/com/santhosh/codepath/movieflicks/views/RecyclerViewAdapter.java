package com.santhosh.codepath.movieflicks.views;


import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.BASE_IMAGE_PATH;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.HIGH_RATED;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.NOT_HIGH_RATED;

import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.santhosh.codepath.movieflicks.custom.Movie;
import com.santhosh.codepath.movieflicks.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Movie> mMovies;

    public RecyclerViewAdapter(List<Movie> movies) {
        mMovies = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case HIGH_RATED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.high_rated, parent, false);
                viewHolder = new PopularMovieHolder(view);
                break;
            case NOT_HIGH_RATED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.not_high_rated, parent, false);
                viewHolder = new MovieHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Movie movie = mMovies.get(position);

        switch (holder.getItemViewType()) {
            case HIGH_RATED:
                Picasso.with(((PopularMovieHolder) holder).popularMoviePoster.getContext())
                        .load(BASE_IMAGE_PATH + movie.getBackdropPath())
                        .fit()
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(10, 10))
                        .placeholder(R.drawable.placeholder)
                        .into(((PopularMovieHolder) holder).popularMoviePoster);
                ((PopularMovieHolder) holder).popularMovieTitle.setText(movie.getTitle());
                break;
            case NOT_HIGH_RATED:
                String imagePath;

                if (((MovieHolder) holder).moviePoster.getContext().getResources()
                        .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    imagePath = BASE_IMAGE_PATH + movie.getBackdropPath();
                } else {
                    imagePath = BASE_IMAGE_PATH + movie.getPosterPath();
                }
                Picasso.with(((MovieHolder) holder).moviePoster.getContext())
                        .load(imagePath)
                        .fit()
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(10, 10))
                        .placeholder(R.drawable.placeholder)
                        .into(((MovieHolder) holder).moviePoster);
                ((MovieHolder) holder).movieTitle.setText(movie.getTitle());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Movie movie = mMovies.get(position);
        if (movie.getRating() > 5) {
            return HIGH_RATED;
        } else {
            return NOT_HIGH_RATED;
        }
    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    public static class MovieHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_image)
        ImageView moviePoster;
        @BindView(R.id.movie_grid_title)
        TextView movieTitle;

        MovieHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class PopularMovieHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_image_2)
        ImageView popularMoviePoster;
        @BindView(R.id.movie_grid_title_2)
        TextView popularMovieTitle;

        PopularMovieHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
