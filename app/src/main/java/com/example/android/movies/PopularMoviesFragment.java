package com.example.android.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * A placeholder fragment containing a simple view.
 * This fragment displays the main {@link GridView} with
 * an movie poster for each image extracted from the
 * MovieDB API.
 *
 * @author Ali K Thabet
 */
public class PopularMoviesFragment extends Fragment {

    private final String LOG_TAG        = PopularMoviesFragment.class.getSimpleName();
    private final String MOVIE_LIST_KEY = "movie_list";

    public static final int MAX_PAGES = 10; // maximum page value allowed by API

    private ImageAdapter mMoviesAdaptor; // adaptor to interact with GridView
    private ArrayList<MovieItem> mMovieItems; // list of all movie items

    @Bind(R.id.gridview_movies)
    GridView gridView;

    @OnItemClick(R.id.gridview_movies)
    public void onItemClick(int position) {
        MovieItem movie = mMoviesAdaptor.getItem(position);
        Intent intent = new Intent(getActivity(), DetailActivity.class)
                .putExtra(getString(R.string.detail_intent_movie_key), movie);
        startActivity(intent);
    }

    public PopularMoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieItems = new ArrayList<>();

        if (savedInstanceState != null) {
            mMovieItems = (ArrayList<MovieItem>) savedInstanceState.get(MOVIE_LIST_KEY);
        }

        // the ImageAdaptor will read relevant data from a source
        // and it will attach images to the GridView using the
        // read data
        mMoviesAdaptor = new ImageAdapter(getActivity(), // the current context (this activity)
                R.layout.grid_item_movies, // the id of layout containing ImageView
                mMovieItems); // list of movies to add

        // get a reference to the GridView and attach the ImageAdaptor to it
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        gridView.setAdapter(mMoviesAdaptor);

        // set scroll listener
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int visibleThreshold = 4;
            private int currentPage = 1;
            private int previousTotal = 0;
            private boolean loading = true;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (currentPage > MAX_PAGES) return;

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                } else if (totalItemCount != 0 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    updateMovies(++currentPage);
                    loading = true;
                }
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_KEY, mMovieItems);
    }

    // fetch movie data from MovieDB API using an AsyncTask
    private void updateMovies(Integer pageNumber) {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity(),mMoviesAdaptor);
        // Fetch from preferences whether to sort
        // by popularity or user rating
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));

        String sortBy = getString(R.string.pref_sort_popular_api);
        if (sortType.equals(getString(R.string.pref_sort_rated)))
            sortBy = getString(R.string.pref_sort_rated_api);
        String[] params = {pageNumber.toString(), sortBy};
        moviesTask.execute(params);
    }

    // when fragment starts, load movie data
    @Override
    public void onStart() {
        super.onStart();
        if (mMovieItems.isEmpty()) {
            mMoviesAdaptor.clear();
            updateMovies(1);
        }
    }
}
