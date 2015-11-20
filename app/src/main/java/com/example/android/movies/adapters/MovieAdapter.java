package com.example.android.movies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.movies.R;
import com.example.android.movies.data.MovieContract;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Custom {@link CursorAdapter} to read movie data
 * from DB and display corresponding move poster
 *
 * @author Ali K Thabet
 */
public class MovieAdapter extends CursorAdapter {
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item_movies, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = new ViewHolder(view);

        String posterPath = cursor.getString(MovieContract.COL_MOVIE_POSTER);
        if (posterPath == null) posterPath = context.getString(R.string.poster_url_alt);

        ImageView imageView = viewHolder.imageView;
        Picasso.with(context).load(posterPath).into(imageView);
    }

    static class ViewHolder{
        @Bind(R.id.grid_item_movies_imageview)
        ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
