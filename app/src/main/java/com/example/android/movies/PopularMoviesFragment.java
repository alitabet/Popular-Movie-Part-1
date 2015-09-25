package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 * This fragment displays the main {@link GridView} with
 * an movie poster for each image extracted from the
 * MovieDB API.
 *
 * @author Ali K Thabet
 *
 */
public class PopularMoviesFragment extends Fragment {

    private ImageAdapter mMoviesAdaptor; // adaptor to interact with GridView

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

        // the ImageAdaptor will read relevant data from a source
        // and it will attach images to the GridView using the
        // read data
        mMoviesAdaptor = new ImageAdapter(getActivity(), // the current context (this activity)
                                R.layout.grid_item_movies, // the id of layout containing ImageView
                                new ArrayList<MovieItem>()); // list of movies to add

        // get a reference to the GridView and attach the ImageAdaptor to it
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdaptor);

        // set an listener for clicking on a movie poster
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // when poster is clicked, call DetailActivity and pass
            // the MovieItem corresponding to the clicked poster
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieItem movie = (MovieItem) mMoviesAdaptor.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(getString(R.string.detail_intent_movie_key), movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    // fetch movie data from MovieDB API using an AsyncTask
    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    // when fragment starts, load movie data
    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    /**
     * AsyncTask to run background thread and fetch data from
     * the MovieDB API. The task wil return an ArrayList of
     * <em>MovieItem</em> objects
     */
    public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<MovieItem>> {

        // LOG_TAG used for debugging
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        // this function parses a JSON string containing movie
        // information into an ArrayList of <em>MovieItem</em>
        // objects
        private ArrayList<MovieItem> getMoviesFromJsonStr (String movieJsonStr)
                    throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_LIST = "results";
            final String MDB_ID = "id";
            final String MDB_TITLE = "original_title";
            final String MDB_THUMB = "backdrop_path";
            final String MDB_SYNP = "overview";
            final String MDB_REL_DATE = "release_date";
            final String MDB_POSTER = "poster_path";
            final String MDB_RATING = "popularity";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);

            // The JSON object returns a list of the most popular movies
            // and for each movie provides some useful information. We are
            // interested in id, tile, poster and thumbnail, synopsis,
            // release date, and popularity rating. The poster and thumbnail
            // are in relative format so we need to add
            // "http://image.tmdb.org/t/p/w185" to get the absolute URL
            // We will store the results of the MDB
            // in a Lis of <em>MovieItem</em> objects

            ArrayList<MovieItem> movieList = new ArrayList<>();

            for (int i = 0; i < movieArray.length(); i++) {

                MovieItem temp = new MovieItem();

                JSONObject movie = movieArray.getJSONObject(i); // get the current movie data
                temp.setId(movie.getInt(MDB_ID)); // movie id
                temp.setTitle(movie.getString(MDB_TITLE)); // original title
                temp.setPosterPath("http://image.tmdb.org/t/p/w185"
                                  + movie.getString(MDB_POSTER)); // URL to poster
                temp.setThumbPath("http://image.tmdb.org/t/p/w185"
                                  + movie.getString(MDB_THUMB)); // URL to thumbnail
                temp.setReleaseDate(movie.getString(MDB_REL_DATE)); // release date
                temp.setSynopsis(movie.getString(MDB_SYNP)); // synopsis
                temp.setRating(movie.getDouble(MDB_RATING)); // get user rating

                movieList.add(temp); // add the extracted movie to the return list
            }

            return movieList;
        }

        @Override
        protected ArrayList<MovieItem> doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            // Fetch from preferences whether to sort
            // by popularity or user rating
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_popular));

            String sortBy = getString(R.string.pref_sort_popular_api);
            if (unitType.equals(getString(R.string.pref_sort_rated))) sortBy = getString(R.string.pref_sort_rated_api);

            String apiKey = getActivity().getString(R.string.api_key);

            try {
                // Construct the URL for the MovieDB API query
                // using the API Key and sorting parameters
                final String FORECAST_BASE_URL = getString(R.string.moviedb_url);
                final String SORT_PARAM = getString(R.string.moviedb_sort_param);
                final String KEY_PARAM = getString(R.string.moviedb_api_key_param);

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortBy)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to MovieDB API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Adding new line not completely necessary for JSON
                    // but helps with debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // if the code couldn't get the movie data then no need
                // to parse it
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMoviesFromJsonStr(moviesJsonStr);
            } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie data.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> results) {
            // if the task managed to read data from
            // the MovieDB API, then store it in the
            // custom adapter
            if (results != null) {
                mMoviesAdaptor.clear();
                for (MovieItem mi : results) {
                    mMoviesAdaptor.add(mi);
                }
            }
        }
    }

    /**
     * Custom adapter to create image views. This adaptor
     * is used to attach to a GridView in the current project.
     * <em>ImageAdapter</em> extends {@link ArrayAdapter}
     * in order to access the <em>add</em> and <em>clear</em>
     * methods
     */
    public class ImageAdapter extends ArrayAdapter<MovieItem> {

        /**
         * The resource indicating what views to inflate to display the content of this
         * array adapter.
         */
        private int mResource;
        private final LayoutInflater mInflater;
        private Context mContext;

        public ImageAdapter(Context context, int resource, ArrayList<MovieItem> objects) {
            super(context, resource, objects);
            mResource = resource;
            mInflater = LayoutInflater.from(context);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rootView;
            ImageView imageView;

            if (convertView == null) {
                rootView = mInflater.inflate(mResource, parent, false);
            } else {
                rootView = convertView;
            }

            imageView = (ImageView) rootView.findViewById(R.id.grid_item_movies_imageview);

            MovieItem item = getItem(position);
            Picasso.with(mContext).load(item.getPosterPath()).into(imageView);

            return rootView;
        }
    }
}
