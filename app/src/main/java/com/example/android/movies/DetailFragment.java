package com.example.android.movies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * This fragment will display the details of the
 * selected movie.
 *
 * @author Ali K Thabet
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;

    public static final String DETAIL_URI = "URI";

    private Uri mUri;

    private ViewHolder viewHolder;
    private LayoutInflater mInflater;
    private ShareActionProvider mShareActionProvider;


    private MovieItem mMovieItem;
    private List<String> mAllReviews =  new ArrayList<>();
    private Map<String,Trailer> mAllTrailers =  new HashMap<>();

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mInflater = inflater;
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = mInflater.inflate(R.layout.fragment_detail, container, false);
        viewHolder = new ViewHolder(rootView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mAllTrailers.size() > 0) {
            ArrayList<Trailer> temp = new ArrayList<>(mAllTrailers.values());
            String url = getActivity().getString(R.string.youtube_url) + temp.get(0).getKey();
            mShareActionProvider.setShareIntent(createShareForecastIntent(url));
        }
    }

    private Intent createShareForecastIntent(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(),
                    mUri,
                    MovieContract.MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data!= null && !data.moveToFirst()) { return; }
        Log.d(LOG_TAG, "Loading...");
        mMovieItem = new MovieItem(data);

        // check if movie is favorite
        Cursor favoriteCursor = queryFavorite();

        if (favoriteCursor.moveToNext()) {
            mMovieItem.setFavorite(true);
        } else {
            mMovieItem.setFavorite(false);
        }

        // set the main view
        setMainView();
        getReviews(Integer.toString(mMovieItem.getId()));
        getTrailers(Integer.toString(mMovieItem.getId()));
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
                if (reviewResult == null) return;

                List<Review> reviewList = reviewResult.results;

                addReviewList(reviewList);
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
                if (trailerResults == null) return;

                List<Trailer> trailerList = trailerResults.results;

                addTrailerList(trailerList);

                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null && getActivity() != null) {
                    mShareActionProvider.setShareIntent(createShareForecastIntent(
                            getActivity().getString(R.string.youtube_url) + trailerList.get(0).getKey()));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, "Error loading review: " + t.getMessage());
            }
        });
    }

    private void addReviewList(List<Review> reviewList) {
        // If we have any trailers then show the label
        if (reviewList.size() > 0) {
            viewHolder.reviewLabel.setVisibility(View.VISIBLE);
        }

        int index = 0;
        for (Review review : reviewList) {
            if (!mAllReviews.contains(review.getId())) {
                StringBuilder sb = new StringBuilder();
                sb.append(review.getContent());
                sb.append("\n\n");
                sb.append(review.getAuthor());
                sb.append("\n");

                mAllReviews.add(review.getId());
                viewHolder.reviewLinearLayout.addView(getReviewView(sb.toString(),index++));
            }
        }
    }

    private void addTrailerList(List<Trailer> trailerList) {
        // If we have any trailers then show the label
        if (trailerList.size() > 0) {
            viewHolder.trailerLabel.setVisibility(View.VISIBLE);
        }

        int trailerCount = 1;
        for (final Trailer trailer : trailerList) {
            if (!mAllTrailers.containsKey(trailer.getId())) {
                mAllTrailers.put(trailer.getId(), trailer);
                viewHolder.trailerLinearLayout.addView(getTrailerView(trailer, trailerCount++));
            }
        }
    }

    private View getTrailerView(Trailer trailer, int count) {
        final String key = trailer.getKey();

        View view = mInflater.inflate(R.layout.trailer_detail_text_view, null);
        TextView textView = (TextView) view.findViewById(R.id.trailer_list_item_textview);
        textView.setText("trailer " + count);
        view.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getActivity().getString(R.string.youtube_url) + key));

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Couldn't call YouTube, no receiving apps installed!");
                }
            }
        });

        return view;
    }

    private View getReviewView(String review, int index) {

        View view = mInflater.inflate(R.layout.review_detail_text_view, null);
        TextView textView = (TextView) view.findViewById(R.id.review_list_item_textview);
        textView.setText(review);

        // Alternate coloring of reviews
        if (index % 2 == 0) {
            textView.setBackgroundColor(Color.GRAY);
        }

        return view;
    }

    private void setMainView() {
        // Make view visible
        viewHolder.detailLinearLayout.setVisibility(View.VISIBLE);

        // display title, release date, and user rating
        viewHolder.titleText.setText(mMovieItem.getTitle());

        String[] date = mMovieItem.getReleaseDate().split("-");
        viewHolder.dateText.setText(date[0]);

        viewHolder.ratingText.setText(String.format("%.1f", mMovieItem.getRating()) + "/10");

        // fetch thumbnail using thumbnail URL
        String thumbPath = mMovieItem.getPosterPath();

        Picasso.with(getActivity()).load(thumbPath).into(viewHolder.thumbImage);

        // extract synopsis
        viewHolder.synopsisText.setText(mMovieItem.getSynopsis());

        viewHolder.favoriteBox.setChecked(mMovieItem.isFavorite());

        viewHolder.favoriteBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    mMovieItem.setFavorite(true);
                    saveFavorite();
                } else {
                    mMovieItem.setFavorite(false);
                    deleteFavorite();
                }
            }
        });
    }

    private Cursor queryFavorite() {
        return getActivity().getContentResolver().query(
                MovieContract.MovieFavoriteEntry.buildMovieUri(mMovieItem.getId()),
                null,
                null,
                null,
                null);
    }

    private void saveFavorite() {
        Cursor favorite = queryFavorite();

        if (!favorite.moveToNext()) {
            getActivity().getContentResolver().insert(
                    MovieContract.MovieFavoriteEntry.CONTENT_URI,
                    mMovieItem.getContentValues());
        }
    }

    private void deleteFavorite() {
        Cursor favorite = queryFavorite();

        if (favorite.moveToNext()) {
            getActivity().getContentResolver().delete(
                    MovieContract.MovieFavoriteEntry.buildMovieUri(mMovieItem.getId()),
                    null,
                    null);
        }
    }

    static class ViewHolder {
        @Bind(R.id.detail_vertical_layout)
        LinearLayout detailLinearLayout;

        @Bind(R.id.trailers_linear_layout)
        LinearLayout trailerLinearLayout;

        @Bind(R.id.reviews_linear_layout)
        LinearLayout reviewLinearLayout;

        @Bind(R.id.title_text)
        TextView titleText;

        @Bind(R.id.date_text)
        TextView dateText;

        @Bind(R.id.rating_text)
        TextView ratingText;

        @Bind(R.id.synopsis_label)
        TextView synopsisLabel;

        @Bind(R.id.synopsis_text)
        TextView synopsisText;

        @Bind(R.id.review_label)
        TextView reviewLabel;

        @Bind(R.id.trailer_label)
        TextView trailerLabel;

        @Bind(R.id.thumb_imageview)
        ImageView thumbImage;

        @Bind(R.id.favorite_check_box)
        CheckBox favoriteBox;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
