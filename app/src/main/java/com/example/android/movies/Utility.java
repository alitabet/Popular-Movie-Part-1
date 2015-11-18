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
        String sortType = getSortType(context);
        if (sortType.equals(context.getString(R.string.pref_sort_popular))) {
            return GeneralEntry.COLUMN_POPULARITY + " DESC";
        } else {
            return GeneralEntry.COLUMN_TITLE + " ASC";
        }
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
        if (sortType.equals(context.getString(R.string.pref_sort_favorite))){
            return MovieContract.MovieFavoriteEntry.CONTENT_URI;
        }

        throw new UnsupportedOperationException("Unknown sort order: " + sortType);
    }

    public static Uri getUriFromAPISort(Context context, String sortType) {
        if (sortType.equals(context.getString(R.string.pref_sort_popular_api))) {
            return MovieContract.MovieEntry.CONTENT_URI;
        }
        if (sortType.equals(context.getString(R.string.pref_sort_rated_api))) {
            return MovieContract.MovieRatingEntry.CONTENT_URI;
        }

        throw new UnsupportedOperationException("Unknown sort order: " + sortType);
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
        if (sortType.equals(context.getString(R.string.pref_sort_favorite))) {
            return MovieContract.MovieFavoriteEntry.buildMovieUri(
                    cursor.getInt(MovieContract.COL_MOVIE_ID));
        }

        throw new UnsupportedOperationException("Unknown sort order: " + sortType);
    }
}
