package com.example.android.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.movies.R;
import com.example.android.movies.models.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Custom adapter to create image views. This adaptor
 * is used to attach to a GridView in the current project.
 * <em>ImageAdapter</em> extends {@link ArrayAdapter}
 * in order to access the <em>add</em> and <em>clear</em>
 * methods
 */
public class ImageAdapter extends ArrayAdapter<MovieItem> {

    private final LayoutInflater mInflater;
    /**
     * The resource indicating what views to inflate to display the content of this
     * array adapter.
     */
    private int mResource;
    private Context mContext;
    private ArrayList<MovieItem> mObjects;

    public ImageAdapter(Context context, int resource, ArrayList<MovieItem> objects) {
        super(context, resource, objects);
        mResource = resource;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mObjects = objects;
    }

    // add object only if does not exist
    @Override
    public void add(MovieItem object) {
        if (!mObjects.contains(object)) {
            super.add(object);
        }
    }

    // add all objects that are not already in list
    @Override
    public void addAll(Collection<? extends MovieItem> collection) {
        ArrayList<MovieItem> temp = new ArrayList<>(collection);
        temp.removeAll(mObjects);
        super.addAll(temp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView;

        if (convertView == null) {
            rootView = mInflater.inflate(mResource, parent, false);
        } else {
            rootView = convertView;
        }

        ViewHolder holder = new ViewHolder(rootView);

        MovieItem item = mObjects.get(position);

        String posterPath = item.getPosterPath();

        if (posterPath == null) posterPath = mContext.getString(R.string.poster_url_alt);

        Picasso.with(mContext).load(posterPath).into(holder.image);

        return rootView;
    }

    static class ViewHolder{
        @Bind(R.id.grid_item_movies_imageview)
        ImageView image;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
