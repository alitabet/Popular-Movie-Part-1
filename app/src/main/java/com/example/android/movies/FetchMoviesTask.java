package com.example.android.movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 * AsyncTask to run background thread and fetch data from
 * the MovieDB API. The task wil return an ArrayList of
 * <em>MovieItem</em> objects
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    // LOG_TAG used for debugging
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private Context mContext;

    public FetchMoviesTask(Context context) {
        mContext = context;
    }

    // this function parses a JSON string containing movie
    // information into an ArrayList of <em>MovieItem</em>
    // objects
    private void getMoviesFromJsonStr(String movieJsonStr)
            throws JSONException {

//        // These are the names of the JSON objects that need to be extracted.
//        final String MDB_TOTAL_PAGES = "total_pages";
//        final String MDB_LIST        = "results";
//        final String MDB_ID          = "id";
//        final String MDB_TITLE       = "original_title";
//        final String MDB_THUMB       = "backdrop_path";
//        final String MDB_SYNP        = "overview";
//        final String MDB_REL_DATE    = "release_date";
//        final String MDB_POSTER      = "poster_path";
//        final String MDB_RATING      = "vote_average";
//        final String MDB_POPULARITY  = "popularity";

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

//        ArrayList<MovieItem> movieList = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {

            MovieItem temp = new MovieItem();

            JSONObject movie = movieArray.getJSONObject(i); // get the current movie data
            temp.setId(movie.getInt(MovieItem.MDB_ID)); // movie id
            temp.setTitle(movie.getString(MovieItem.MDB_TITLE)); // original title
            if (movie.getString(MovieItem.MDB_POSTER) == "null") {
                temp.setPosterPath(null);
            } else {
                temp.setPosterPath(mContext.getString(R.string.poster_url)
                        + movie.getString(MovieItem.MDB_POSTER)); // URL to poster
            }
            if (movie.getString(MovieItem.MDB_THUMB) == "null") {
                temp.setThumbPath(null);
            } else {
                temp.setThumbPath(mContext.getString(R.string.thumb_url)
                        + movie.getString(MovieItem.MDB_THUMB)); // URL to thumbnail
            }
            temp.setReleaseDate(movie.getString(MovieItem.MDB_REL_DATE)); // release date
            temp.setSynopsis(movie.getString(MovieItem.MDB_SYNP)); // synopsis
            temp.setRating(movie.getDouble(MovieItem.MDB_RATING)); // get user rating
            temp.setPopularity(movie.getDouble(MovieItem.MDB_POPULARITY)); // get the movie popularity

            cVVector.add(temp.getContentValues()); // Add the ContentValue of retrieved movie
        }

        String sortOrder = Utility.getSortType(mContext);
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

//            if (sortOrder.equals(mContext.getString(R.string.pref_sort_popular))) {
//                mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
//            } else if (sortOrder.equals(mContext.getString(R.string.pref_sort_rated))) {
//                mContext.getContentResolver().bulkInsert(MovieContract.MovieRatingEntry.CONTENT_URI, cvArray);
//            }

            mContext.getContentResolver().bulkInsert(Utility.getUriFromSort(mContext), cvArray);
        }

//        Cursor cursor = null;
//        if (sortOrder.equals(mContext.getString(R.string.pref_sort_popular))) {
//            cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
//                    null, null, null, sortOrder);
//        }
//        if (sortOrder.equals(mContext.getString(R.string.pref_sort_rated))) {
//            cursor = mContext.getContentResolver().query(MovieContract.MovieRatingEntry.CONTENT_URI,
//                    null, null, null, sortOrder);
//        }
//
//        if (cursor == null) return;

        Cursor cursor = mContext.getContentResolver().query(Utility.getUriFromSort(mContext),
                    null, null, null, Utility.getSortOrder(mContext));

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
            final String FORECAST_BASE_URL = mContext.getString(R.string.moviedb_url);
            final String SORT_PARAM = mContext.getString(R.string.moviedb_sort_param);
            final String PAGE_PARAM = mContext.getString(R.string.moviedb_page_param);
            final String KEY_PARAM = mContext.getString(R.string.moviedb_api_key_param);

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
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
    protected Void doInBackground(String... params) {

        String pagesRequested = "1";
        String sortBy = mContext.getString(R.string.pref_sort_popular_api);
        // Check the input
        if (params.length > 1) sortBy = params[1];
        if (params.length > 0) pagesRequested = params[0];

        // if requested page is out of bound then return
        if (Integer.parseInt(pagesRequested) > PopularMoviesFragment.MAX_PAGES) return null;

        String apiKey = BuildConfig.MOVIE_DB_API_KEY; //mContext.getString(R.string.api_key);

        // fetch all the movies from pages 1 to pagesRequested
        for (int i = 1; i <= Integer.parseInt(pagesRequested); i++) {
            getData(sortBy, apiKey, String.valueOf(i));
        }

        return null;
    }
}
