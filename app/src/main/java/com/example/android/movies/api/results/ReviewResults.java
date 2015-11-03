package com.example.android.movies.api.results;

import com.example.android.movies.models.Review;

import java.util.ArrayList;

/**
 * Created by thabetak on 11/3/2015.
 */
public class ReviewResults {
    public int id;
    public int page;
    public ArrayList<Review> results;
    public int total_pages;
    public int total_results;
}
