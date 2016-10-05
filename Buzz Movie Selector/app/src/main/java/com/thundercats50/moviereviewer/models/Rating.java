package com.thundercats50.moviereviewer.models;

/**
 * Created by neilbarooah on 13/03/2016.
 */
public class Rating {
    private String user, textReview;
    private int numericalRating;
    private long movieId;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTextReview() {
        return textReview;
    }

    public void setTextReview(String textReview) {
        this.textReview = textReview;
    }

    public int getNumericalRating() {
        return numericalRating;
    }

    public void setNumericalRating(int numericalRating) {
        this.numericalRating = numericalRating;
    }

    public void setMovieId(long id) {
        movieId = id;
    }

    public long getMovieId() {
        return movieId;
    }

}
