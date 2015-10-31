package com.example.android.movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Define table and column names for Popular Movies DB
 */
public class MovieContract {
    // The content authority for the Popular Movies App
    public static final String CONTENT_AUTHORITY = "com.example.android.movies.app";

    // The base of all URI's which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path to append to base content.
    // content://com.example.android.movies.app/movie/
    public static final String PATH_MOVIE = "movie";

    /*
        Inner class that defines the contents of the movie table
     */
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

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

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
