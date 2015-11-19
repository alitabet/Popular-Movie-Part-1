package com.example.android.movies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.R;
import com.example.android.movies.data.MovieContract;
import com.example.android.movies.models.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Custom adapter to create image views. This adaptor
 * is used to attach to a GridView in the current project.
 * <em>DetailAdapter</em> extends {@link ArrayAdapter}
 * in order to access the <em>add</em> and <em>clear</em>
 * methods
 */
public class DetailAdapter extends ArrayAdapter<String> {
    private final String LOG_TAG = DetailAdapter.class.getSimpleName();

    private final LayoutInflater mInflater;
    /**
     * The resource indicating what views to inflate to display the content of this
     * array adapter.
     */
    private Context mContext;
    private ArrayList<String> mObjects;
    private MovieItem mMovieItem;
    HeaderViewHolder headerViewHolder;
    FooterViewHolder footerViewHolder;
    ViewHolder holder;

    public DetailAdapter(Context context, ArrayList<String> objects) {
        super(context, 0, objects);

        mInflater = LayoutInflater.from(context);
        mContext = context;
        mObjects = objects;
    }

    public void setMovieItem(MovieItem movieItem) {
        this.mMovieItem = movieItem;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        else return  1;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Count of different layouts
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView;

        if (convertView == null) {
            switch (getItemViewType(position)) {
                case 0:
                    rootView = mInflater.inflate(R.layout.fragment_detail_header, parent, false);
                    headerViewHolder = new HeaderViewHolder(rootView);
//                    rootView.setTag(headerViewHolder);
                    break;
                default:
                    rootView = mInflater.inflate(R.layout.trailer_detail_text_view, parent, false);
                    holder = new ViewHolder(rootView);
//                    rootView.setTag(holder);
                    break;
            }
        }
        else {
            switch (getItemViewType(position)) {
                case 0:
                    headerViewHolder = new HeaderViewHolder(convertView);
                    break;
                default:
                    holder = new ViewHolder(convertView);
                    break;
            }
            rootView = convertView;
        }

        String item;

        // check if movie is in favorites table
        Cursor favoriteCursor = queryFavorite();

        if (favoriteCursor.moveToNext()) {
            mMovieItem.setFavorite(true);
        } else {
            mMovieItem.setFavorite(false);
        }

        switch (getItemViewType(position)) {
            case 0:
                // display title, release date, and user rating
                headerViewHolder.titleText.setText(mMovieItem.getTitle());

                String[] date = mMovieItem.getReleaseDate().split("-");
                headerViewHolder.dateText.setText(date[0]);

                headerViewHolder.ratingText.setText(String.format("%.1f", mMovieItem.getRating()) + "/10");

                // fetch thumbnail using thumbnail URL
                String thumbPath = mMovieItem.getPosterPath();

                Picasso.with(mContext).load(thumbPath).into(headerViewHolder.thumbImage);

                // extract synopsis
                headerViewHolder.synopsisText.setText(mMovieItem.getSynopsis());

                headerViewHolder.reviewText.setText(mMovieItem.getReviews());

                headerViewHolder.favoriteBox.setChecked(mMovieItem.isFavorite());

                headerViewHolder.favoriteBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        if (checkBox.isChecked()) {
                            mMovieItem.setFavorite(true);
                            saveFavorite();
                        } else {
                            mMovieItem.setFavorite(false);
                            deleteFavorite();
                        }
                    }
                });

                break;
            default:
                item = mObjects.get(position);
                holder.textView.setText(item);
                break;
        }
        return rootView;
    }

    private Cursor queryFavorite() {
        return mContext.getContentResolver().query(
                MovieContract.MovieFavoriteEntry.buildMovieUri(mMovieItem.getId()),
                null,
                null,
                null,
                null);
    }

    private void saveFavorite() {
        Cursor favorite = queryFavorite();

        if (!favorite.moveToNext()) {
            mContext.getContentResolver().insert(
                    MovieContract.MovieFavoriteEntry.CONTENT_URI,
                    mMovieItem.getContentValues());
        }
    }

    private void deleteFavorite() {
        Cursor favorite = queryFavorite();

        if (favorite.moveToNext()) {
            mContext.getContentResolver().delete(
                    MovieContract.MovieFavoriteEntry.buildMovieUri(mMovieItem.getId()),
                    null,
                    null);
        }
    }

    static class ViewHolder{
        @Bind(R.id.trailer_list_item_textview)
        TextView textView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class HeaderViewHolder {
        @Bind(R.id.title_text)
        TextView titleText;

        @Bind(R.id.date_text)
        TextView dateText;

        @Bind(R.id.rating_text)
        TextView ratingText;

        @Bind(R.id.synopsis_label)
        TextView synopsisLabel;

        @Bind(R.id.synopsis_text)
        TextView synopsisText;

        @Bind(R.id.review_text)
        TextView reviewText;

//        @Bind(R.id.review_label)
//        TextView reviewLabel;
//
//        @Bind(R.id.review_text)
//        TextView reviewText;

//        @Bind(R.id.trailer_label)
//        TextView trailerLabel;

        @Bind(R.id.thumb_imageview)
        ImageView thumbImage;

        @Bind(R.id.favorite_check_box)
        CheckBox favoriteBox;

//        @Bind(R.id.list_item_textview)
//        TextView textView;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class FooterViewHolder {
        @Bind(R.id.review_text)
        TextView reviewText;

        public FooterViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
