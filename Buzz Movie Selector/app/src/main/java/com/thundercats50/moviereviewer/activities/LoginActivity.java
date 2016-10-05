package com.thundercats50.moviereviewer.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thundercats50.moviereviewer.R;
import com.thundercats50.moviereviewer.database.BlackBoardConnector;
import com.thundercats50.moviereviewer.models.UserManager;
import com.thundercats50.moviereviewer.models.User;
import com.thundercats50.moviereviewer.database.BlackBoardConnector.UserStatus;



import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private boolean b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_password || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (!haveNetworkConnection()) {
            mEmailView.setError(getString(R.string.no_internet));
            focusView = mEmailView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            Log.d("Debug", "Reached 2");

        } else if (email.equals("admin@email.com") && password.equals("password")) {

            showProgress(true);
            startActivity(new Intent(this, AdminActivity.class));
            finish();

        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
            try {
                UserStatus s = mAuthTask.get();
                if (!s.equals(UserStatus.VERIFIED)) {
                    if (s.equals(UserStatus.INTERRUPTED_BY_INTERNET)) {
                        mEmailView.setError(getString(R.string.no_internet));
                        focusView = mEmailView;
                    } else if (s.equals(UserStatus.BANNED)) {
                        mEmailView.setError(getString(R.string.account_banned));
                        focusView = mEmailView;
                    } else if (s.equals(UserStatus.BAD_USER)) {
                        mEmailView.setError(getString(R.string.no_user));
                        focusView = mEmailView;
                    } else if (s.equals(UserStatus.LOCKED)) {
                        mEmailView.setError(getString(R.string.account_locked));
                        focusView = mEmailView;
                    } else {
                        mEmailView.setError(getString(R.string.no_internet));
                        focusView = mEmailView;
                    }
                    cancel = true;
                }
            } catch (Exception e) {
                Log.d("Task Error", "Cannot create logged in view.");
            }

            if (!cancel) {

                startActivity(new Intent(this, LoggedInActivity.class));
                //delete the current users info as you move up stack
                //as security measure
                finish();
            }
        }
    }

    public void cancel(View view) {
        startActivity(new Intent(this, WelcomeActivity.class));
    }

    private boolean isEmailValid(String email) {
        return (email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")
                && email.length() >= 6);
    }

    private boolean isPasswordValid(String password) {
        return (password.matches("[a-zA-Z0-9]+") && password.length() >= 6);
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, UserStatus> {

        private final String mEmail;
        private final String mPassword;
        private String mGender;
        private String mFirstName;
        private String mLastName;
        private String mMajor;
        private final UserManager manager = (UserManager) getApplicationContext();
        private boolean internetAccessExists = true;
        private boolean userVerified;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            userVerified = false;
            internetAccessExists = true;
        }

        @Override
        protected UserStatus doInBackground(Void...params) {
            BlackBoardConnector bbc = null;
            UserStatus retVal = UserStatus.INTERRUPTED_BY_INTERNET;
            try {
                bbc = new BlackBoardConnector();
                retVal = bbc.verifyUser(mEmail, mPassword);
                Log.d("DB verifyUser Called", "doInBackground method returned: "
                        + retVal);
                if (!retVal.equals(UserStatus.VERIFIED)) {
                    bbc.disconnect();
                    return retVal;
                }
                bbc.resetLoginAttempts(mEmail);
                ResultSet userInfo = bbc.getUserData(mEmail);
                userInfo.next(); //must call next to move to first entry
                mFirstName = userInfo.getString(1);
                mLastName = userInfo.getString(2);
                mMajor = userInfo.getString(3);
                mGender = userInfo.getString(4);
                manager.setCurrentMember(new User(mEmail, mFirstName, mLastName,
                            mMajor, mGender));
            } catch (SQLException sqle) {
                Log.d("Connection Error", "Check internet for MySQL access." + sqle.getMessage() + sqle.getSQLState());
                cancel(true);
                return UserStatus.INTERRUPTED_BY_INTERNET;
            } finally {
                bbc.disconnect();
                return retVal;
            }
        }

        @Override
        protected void onPostExecute(final UserStatus success) {
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

