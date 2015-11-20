package com.example.android.movies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.movies.adapters.MovieAdapter;
import com.example.android.movies.data.MovieContract;
import com.example.android.movies.sync.MoviesSyncAdapter;

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

    private final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();

    public static final int MAX_PAGES = 5; // maximum page value allowed by API

    private MovieAdapter mMoviesAdaptor; // adaptor to interact with GridView
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";
    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }

    @Bind(R.id.gridview_movies)
    GridView gridView;

    @OnItemClick(R.id.gridview_movies)
    public void onItemClick(int position) {
        Cursor cursor = (Cursor) gridView.getItemAtPosition(position);
        if (cursor != null) {
            ((Callback) getActivity())
                    .onItemSelected(Utility.getUriWithIDFromSort(getActivity(), cursor));
        }
        mPosition = position;
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

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        super.onResume();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        updateMovies();
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // check if sort order has changed
    void onSortOrderChanged() {
//        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    // fetch movie data from MovieDB API using an AsyncTask
    private void updateMovies() {
        MoviesSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = Utility.getSortOrder(getActivity());
        return new CursorLoader(getActivity(),
                Utility.getUriFromSort(getActivity()),
                MovieContract.MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdaptor.swapCursor(data);

//        if (mPosition != ListView.INVALID_POSITION) {
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            gridView.smoothScrollToPosition(mPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdaptor.swapCursor(null);
    }
}
