package com.example.android.movies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alitabet on 11/2/15.
 */
public class MovieResults {
//    public static final Map<String, String> QUERY_MAP = new HashMap<>();
//    static {
//        QUERY_MAP.put("api_key", "apiKey");
//        QUERY_MAP.put("sort_by", "sortBy");
//    }

    private List<MovieItem> results = new ArrayList<>();

    public List<MovieItem> getResults() {
        return results;
    }

    public void setResults(List<MovieItem> results) {
        this.results = results;
    }
}
