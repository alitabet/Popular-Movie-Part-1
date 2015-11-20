package com.example.android.movies.api;

import com.example.android.movies.api.results.MovieResults;
import com.example.android.movies.api.results.ReviewResults;
import com.example.android.movies.api.results.TrailerResults;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Retrofit API implementation for MoviesDB
 *
 * @author Ali K Thabet
 */
public interface MovieDBApi {
    String BASE_URL = "http://api.themoviedb.org/3/";

    @GET("movie/{sort_by}")
    Call<MovieResults> getMovieResults(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewResults> getReviews(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResults> getTrailers(@Path("id") String id, @Query("api_key") String apiKey);
}
