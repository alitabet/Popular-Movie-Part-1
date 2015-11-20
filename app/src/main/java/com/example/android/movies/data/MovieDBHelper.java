package com.example.android.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Manages a local database for movie data.
 *
 * @author Ali K Thabet
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    // Database version: increased every time schema changes
    private static final int DATABASE_VERSION = 27;

    // name of database
    static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void createTable(SQLiteDatabase sqLiteDatabase, String tableName) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + tableName+ " (" +
                GeneralEntry._ID + " INTEGER PRIMARY KEY," +
                GeneralEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                GeneralEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                GeneralEntry.COLUMN_RATING + " REAL, " +
                GeneralEntry.COLUMN_POPULARITY + " REAL, " +
                GeneralEntry.COLUMN_SYNOPSIS + " TEXT, " +
                GeneralEntry.COLUMN_POSTER + " TEXT, " +
                GeneralEntry.COLUMN_THUMB + " TEXT, " +
                "UNIQUE (" + GeneralEntry._ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the movie data. Each movie item is defined
        // by an ID, title, release date, user rating, synopsis, and poster
        // and thumbnail paths
        createTable(sqLiteDatabase, MovieContract.MovieEntry.TABLE_NAME);
        createTable(sqLiteDatabase, MovieContract.MovieRatingEntry.TABLE_NAME);
        createTable(sqLiteDatabase, MovieContract.MovieFavoriteEntry.TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over whenever the version
        // number of the DB changes
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieRatingEntry.TABLE_NAME);

        // TODO: This is wrong since movie favorites should not be deleted on upgrade. Need to change it
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieFavoriteEntry.TABLE_NAME);

        // create tables again
        onCreate(sqLiteDatabase);
    }
}
