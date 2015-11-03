package com.example.android.movies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by alitabet on 10/31/15.
 */
public class MovieProvider extends ContentProvider {
    private static final String LOG_TAG = MovieContract.MovieEntry.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    // Codes for the UriMatcher //////
    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 200;
    private static final int MOVIE_RATING = 300;
    private static final int MOVIE_RATING_WITH_ID = 400;
    ////////

    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME, MOVIE);
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.MovieRatingEntry.TABLE_NAME, MOVIE_RATING);
        matcher.addURI(authority, MovieContract.MovieRatingEntry.TABLE_NAME + "/#", MOVIE_RATING_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());

        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE: {
                return MovieContract.MovieEntry.CONTENT_TYPE;
            }
            case MOVIE_WITH_ID: {
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            case MOVIE_RATING: {
                return MovieContract.MovieRatingEntry.CONTENT_TYPE;
            }
            case MOVIE_RATING_WITH_ID: {
                return MovieContract.MovieRatingEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            // All Movies selected
            case MOVIE: {
                return query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
            }
            // Individual movie based on Id selected
            case MOVIE_WITH_ID: {
                return query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        sortOrder);
            }
            // All Movies selected
            case MOVIE_RATING: {
                return query(MovieContract.MovieRatingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
            }
            // Individual movie based on Id selected
            case MOVIE_RATING_WITH_ID: {
                return query(MovieContract.MovieRatingEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieRatingEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        sortOrder);
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    private Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                returnUri = insert(MovieContract.MovieEntry.TABLE_NAME, uri, values);
                break;
            }
            case MOVIE_RATING: {
                returnUri = insert(MovieContract.MovieRatingEntry.TABLE_NAME, uri, values);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private Uri insert(String tableName, Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id = db.insert(tableName, null, values);
        // insert unless it is already contained in the database
        if (_id > 0) {
            returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
        } else {
            throw new android.database.SQLException("Failed to insert row into: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numDeleted;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                numDeleted = delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                numDeleted = delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case MOVIE_RATING:
                numDeleted = delete(MovieContract.MovieRatingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_RATING_WITH_ID:
                numDeleted = delete(MovieContract.MovieRatingEntry.TABLE_NAME,
                        MovieContract.MovieRatingEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numDeleted;
    }

    private int delete(String tableName, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        return db.delete(tableName, selection, selectionArgs);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numInserted = 0;
        switch(match){
            case MOVIE:
                numInserted = bulkInsert(MovieContract.MovieEntry.TABLE_NAME, values);
                break;
            case MOVIE_RATING:
                numInserted = bulkInsert(MovieContract.MovieRatingEntry.TABLE_NAME, values);
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        if (numInserted > 0){
            // if there was successful insertion, notify the content resolver that there
            // was a change
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numInserted;
    }

    private int bulkInsert(String tableName, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // allows for multiple transactions
        db.beginTransaction();

        // keep track of successful inserts
        int numInserted = 0;
        try{
            for(ContentValues value : values){
                if (value == null){
                    throw new IllegalArgumentException("Cannot have null content values");
                }
                long _id = -1;
                try{
                    _id = db.insertOrThrow(tableName,
                            null, value);
                }catch(SQLiteConstraintException e) {
                    Log.w(LOG_TAG, "Attempting to insert " +
                            value.getAsString(
                                    MovieContract.MovieEntry.COLUMN_TITLE)
                            + " but value is already in database.");
                }
                if (_id != -1){
                    numInserted++;
                }
            }
            if(numInserted > 0){
                // If no errors, declare a successful transaction.
                db.setTransactionSuccessful();
            }
        } finally {
            // all transactions occur at once
            db.endTransaction();
        }
        return numInserted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case MOVIE: {
                numUpdated = update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                numUpdated = update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            case MOVIE_RATING: {
                numUpdated = update(MovieContract.MovieRatingEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIE_RATING_WITH_ID: {
                numUpdated = update(MovieContract.MovieRatingEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.MovieRatingEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

    private int update(String tableName, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(tableName,
                contentValues,
                selection,
                selectionArgs);
    }
}
