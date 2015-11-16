package com.example.android.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.movies.sync.MoviesSyncAdapter;

public class MainActivity extends ActionBarActivity implements PopularMoviesFragment.Callback {

    private static final String MOVIEFRAGMENT_TAG = "MFTAG";

    private boolean mTwoPane;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSortOrder = Utility.getSortType(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, new DetailFragment(), MOVIEFRAGMENT_TAG)
                        .commit();
            }
        }else {
            mTwoPane = false;
        }
        MoviesSyncAdapter.initializeSyncAdapter(this);
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
        checkSortOrderChanged();
    }

    private void checkSortOrderChanged() {
        String sortOrder = Utility.getSortType(this);

        // update the sort order if it has changes
        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            PopularMoviesFragment moviesFragment = (PopularMoviesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if (moviesFragment != null) {
                moviesFragment.onSortOrderChanged();
            }

//            DetailFragment detailFragment = (DetailFragment)
//                    getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
//            if (detailFragment != null) {
//                detailFragment.onSortOrderChanged();
//            }
            mSortOrder = sortOrder;
        }
    }
    @Override
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, movieUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MOVIEFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }
    }
}
