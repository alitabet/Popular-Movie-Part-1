package com.example.android.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.movies.data.MovieContract;

/**
 * Created by thabetak on 11/1/2015.
 */
public class Utility {
    public static String getSortOrder(Context context) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sortType = sharedPrefs.getString(
                context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_popular));

        String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        if (sortType == context.getString(R.string.pref_sort_rated)) {
            sortOrder = MovieContract.MovieEntry.COLUMN_RATING + " DESC";
        }

        return sortOrder;
    }
}
