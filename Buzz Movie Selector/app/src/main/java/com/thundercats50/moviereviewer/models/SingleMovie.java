package com.thundercats50.moviereviewer.models;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by neilbarooah on 23/02/16.
 */
public class SingleMovie {

    private String title, mpaaRating, synopsis, criticReview, imageURL;
    private List<String> genres, cast;
    private long id;
    private Integer year, runtime;
    private HashMap<String, Rating> userRatings;

    public SingleMovie() {
        userRatings = new HashMap<>();
    }

    public int getUserRating(String email) {
        return userRatings.get(email).getNumericalRating();
    }

    public boolean hasRatingByUser(String email) {
        return userRatings.containsKey(email);
    }

    public String getUserReview(String email) {
        return userRatings.get(email).getTextReview();
    }
    public void addUserRating(String email, Rating rating) {
        this.userRatings.put(email, rating);
    }

    // holds ImageView for thumbnail
    protected Bitmap thumbnail;

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getThumbnailURL() { return imageURL; }

    public void setThumbnailURL(String imageURL) {
        this.imageURL = imageURL;
    }


    public Bitmap getThumbnail() { return thumbnail; }

    public void setThumbnail(Bitmap image) {
        thumbnail = image;
    }

    public String getMpaaRating() { return mpaaRating; }

    public void setMpaaRating(String mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setGenres(List<String> list) {
        for (String genre : list) {
            genres.add(genre);
        }
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setCast(List<String> list) {
        for (String actor : list) {
            cast.add(actor);
        }
    }

    public List<String> getCast() {
        return cast;
    }

    public void setCriticReview(String criticReview) {
        this.criticReview = criticReview;
    }

    public String getCriticReview() {
        return criticReview;
    }

    public void setId(long id) { this.id = id; }

    public long getId() {
        return id;
    }

    public boolean equals(Object object) {
        if (!(object instanceof SingleMovie)) {
            return false;
        }
        SingleMovie movie = (SingleMovie) object;
        if (movie.getId() == id) {
            return true;
        }
        return false;
    }

}
