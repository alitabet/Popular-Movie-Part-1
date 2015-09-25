package com.example.android.movies;

import android.os.Parcel;
import android.os.Parcelable;

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

    private Integer id; // unique ID for movie
    private String title; // title of the movie
    private String posterPath; // URL path to poster image
    private String thumbPath; // URL path to thumbnail image
    private String releaseDate; // movie release date
    private Double rating; // movie rating
    private String synopsis; // Movie synopsis

    public MovieItem() {

    }
    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
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
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        thumbPath = in.readString();
        releaseDate = in.readString();
        rating = in.readDouble();
        synopsis = in.readString();
    }
}