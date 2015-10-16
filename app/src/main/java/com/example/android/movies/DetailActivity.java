package com.example.android.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment will display the details of the
     * selected movie.
     */
    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for movie data.
            Intent intent = getActivity().getIntent();
            if (intent != null &&
                    intent.hasExtra(getString(R.string.detail_intent_movie_key))) {
                // get the MovieItem object passed thought he intent
                MovieItem movie = intent.getExtras().
                        getParcelable(getString(R.string.detail_intent_movie_key));

                // display title, release date, and user rating
                ((TextView) rootView.findViewById(R.id.title_text))
                        .setText(movie.getTitle());
                ((TextView) rootView.findViewById(R.id.date_text))
                        .setText(movie.getReleaseDate());
                // user rating rounded to 2 decimal places
                ((TextView) rootView.findViewById(R.id.rating_text))
                        .setText(String.format("%.2f", movie.getRating()));

                // fetch thumbnail using thumbnail URL
                ImageView imageView = (ImageView) rootView.findViewById(R.id.thumb_imageview);

                String thumbPath = movie.getThumbPath();

                if (thumbPath == null) thumbPath = getString(R.string.thumb_url_alt);

                Picasso.with(getActivity()).load(thumbPath).into(imageView);

                // extract synopsis
                ((TextView) rootView.findViewById(R.id.synopsis_text))
                        .setText(movie.getSynopsis());
            }

            return rootView;
        }
    }
}