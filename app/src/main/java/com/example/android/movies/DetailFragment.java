package com.example.android.movies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.movies.adapters.DetailAdapter;
import com.example.android.movies.api.MovieDBApi;
import com.example.android.movies.api.results.ReviewResults;
import com.example.android.movies.api.results.TrailerResults;
import com.example.android.movies.data.MovieContract;
import com.example.android.movies.models.MovieItem;
import com.example.android.movies.models.Review;
import com.example.android.movies.models.Trailer;

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

/**
 * This fragment will display the details of the
 * selected movie.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;

    static final String DETAIL_URI = "URI";
    private String mReviews;
    private Uri mUri;

//    private View headerView;
//    private View footerView;
//    private HeaderViewHolder headerViewHolder;
//    private FooterViewHolder footerViewHolder;

    private DetailAdapter mTrailersAdapter;

    private ArrayList<String> trailerKeys;

    @Bind(R.id.trailer_detail_list_view)
    ListView trailerListView;

    @OnItemClick(R.id.trailer_detail_list_view)
    public void onItemClick(int position) {
        if (position == 0) return;

        if (trailerKeys.size() > position && trailerKeys.get(position - 1) != null) {
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

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mTrailersAdapter =
                new DetailAdapter(getActivity(),new ArrayList<String>());
//                new ArrayAdapter<>(
//                        getActivity(), // The current context (this activity)
//                        R.layout.detail_text_view, // The name of the layout ID.
//                        R.id.list_item_textview, // The ID of the textview to populate.
//                        new ArrayList<String>());

        trailerKeys = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        trailerListView.setAdapter(mTrailersAdapter);

//        headerView = inflater.inflate(R.layout.fragment_detail_header, container, false);
//        headerViewHolder = new HeaderViewHolder(headerView);
//
//        footerView = inflater.inflate(R.layout.fragment_detail_footer, container, false);
//        footerViewHolder = new FooterViewHolder(footerView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // check if sort order has changed
    void onSortOrderChanged() {
        if (mUri != null) {
            mUri = null;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
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
        if (!data.moveToFirst()) { return; }

        MovieItem movie = new MovieItem(data);

        getTrailers(String.valueOf(data.getInt(MovieContract.COL_MOVIE_ID)));
        getReviews(String.valueOf(data.getInt(MovieContract.COL_MOVIE_ID)));

        if (mReviews != null)
            movie.setReviews(mReviews);

        mTrailersAdapter.setMovieItem(movie);
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
                StringBuilder sb = new StringBuilder();
                for (Review review : reviewList) {
                    sb.append(review.getContent());
                    sb.append("\n\n");
                    sb.append(review.getAuthor());
                    sb.append("\n\n\n\n");
                }
                mReviews = sb.toString();
                mTrailersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                mReviews = null;
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
                mTrailersAdapter.clear();
                mTrailersAdapter.add("HEADER");
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

//    static class HeaderViewHolder {
//        @Bind(R.id.title_text)
//        TextView titleText;
//
//        @Bind(R.id.date_text)
//        TextView dateText;
//
//        @Bind(R.id.rating_text)
//        TextView ratingText;
//
//        @Bind(R.id.synopsis_text)
//        TextView synopsisText;
//
//        @Bind(R.id.thumb_imageview)
//        ImageView thumbImage;
//
//        public HeaderViewHolder(View view) {
//            ButterKnife.bind(this, view);
//        }
//    }
//
//    static class FooterViewHolder {
//        @Bind(R.id.review_text)
//        TextView reviewText;
//
//        public FooterViewHolder(View view) {
//            ButterKnife.bind(this, view);
//        }
//    }
}
