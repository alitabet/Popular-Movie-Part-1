package com.example.android.movies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.movies.api.MovieDBApi;
import com.example.android.movies.api.results.ReviewResults;
import com.example.android.movies.api.results.TrailerResults;
import com.example.android.movies.data.MovieContract;
import com.example.android.movies.models.MovieItem;
import com.example.android.movies.models.Review;
import com.example.android.movies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

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
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final int DETAIL_LOADER = 0;

        private View headerView;
        private View footerView;
        private HeaderViewHolder headerViewHolder;
        private FooterViewHolder footerViewHolder;

        private ArrayAdapter<String> mTrailersAdapter;

        private ArrayList<String> trailerKeys;

        @Bind(R.id.trailer_detail_list_view)
        ListView trailerListView;

        @OnItemClick(R.id.trailer_detail_list_view)
        public void onItemClick(int position) {
            if (trailerKeys.size() > position && trailerKeys.get(position) != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerKeys.get(position)));

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Couldn't call YouTube, no receiving apps installed!");
                }
            }
        }

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // The ArrayAdapter will take data from a source and
            // use it to populate the ListView it's attached to.
            mTrailersAdapter =
                    new ArrayAdapter<>(
                            getActivity(), // The current context (this activity)
                            R.layout.detail_text_view, // The name of the layout ID.
                            R.id.list_item_textview, // The ID of the textview to populate.
                            new ArrayList<String>());

            trailerKeys = new ArrayList<>();

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ButterKnife.bind(this, rootView);

            trailerListView.setAdapter(mTrailersAdapter);

            headerView = inflater.inflate(R.layout.fragment_detail_header, container, false);
            headerViewHolder = new HeaderViewHolder(headerView);

            footerView = inflater.inflate(R.layout.fragment_detail_footer, container, false);
            footerViewHolder = new FooterViewHolder(footerView);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Intent intent = getActivity().getIntent();
            if (intent == null) return null;

            return new CursorLoader(getActivity(),
                    intent.getData(),
                    MovieContract.MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) { return; }

            MovieItem movie = new MovieItem(data);

            // display title, release date, and user rating
            headerViewHolder.titleText.setText(movie.getTitle());

            String[] date = movie.getReleaseDate().split("-");
            headerViewHolder.dateText.setText(date[0]);

            headerViewHolder.ratingText.setText(String.format("%.1f", movie.getRating()) + "/10");

            // fetch thumbnail using thumbnail URL
            String thumbPath = movie.getPosterPath();

            if (thumbPath == null) thumbPath = getString(R.string.poster_url_alt);

            Picasso.with(getActivity()).load(thumbPath).into(headerViewHolder.thumbImage);

            // extract synopsis
            headerViewHolder.synopsisText.setText(movie.getSynopsis());

            getTrailers(String.valueOf(data.getInt(MovieContract.COL_MOVIE_ID)));
            getReviews(String.valueOf(data.getInt(MovieContract.COL_MOVIE_ID)));

            trailerListView.addHeaderView(headerView);
            trailerListView.addFooterView(footerView);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        private void getReviews(String id) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieDBApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MovieDBApi service = retrofit.create(MovieDBApi.class);
            Call<ReviewResults> call = service.getReviews(id, BuildConfig.MOVIE_DB_API_KEY);

            call.enqueue(new Callback<ReviewResults>() {
                @Override
                public void onResponse(Response<ReviewResults> response, Retrofit retrofit) {
                    ReviewResults reviewResult = response.body();
                    List<Review> reviewList = reviewResult.results;
                    StringBuilder sb = new StringBuilder();
                    for (Review review : reviewList) {
                        sb.append(review.getContent());
                        sb.append("\n\n");
                        sb.append(review.getAuthor());
                        sb.append("\n\n\n\n");
                    }
                    footerViewHolder.reviewText.setText(sb.toString());
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(LOG_TAG, "Error loading review: " + t.getMessage());
                }
            });
        }

        private void getTrailers(String id) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieDBApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MovieDBApi service = retrofit.create(MovieDBApi.class);
            Call<TrailerResults> call = service.getTrailers(id, BuildConfig.MOVIE_DB_API_KEY);

            call.enqueue(new Callback<TrailerResults>() {
                @Override
                public void onResponse(Response<TrailerResults> response, Retrofit retrofit) {
                    TrailerResults trailerResults = response.body();
                    List<Trailer> trailerList = trailerResults.results;
                    mTrailersAdapter.clear();
                    int trailerCount = 1;
                    for (Trailer trailer : trailerList) {
                        mTrailersAdapter.add("trailer " + trailerCount++);
                        trailerKeys.add("https://www.youtube.com/watch?v=" + trailer.getKey());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(LOG_TAG, "Error loading review: " + t.getMessage());
                }
            });
        }

        static class HeaderViewHolder {
            @Bind(R.id.title_text)
            TextView titleText;

            @Bind(R.id.date_text)
            TextView dateText;

            @Bind(R.id.rating_text)
            TextView ratingText;

            @Bind(R.id.synopsis_text)
            TextView synopsisText;

            @Bind(R.id.thumb_imageview)
            ImageView thumbImage;

            public HeaderViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

        static class FooterViewHolder {
            @Bind(R.id.review_text)
            TextView reviewText;

            public FooterViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}