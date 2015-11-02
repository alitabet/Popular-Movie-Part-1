package com.example.android.movies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.movies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by thabetak on 11/1/2015.
 */
public class MovieAdapter extends CursorAdapter {
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movies, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view;
        String posterPath = cursor.getString(MovieContract.COL_MOVIE_POSTER);

        if (posterPath == null) posterPath = context.getString(R.string.poster_url_alt);

        Picasso.with(context).load(posterPath).into(imageView);
    }
}
