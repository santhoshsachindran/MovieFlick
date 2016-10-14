package com.santhosh.codepath.movieflicks;

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

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(0, null, FragmentMain.this);
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
                        intent.putExtra("PARCELABLE", movieList.get(position));
                        startActivity(intent);
                    }
                }));

        mRecyclerViewAdapter = new RecyclerViewAdapter(movieList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("CURRENT_STATE");
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("CURRENT_STATE",
                mRecyclerView.getLayoutManager().onSaveInstanceState());
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
            mRecyclerView.setAdapter(new RecyclerViewAdapter(movieList));
            mRecyclerView.invalidate();
        }

        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            mRecyclerView.invalidate();
        }
    }

    private static class FetchMovies extends AsyncTaskLoader<List<Movie>> {
        private final String FETCH_URL =
                "https://api.themoviedb"
                        + ".org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

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
                    .url(FETCH_URL)
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
            JSONArray results = jsonObject.optJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject eachResult = (JSONObject) results.get(i);
                String poster = eachResult.optString("poster_path");
                String overview = eachResult.optString("overview");
                String release = eachResult.optString("release_date");
                int id = eachResult.optInt("id");
                String title = eachResult.optString("title");
                String backdrop = eachResult.optString("backdrop_path");
                double rating = eachResult.optDouble("vote_average");

                movieList.add(new Movie(poster, overview, release, id, title, backdrop, rating));
            }

            return movieList;
        }
    }
}
