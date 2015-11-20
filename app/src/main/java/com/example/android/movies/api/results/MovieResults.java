package com.example.android.movies.api.results;

import com.example.android.movies.models.MovieItem;

import java.util.ArrayList;

/**
 * Class to store JSON results from Movies Discover
 *
 * @author Ali K Thabet
 */
public class MovieResults {
    public int page;
    public ArrayList<MovieItem> results;
    public int total_pages;
    public int total_results;
}
