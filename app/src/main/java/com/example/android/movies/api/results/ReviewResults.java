package com.example.android.movies.api.results;

import com.example.android.movies.models.Review;

import java.util.ArrayList;

/**
 * Class to store JSON results from Movies Reviews
 *
 * @author Ali K Thabet
 */
public class ReviewResults {
    public int id;
    public int page;
    public ArrayList<Review> results;
    public int total_pages;
    public int total_results;
}
