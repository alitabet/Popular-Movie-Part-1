package com.example.android.movies;

import android.content.Intent;
import android.database.Cursor;
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
                    .setData(Utility.getUriWithIDFromSort(getActivity(), cursor));
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

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        // When tablets rotate, the currently selected list item needs to be saved.
//        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
//        // so check for that before storing.
//        gridView.getItemAtPosition(gridView.getFirstVisiblePosition());
//        mPosition = gridView.getFirstVisiblePosition();
//        if (mPosition != GridView.INVALID_POSITION) {
//            outState.putInt(SELECTED_KEY, mPosition);
//        }
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMoviesAdaptor = new MovieAdapter(getActivity(), null, 0);
        // get a reference to the GridView and attach the ImageAdaptor to it
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        gridView.setAdapter(mMoviesAdaptor);

//        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
//            // The gridview probably hasn't even been populated yet.  Actually perform the
//            // swapout in onLoadFinished.
//            mPosition = savedInstanceState.getInt(SELECTED_KEY);
//        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Cursor cursor = getActivity().getContentResolver()
                .query(Utility.getUriFromSort(getActivity()),
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
        Cursor cursor = getActivity().getContentResolver()
                .query(Utility.getUriFromSort(getActivity()),
                        null,
                        null,
                        null,
                        null);

        // for now we will only insert data if the cursor is empty
        if (cursor.getCount() ==0) {
            updateMovies(MAX_PAGES);
        }
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    // fetch movie data from MovieDB API using an AsyncTask
    private void updateMovies(int pageNumber) {
        // Fetch from preferences whether to sort
        // by popularity or user rating
        String sortType =  Utility.getSortType(getActivity());
//        SharedPreferences sharedPrefs =
//                PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String sortType = sharedPrefs.getString(
//                getString(R.string.pref_sort_key),
//                getString(R.string.pref_sort_popular));

        String sortBy = getString(R.string.pref_sort_popular_api);
        if (sortType.equals(getString(R.string.pref_sort_rated))) {
            sortBy = getString(R.string.pref_sort_rated_api);
        }

        String[] params = {String.valueOf(pageNumber), sortBy};
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
        moviesTask.execute(params);

//        String apiKey = BuildConfig.MOVIE_DB_API_KEY;
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(getActivity().getString(R.string.movie_db_base_url))
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        // prepare call in Retrofit 2.0
//        MovieDBApi movieDBApi = retrofit.create(MovieDBApi.class);
//
//        Map<String,String> queryMap = new HashMap<>();
//        queryMap.put(getActivity().getString(R.string.moviedb_api_key_param),apiKey);
//        queryMap.put(getActivity().getString(R.string.moviedb_sort_param),sortBy);
//        queryMap.put(getActivity().getString(R.string.moviedb_page_param),"1");
//
//        Call<MovieResults> call = movieDBApi.getMovieResults(queryMap);
//        Log.i(LOG_TAG, call.toString());
//        call.enqueue(new Callback<MovieResults>() {
//            @Override
//            public void onResponse(Response<MovieResults> response, Retrofit retrofit) {
//
//                if (response.body() == null) return;
//
//                List<MovieItem> results = response.body().results;
//                // Insert the movie data into the database
//                if (results.size() > 0) {
//                    Vector<ContentValues> cVVector = new Vector<>(results.size());
//
//                    for (MovieItem movieItem : results) {
//                        cVVector.add(movieItem.getContentValues());
//                    }
//
//                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
//                    cVVector.toArray(cvArray);
//                    getActivity().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
//
//                }
//                Log.i(LOG_TAG, "Successful loading of " + results.size() + " items.");
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.e(LOG_TAG, "Error retrieving movies: " + t.getMessage());
//                Toast.makeText(getActivity(), "Cannot load movies...", Toast.LENGTH_SHORT).show();
//            }
//        });
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
