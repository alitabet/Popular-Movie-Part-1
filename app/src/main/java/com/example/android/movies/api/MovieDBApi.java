package com.example.android.movies.api;

import com.example.android.movies.api.results.MovieResults;
import com.example.android.movies.api.results.ReviewResults;
import com.example.android.movies.api.results.TrailerResults;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by alitabet on 11/2/15.
 */
public interface MovieDBApi {
    String BASE_URL = "http://api.themoviedb.org/3/";

    @GET("movie/{sort_by}")
    Call<MovieResults> getMovieResults(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewResults> getReviews(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResults> getTrailers(@Path("id") String id, @Query("api_key") String apiKey);

//        @Query("api_key") String key, @Query("sort_by") sortBy);
}

//        String apiKey = BuildConfig.MOVIE_DB_API_KEY;
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(getActivity().getString(R.string.movie_db_base_url))
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        // prepare call in Retrofit 2.0
//        MovieDBApi movieDBApi = retrofit.create(MovieDBApi.class);
//
//        Map<String,String> queryMap = new HashMap<>();
//        queryMap.put(getActivity().getString(R.string.moviedb_api_key_param),apiKey);
//        queryMap.put(getActivity().getString(R.string.moviedb_sort_param),sortBy);
//        queryMap.put(getActivity().getString(R.string.moviedb_page_param),"1");
//
//        Call<MovieResults> call = movieDBApi.getMovieResults(queryMap);
//        Log.i(LOG_TAG, call.toString());
//        call.enqueue(new Callback<MovieResults>() {
//            @Override
//            public void onResponse(Response<MovieResults> response, Retrofit retrofit) {
//
//                if (response.body() == null) return;
//
//                List<MovieItem> results = response.body().results;
//                // Insert the movie data into the database
//                if (results.size() > 0) {
//                    Vector<ContentValues> cVVector = new Vector<>(results.size());
//
//                    for (MovieItem movieItem : results) {
//                        cVVector.add(movieItem.getContentValues());
//                    }
//
//                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
//                    cVVector.toArray(cvArray);
//                    getActivity().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
//
//                }
//                Log.i(LOG_TAG, "Successful loading of " + results.size() + " items.");
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.e(LOG_TAG, "Error retrieving movies: " + t.getMessage());
//                Toast.makeText(getActivity(), "Cannot load movies...", Toast.LENGTH_SHORT).show();
//            }
//        });
