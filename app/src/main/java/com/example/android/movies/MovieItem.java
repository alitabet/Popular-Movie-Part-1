package com.example.android.movies;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.movies.data.MovieContract;

/**
 * Class <tt>MovieItem</tt> stores a record obtained
 * from the MovieDB API. <tt>MovieItem</tt> implements
 * {@link Parcelable} in order to be able to send
 * a movie item through an intent from the main
 * activity container of all movies, to the detail
 * activity of each movie
 *
 * @author Ali K Thabet
 */
public class MovieItem implements Parcelable {

//    @SerializedName("")
//    @Expose
    private Integer id;         // unique ID for movie
    private String title;       // title of the movie
    private String posterPath;  // URL path to poster image
    private String thumbPath;   // URL path to thumbnail image
    private String releaseDate; // movie release date
    private Double rating;      // movie rating
    private Double popularity;  // popularity of movie in MovieDB
    private String synopsis;    // Movie synopsis

    public MovieItem() {

    }
    public MovieItem(ContentValues contentValues) {
        this.id          = contentValues.getAsInteger(MovieContract.MovieEntry._ID);
        this.title       = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_TITLE);
        this.rating      = contentValues.getAsDouble(MovieContract.MovieEntry.COLUMN_RATING);
        this.popularity  = contentValues.getAsDouble(MovieContract.MovieEntry.COLUMN_POPULARITY);
        this.synopsis    = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
        this.posterPath  = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_POSTER);
        this.thumbPath   = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_THUMB);
        this.releaseDate = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
    }
    public MovieItem(Cursor cursor) {
        this.id          = cursor.getInt(MovieContract.MovieEntry.COL_MOVIE_ID);
        this.title       = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
        this.rating      = cursor.getDouble(MovieContract.MovieEntry.COL_MOVIE_RATING);
        this.popularity  = cursor.getDouble(MovieContract.MovieEntry.COL_MOVIE_POPULARITY);
        this.synopsis    = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_SYNOPSIS);
        this.posterPath  = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER);
        this.thumbPath   = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_THUMB);
        this.releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE);
    }
    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        if (synopsis == "null") this.synopsis = "No synopsis available";
        else                  this.synopsis = synopsis;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public ContentValues getContentValues() {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.MovieEntry._ID,                 this.getId());
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE,        this.getTitle());
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING,       this.getRating());
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,   this.getPopularity());
        movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS,     this.getSynopsis());
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER,       this.getPosterPath());
        movieValues.put(MovieContract.MovieEntry.COLUMN_THUMB,        this.getThumbPath());
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, this.getReleaseDate());

        return movieValues;
    }

//    public void getMovieFromContentValues(ContentValues contentValues) {
//        id          = contentValues.getAsInteger(MovieContract.MovieEntry._ID);
//        title       = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_TITLE);
//        rating      = contentValues.getAsDouble(MovieContract.MovieEntry.COLUMN_RATING);
//        popularity  = contentValues.getAsDouble(MovieContract.MovieEntry.COLUMN_POPULARITY);
//        synopsis    = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
//        posterPath  = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_POSTER);
//        thumbPath   = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_THUMB);
//        releaseDate = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
//    }
    // methods to override for Parcelable:

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(thumbPath);
        dest.writeString(releaseDate);
        dest.writeDouble(rating);
        dest.writeDouble(popularity);
        dest.writeString(synopsis);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR
            = new Parcelable.Creator<MovieItem>() {
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    public MovieItem(Parcel in) {
        id          = in.readInt();
        title       = in.readString();
        posterPath  = in.readString();
        thumbPath   = in.readString();
        releaseDate = in.readString();
        rating      = in.readDouble();
        popularity  = in.readDouble();
        synopsis    = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieItem movieItem = (MovieItem) o;

        return title.equals(movieItem.title);

    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }
}