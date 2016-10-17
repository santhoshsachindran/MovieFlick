package com.santhosh.codepath.movieflicks.fragment;

import static com.santhosh.codepath.movieflicks.R.id.swipeContainerEmpty;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.BACKDROP_PATH;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.CURRENT_STATE;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.FETCH_MOVIES_URL;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.MOVIE_ID;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.OVERVIEW;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.PARCELABLE;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.POSTER_PATH;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.RELEASE_DATE;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.RESULTS;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.TITLE;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.VOTE_AVERAGE;
import static com.santhosh.codepath.movieflicks.utils.UtilsAndConstants.networkAvailable;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.santhosh.codepath.movieflicks.R;
import com.santhosh.codepath.movieflicks.activity.DetailsActivity;
import com.santhosh.codepath.movieflicks.custom.Movie;
import com.santhosh.codepath.movieflicks.utils.UtilsAndConstants;
import com.santhosh.codepath.movieflicks.views.RecyclerViewAdapter;
import com.santhosh.codepath.movieflicks.views.RecyclerViewItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMain extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>> {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout mSwipeContainer;
    @BindView(R.id.empty_view)
    TextView mEmptyView;
    @BindView(swipeContainerEmpty)
    SwipeRefreshLayout mSwipeContainerEmpty;

    private List<Movie> movieList;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerLayoutManager;
    private Unbinder unbinder;

    public FragmentMain() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);

        mSwipeContainerEmpty.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (networkAvailable(getContext())) {
                    getLoaderManager().restartLoader(0, null, FragmentMain.this);
                } else {
                    mSwipeContainerEmpty.setRefreshing(false);
                    Toast.makeText(getContext(), R.string.no_network,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSwipeContainerEmpty.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (networkAvailable(getContext())) {
                    getLoaderManager().restartLoader(0, null, FragmentMain.this);
                } else {
                    mSwipeContainer.setRefreshing(false);
                    Toast.makeText(getContext(), R.string.no_network,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(),
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra(PARCELABLE, movieList.get(position));
                        startActivity(intent);
                    }
                }));

        mRecyclerViewAdapter = new RecyclerViewAdapter(movieList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        if (!UtilsAndConstants.networkAvailable(getContext())) {
            mSwipeContainerEmpty.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(getString(R.string.no_network));
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mSwipeContainerEmpty.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(0, null, this);
        }
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(CURRENT_STATE);
            if (mRecyclerView != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecyclerView != null) {
            outState.putParcelable(CURRENT_STATE,
                    mRecyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            mRecyclerView.invalidate();
        }
        return new FetchMovies(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        movieList = data;

        if (data != null) {
            mSwipeContainerEmpty.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(new RecyclerViewAdapter(movieList));
            mRecyclerView.invalidate();
        } else {
            if (networkAvailable(getContext())) {
                mEmptyView.setText(getString(R.string.no_data_available));
            } else {
                mEmptyView.setText(getString(R.string.no_network));
            }
            mSwipeContainerEmpty.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        mSwipeContainer.setRefreshing(false);
        mSwipeContainerEmpty.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            mRecyclerView.invalidate();
        }
    }

    private static class FetchMovies extends AsyncTaskLoader<List<Movie>> {
        FetchMovies(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Movie> loadInBackground() {
            OkHttpClient client = new OkHttpClient();
            Response response;

            Request request = new Request.Builder()
                    .url(FETCH_MOVIES_URL)
                    .build();

            try {
                response = client.newCall(request).execute();
                return getMovieList(response.body().string());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private List<Movie> getMovieList(String body) throws JSONException {
            List<Movie> movieList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(body);
            JSONArray results = jsonObject.optJSONArray(RESULTS);

            for (int i = 0; i < results.length(); i++) {
                JSONObject eachResult = (JSONObject) results.get(i);
                String poster = eachResult.optString(POSTER_PATH);
                String overview = eachResult.optString(OVERVIEW);
                String release = eachResult.optString(RELEASE_DATE);
                int id = eachResult.optInt(MOVIE_ID);
                String title = eachResult.optString(TITLE);
                String backdrop = eachResult.optString(BACKDROP_PATH);
                double rating = eachResult.optDouble(VOTE_AVERAGE);

                movieList.add(new Movie(poster, overview, release, id, title, backdrop, rating));
            }

            return movieList;
        }
    }
}
