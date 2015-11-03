package com.example.android.movies.api.results;

import com.example.android.movies.models.MovieItem;

import java.util.ArrayList;

/**
 * Created by alitabet on 11/2/15.
 */
public class MovieResults {
    public int page;
    public ArrayList<MovieItem> results;
    public int total_pages;
    public int total_results;
}
