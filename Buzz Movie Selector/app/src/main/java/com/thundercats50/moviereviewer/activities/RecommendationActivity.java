package com.thundercats50.moviereviewer.activities;

import android.app.SearchManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.support.v4.app.Fragment;

import com.thundercats50.moviereviewer.listview.MovieFragment;

import com.thundercats50.moviereviewer.R;
import com.thundercats50.moviereviewer.models.UserManager;
import com.thundercats50.moviereviewer.models.User;

public class RecommendationActivity extends AppCompatActivity {

    private MovieFragment movieFragment;
    private String genre;
    private String major;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        Spinner dropdown = (Spinner)findViewById(R.id.genre);
        String[] items = new String[]{"Action & Adventure", "Animation", "Art House & Internationl", "Classics",
                "Comedy", "Drama", "Horror", "Kids & Family", "Mystery & Suspense", "Romance", "Science Fiction & Fantasy",
                "Documentary", "Musical & Performing Arts", "Special Interest", "Sports & Fitness", "Television", "Western"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        this.genre = dropdown.getSelectedItem().toString();

        // get the movie fragment to send the queries to
        movieFragment = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
        UserManager manager = (UserManager) getApplicationContext();
        User user = (User) manager.getCurrentMember();
        major = user.getMajor();

    }

    public void searchByGenre(View view) {
        movieFragment.searchByGenre(genre);
    }

    public void searchByMajor(View view) {movieFragment.searchByMajor(major);}

    public void goHome(View view) {
        startActivity(new Intent(this, LoggedInActivity.class));
    }

}
