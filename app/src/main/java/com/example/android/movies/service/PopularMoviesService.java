package com.example.android.movies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;

import com.example.android.movies.BuildConfig;
import com.example.android.movies.PopularMoviesFragment;
import com.example.android.movies.R;
import com.example.android.movies.Utility;
import com.example.android.movies.models.MovieItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by thabetak on 11/3/2015.
 */
public class PopularMoviesService extends IntentService {
    public static final String SORT_QUERY_EXTRA = "sqe";
    public static final String PAGE_QUERY_EXTRA = "pqe";
    public static final String LOG_TAG = PopularMoviesService.class.getSimpleName();

    public PopularMoviesService() {
        super("PopularMovies");
    }

    private void getMoviesFromJsonStr(String movieJsonStr)
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray movieArray = movieJson.getJSONArray(MovieItem.MDB_LIST);

        // Insert the movie data into the database
        Vector<ContentValues> cVVector = new Vector<>(movieArray.length());

        // The JSON object returns a list of the most popular movies
        // and for each movie provides some useful information. We are
        // interested in id, tile, poster and thumbnail, synopsis,
        // release date, and popularity rating. The poster and thumbnail
        // are in relative format so we need to add
        // "http://image.tmdb.org/t/p/w185" to get the absolute URL
        // We will store the results of the MDB
        // in a Lis of <em>MovieItem</em> objects

        for (int i = 0; i < movieArray.length(); i++) {

            MovieItem temp = new MovieItem();

            JSONObject movie = movieArray.getJSONObject(i); // get the current movie data
            temp.setId(movie.getInt(MovieItem.MDB_ID)); // movie id
            temp.setTitle(movie.getString(MovieItem.MDB_TITLE)); // original title
            if (movie.getString(MovieItem.MDB_POSTER) == "null") {
                temp.setPosterPath(null);
            } else {
                temp.setPosterPath(getString(R.string.poster_url)
                        + movie.getString(MovieItem.MDB_POSTER)); // URL to poster
            }
            if (movie.getString(MovieItem.MDB_THUMB) == "null") {
                temp.setThumbPath(null);
            } else {
                temp.setThumbPath(getString(R.string.thumb_url)
                        + movie.getString(MovieItem.MDB_THUMB)); // URL to thumbnail
            }
            temp.setReleaseDate(movie.getString(MovieItem.MDB_REL_DATE)); // release date
            temp.setSynopsis(movie.getString(MovieItem.MDB_SYNP)); // synopsis
            temp.setRating(movie.getDouble(MovieItem.MDB_RATING)); // get user rating
            temp.setPopularity(movie.getDouble(MovieItem.MDB_POPULARITY)); // get the movie popularity

            cVVector.add(temp.getContentValues()); // Add the ContentValue of retrieved movie
        }

        String sortOrder = Utility.getSortType(this);
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            this.getContentResolver().bulkInsert(Utility.getUriFromSort(this), cvArray);
        }

        Cursor cursor = this.getContentResolver().query(Utility.getUriFromSort(this),
                null, null, null, Utility.getSortOrder(this));

        cVVector = new Vector<>(cursor.getCount());
        if ( cursor.moveToFirst() ) {
            do {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, cv);
                cVVector.add(cv);
            } while (cursor.moveToNext());
        }

        Log.i(LOG_TAG, "Number of movies retrieved: " + cVVector.size());
    }

    private boolean getData(String sortBy, String apiKey, String pageRequested) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            // Construct the URL for the MovieDB API query
            // using the API Key and sorting parameters
            final String MOVIE_BASE_URL = getString(R.string.moviedb_url);
            final String SORT_PARAM = getString(R.string.moviedb_sort_param);
            final String PAGE_PARAM = getString(R.string.moviedb_page_param);
            final String KEY_PARAM = getString(R.string.moviedb_api_key_param);

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sortBy)
                    .appendQueryParameter(KEY_PARAM, apiKey)
                    .appendQueryParameter(PAGE_PARAM, pageRequested)
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
                return false;
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
                return false;
            }
            moviesJsonStr = buffer.toString();
            getMoviesFromJsonStr(moviesJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // if the code couldn't get the movie data then no need
            // to parse it
            return false;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }finally {
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

        return true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String pagesRequested = "1";
        String sortBy = getString(R.string.pref_sort_popular_api);
        // Check the input
        if (intent.hasExtra(SORT_QUERY_EXTRA)){
            sortBy = intent.getStringExtra(SORT_QUERY_EXTRA);
        }
        if (intent.hasExtra(PAGE_QUERY_EXTRA)){
            pagesRequested = intent.getStringExtra(PAGE_QUERY_EXTRA);
        }

        // if requested page is out of bound then return
        if (Integer.parseInt(pagesRequested) > PopularMoviesFragment.MAX_PAGES) return;

        String apiKey = BuildConfig.MOVIE_DB_API_KEY;

        // fetch all the movies from pages 1 to pagesRequested
        for (int i = 1; i <= Integer.parseInt(pagesRequested); i++) {
            getData(sortBy, apiKey, String.valueOf(i));
        }
    }
}
