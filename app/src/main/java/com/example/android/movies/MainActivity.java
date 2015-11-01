package com.example.android.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    private final String MOVIEFRAGMENT_TAG = "MFTAG";

    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSortOrder = Utility.getSortOrder(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PopularMoviesFragment(), MOVIEFRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = Utility.getSortOrder(this);

        // update the sort order if it has changes
        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            PopularMoviesFragment moviesFragment = (PopularMoviesFragment)
                    getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);

            if (moviesFragment != null) {
                moviesFragment.onSortOrderChanged();
            }
            mSortOrder = sortOrder;
        }
    }
}
