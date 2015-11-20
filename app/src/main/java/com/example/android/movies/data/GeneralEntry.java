package com.example.android.movies.data;

import android.provider.BaseColumns;

/**
 * Since all tables have the same structure
 * they all inherit from this Entry
 *
 * @author Ali K Thabet
 */
public class GeneralEntry implements BaseColumns {
    // Movie title
    public static final String COLUMN_TITLE = "title";
    // Movie synopsis
    public static final String COLUMN_SYNOPSIS = "synopsis";
    // Release date of the movie
    public static final String COLUMN_RELEASE_DATE = "release_date";
    // ID of the movie poster
    public static final String COLUMN_POSTER = "poster";
    // ID of the movie thumbnail poster
    public static final String COLUMN_THUMB = "thumb";
    // Rating of movie
    public static final String COLUMN_RATING = "rating";
    // Popularity of movie
    public static final String COLUMN_POPULARITY = "popularity";
}
