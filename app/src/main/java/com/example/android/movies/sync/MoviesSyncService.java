package com.example.android.movies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MoviesSyncService extends Service {
    public final String LOG_TAG = MoviesSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static MoviesSyncAdapter sMoviesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate - MoviesSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMoviesSyncAdapter == null) {
                sMoviesSyncAdapter = new MoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
