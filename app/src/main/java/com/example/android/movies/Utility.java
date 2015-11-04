package com.example.android.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.android.movies.data.GeneralEntry;
import com.example.android.movies.data.MovieContract;

/**
 * Created by thabetak on 11/1/2015.
 */
public class Utility {
    public static String getSortOrder(Context context) {
//        SharedPreferences sharedPrefs =
//                PreferenceManager.getDefaultSharedPreferences(context);
//        String sortType = sharedPrefs.getString(
//                context.getString(R.string.pref_sort_key),
//                context.getString(R.string.pref_sort_popular));
        String sortType = getSortType(context);
        String sortOrder = GeneralEntry.COLUMN_POPULARITY + " DESC";
//        if (sortType == context.getString(R.string.pref_sort_rated)) {
//            sortOrder = GeneralEntry.COLUMN_RATING + " DESC";
//        }
//        // Add a second layer of sorting using the movie title
//        sortOrder = sortOrder + ", " + GeneralEntry._ID + " ASC";
        return sortOrder;
    }

    public static String getSortType(Context context){
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(
                context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_popular));
    }

    public static Uri getUriFromSort(Context context) {
        String sortType = getSortType(context);
        if (sortType.equals(context.getString(R.string.pref_sort_popular))) {
            return MovieContract.MovieEntry.CONTENT_URI;
        }
        if (sortType.equals(context.getString(R.string.pref_sort_rated))) {
            return MovieContract.MovieRatingEntry.CONTENT_URI;
        }
        else {
            throw new UnsupportedOperationException("Unknown sort order: " + sortType);
        }
    }

    public static Uri getUriWithIDFromSort(Context context, Cursor cursor) {
        String sortType = getSortType(context);
        if (sortType.equals(context.getString(R.string.pref_sort_popular))) {
            return MovieContract.MovieEntry.buildMovieUri(
                    cursor.getInt(MovieContract.COL_MOVIE_ID));
        }
        if (sortType.equals(context.getString(R.string.pref_sort_rated))) {
            return MovieContract.MovieRatingEntry.buildMovieUri(
                    cursor.getInt(MovieContract.COL_MOVIE_ID));
        }
        else {
            throw new UnsupportedOperationException("Unknown sort order: " + sortType);
        }
    }
}
