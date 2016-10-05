package com.thundercats50.moviereviewer.models;

import android.app.Application;

/**
 * Created by neilbarooah on 3/6/2016.
 */
public class MovieManager extends Application{
    public static SingleMovie movie;
    public SingleMovie getCurrentMovie() {
        return movie;
    }
    public void setCurrentMovie(SingleMovie movie) {
        this.movie = movie;
    }

}
