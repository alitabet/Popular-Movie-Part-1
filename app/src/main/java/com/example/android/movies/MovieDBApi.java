package com.example.android.movies;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by alitabet on 11/2/15.
 */
public interface MovieDBApi {

    @GET("3/discover/movie")
    Call<MovieResults> getMovieResults(@QueryMap Map<String, String> queryMap);
//        @Query("api_key") String key, @Query("sort_by") sortBy);
}
