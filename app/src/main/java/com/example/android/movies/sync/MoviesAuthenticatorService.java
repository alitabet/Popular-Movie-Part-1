package com.example.android.movies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This service  allows the sync adapter framework to access the authenticator.
 */
public class MoviesAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private MoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // create a new authenticator object
        mAuthenticator = new MoviesAuthenticator(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
