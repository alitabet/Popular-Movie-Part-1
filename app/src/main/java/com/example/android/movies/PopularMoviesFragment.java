package com.example.android.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.movies.data.MovieContract;

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
public class PopularMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER = 0;

    private final String LOG_TAG        = PopularMoviesFragment.class.getSimpleName();
    private final String MOVIE_LIST_KEY = "movie_list";

    public static final int MAX_PAGES = 10; // maximum page value allowed by API

    private MovieAdapter mMoviesAdaptor; // adaptor to interact with GridView
//    private ArrayList<ContentValues> mMovieItems; // list of all movie items

    @Bind(R.id.gridview_movies)
    GridView gridView;

    @OnItemClick(R.id.gridview_movies)
    public void onItemClick(int position) {
        Cursor cursor = (Cursor) gridView.getItemAtPosition(position);
        if (cursor != null) {
            Intent intent = new Intent(getActivity(), DetailActivity.class)
                    .setData(MovieContract.MovieEntry.buildMovieUri(
                            cursor.getInt(MovieContract.MovieEntry.COL_MOVIE_ID)));
//                .putExtra(getString(R.string.detail_intent_movie_key), movie);
            startActivity(intent);
        }
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

        mMoviesAdaptor = new MovieAdapter(getActivity(), null, 0);
        // get a reference to the GridView and attach the ImageAdaptor to it
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        gridView.setAdapter(mMoviesAdaptor);

        // set scroll listener
//        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            private int visibleThreshold = 4;
//            private int currentPage = 1;
//            private int previousTotal = 0;
//            private boolean loading = true;
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (currentPage > MAX_PAGES) return;
//
//                if (loading) {
//                    if (totalItemCount > previousTotal) {
//                        loading = false;
//                        previousTotal = totalItemCount;
//                    }
//                } else if (totalItemCount != 0 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//                    updateMovies(++currentPage);
//                    loading = true;
//                }
//            }
//        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Cursor cursor = getActivity().getContentResolver()
                .query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        // for now we will only insert data if the cursor is empty
        if (cursor.getCount() ==0) {
            updateMovies(MAX_PAGES);
        }
        // initialize loader
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // check if sort order has changed
    void onSortOrderChanged() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    // fetch movie data from MovieDB API using an AsyncTask
    private void updateMovies(int pageNumber) {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
        // Fetch from preferences whether to sort
        // by popularity or user rating
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));

        String sortBy = getString(R.string.pref_sort_popular_api);
        if (sortType.equals(getString(R.string.pref_sort_rated))) {
            sortBy = getString(R.string.pref_sort_rated_api);
        }

        String[] params = {String.valueOf(pageNumber), sortBy};
        moviesTask.execute(params);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = Utility.getSortOrder(getActivity());
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdaptor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdaptor.swapCursor(null);
    }
}
