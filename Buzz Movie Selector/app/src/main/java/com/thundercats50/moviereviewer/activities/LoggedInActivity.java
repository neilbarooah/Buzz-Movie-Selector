package com.thundercats50.moviereviewer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.thundercats50.moviereviewer.R;
import com.thundercats50.moviereviewer.models.UserManager;
import com.thundercats50.moviereviewer.models.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loggedin);

        refillForms();
    }

    private void refillForms() {
        EditText firstname = (EditText) findViewById(R.id.profileFirstName);
        EditText lastname = (EditText) findViewById(R.id.profileLastName);
        EditText email = (EditText) findViewById(R.id.profileEmail);
        EditText gender = (EditText) findViewById(R.id.profileGender);
        EditText major = (EditText) findViewById(R.id.profileMajor);

        UserManager manager = (UserManager) getApplicationContext();
        User user = manager.getCurrentMember();
        firstname.setText(user.getFirstname());
        lastname.setText(user.getLastname());
        email.setText(user.getEmail());
        gender.setText(user.getGender());
        major.setText(user.getMajor());

        firstname.setKeyListener(null);
        lastname.setKeyListener(null);
        email.setKeyListener(null);
        gender.setKeyListener(null);
        major.setKeyListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refillForms();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserManager manager = (UserManager) getApplicationContext();
        manager.setCurrentMember(new User("",""));
        //delete the current users info as you move up stack
        //as security measure
    }



    public void logout(View view) {
        startActivity(new Intent(this, WelcomeActivity.class));
    }

    public void editProfile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void goToSearch(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void getRecommendation(View view) { startActivity(new Intent(this, RecommendationActivity.class)); }
}
