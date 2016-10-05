package com.thundercats50.moviereviewer.activities;

import android.app.SearchManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.view.View;
import android.support.v4.app.Fragment;

import com.thundercats50.moviereviewer.listview.MovieFragment;

import com.thundercats50.moviereviewer.R;

public class SearchActivity extends AppCompatActivity {
    // holds the query the user enters
    private String searchQuery;
    private MovieFragment movieFragment;
    private String newDVDSubkey = "lists/dvds/new_releases.json";
    private String newReleasesSubkey = "lists/movies/in_theaters.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        TextView searchDialogue = (TextView) findViewById(R.id.searchPrompt);
        searchDialogue.setText(getString(R.string.search_hint));
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        // get the movie fragment to send the queries to
        movieFragment = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);

        // Get the intent, verify the action and get the query
        // TODO Have the query show up dynamically in the searchDialogue rather than widget
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            searchQuery = searchQuery.replace(" ", "+");
            movieFragment.updateList(0, searchQuery);
        }
    }

    public void searchNewReleases(View view) {
        movieFragment.updateList(1, newReleasesSubkey);
    }

    public void searchNewDVD(View view) {
        movieFragment.updateList(1, newDVDSubkey);
    }

    public void goHome(View view){
        startActivity(new Intent(this, WelcomeActivity.class));
    }

}
