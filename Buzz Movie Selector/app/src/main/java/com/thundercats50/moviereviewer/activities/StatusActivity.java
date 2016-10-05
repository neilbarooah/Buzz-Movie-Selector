package com.thundercats50.moviereviewer.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.thundercats50.moviereviewer.R;
import com.thundercats50.moviereviewer.database.BlackBoardConnector;
import com.thundercats50.moviereviewer.models.UserManager;
import com.thundercats50.moviereviewer.models.User;
import java.sql.SQLException;
import java.util.InputMismatchException;

public class StatusActivity extends AppCompatActivity {

    private User user = UserManager.currentMember;
    private View mReviewFormView;
    private View mProgressView;
    private UserManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        User user = UserManager.currentMember;
        manager = (UserManager) getApplicationContext();
        //getRating((int) movie.getId(), manager.getCurrentEmail());

        mReviewFormView = findViewById(R.id.status_form);
        mProgressView = findViewById(R.id.status_progress);

        Button mBanButton = (Button) findViewById(R.id.ban);
        mBanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                banUser();
            }
        });

        Button mUnbanButton = (Button) findViewById(R.id.unban);
        mUnbanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbanUser();
            }
        });

        Button mUnlockButton = (Button) findViewById(R.id.unlock);
        mUnlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlockUser();
            }
        });



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

            mReviewFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mReviewFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mReviewFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mReviewFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void banUser() {
        showProgress(true);
        BanUserTask banTask = new BanUserTask(manager);
        try {
            boolean successfulFinish = banTask.execute().get();
            Log.d("Returned:", Boolean.toString(successfulFinish));
            if (successfulFinish) {
                finish();
            } else {
                showProgress(false);
            }
        } catch (Exception e) {
            Log.d("Write to DB", "Concurrent exception in ReviewAct");
        }
    }

    public class BanUserTask extends AsyncTask<Void, Void, Boolean> {
        private UserManager manager;
        private Exception error;

        BanUserTask(UserManager manager) {
            this.manager = manager;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            error = null;
            return banUser();
        }

        public Boolean banUser() {
            BlackBoardConnector bbc;
            boolean retVal = false;
            try {
                bbc = new BlackBoardConnector();
                UserManager manager = (UserManager) getApplicationContext();
                String email = manager.getCurrentMember().getEmail();

                retVal = bbc.setBanned(email, true);
                Log.d("DB setBanned Finished", "doInBackground method returned: "
                        + Boolean.toString(retVal));
                bbc.disconnect();
                return retVal;
            } catch (InputMismatchException imee) {
                error = imee;
            } catch (SQLException sqle) {
                error = sqle;
                Log.d("Connection Error", "Check internet for MySQL access." + sqle.getMessage() + sqle.getSQLState());
                for (Throwable e : sqle) {
                    e.printStackTrace(System.err);
                    Log.d("Connection Error", "SQLState: " +
                            ((SQLException) e).getSQLState());

                    Log.d("Connection Error", "Error Code: " +
                            ((SQLException) e).getErrorCode());

                    Log.d("Connection Error", "Message: " + e.getMessage());

                    Throwable t = sqle.getCause();
                    while (t != null) {
                        Log.d("Connection Error", "Cause: " + t);
                        t = t.getCause();
                    }
                }
            } finally {
                return retVal;
            }
        }


    }

    public void unlockUser() {
        showProgress(true);
        UnlockUserTask unlockTask = new UnlockUserTask(manager);
        try {
            boolean successfulFinish = unlockTask.execute().get();
            Log.d("Returned:", Boolean.toString(successfulFinish));
            if (successfulFinish) {
                finish();
            } else {
                showProgress(false);
            }
        } catch (Exception e) {
            Log.d("Write to DB", "Concurrent exception in ReviewAct");
        }
    }

    public class UnlockUserTask extends AsyncTask<Void, Void, Boolean> {
        private UserManager manager;
        private Exception error;

        UnlockUserTask(UserManager manager) {
            this.manager = manager;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            error = null;
            return unlockUser();
        }

        public Boolean unlockUser() {
            BlackBoardConnector bbc;
            boolean retVal = false;
            try {
                bbc = new BlackBoardConnector();
                UserManager manager = (UserManager) getApplicationContext();
                String email = manager.getCurrentMember().getEmail();

                retVal = bbc.resetLoginAttempts(email);
                Log.d("DB reset Finished", "doInBackground method returned: "
                        + Boolean.toString(retVal));
                bbc.disconnect();
                return retVal;
            } catch (InputMismatchException imee) {
                error = imee;
            } catch (SQLException sqle) {
                error = sqle;
                Log.d("Connection Error", "Check internet for MySQL access." + sqle.getMessage() + sqle.getSQLState());
                for (Throwable e : sqle) {
                    e.printStackTrace(System.err);
                    Log.d("Connection Error", "SQLState: " +
                            ((SQLException) e).getSQLState());

                    Log.d("Connection Error", "Error Code: " +
                            ((SQLException) e).getErrorCode());

                    Log.d("Connection Error", "Message: " + e.getMessage());

                    Throwable t = sqle.getCause();
                    while (t != null) {
                        Log.d("Connection Error", "Cause: " + t);
                        t = t.getCause();
                    }
                }
            } finally {
                return retVal;
            }
        }
    }

    public void unbanUser() {
        showProgress(true);
        UnbanUserTask unbanTask = new UnbanUserTask(manager);
        try {
            boolean successfulFinish = unbanTask.execute().get();
            Log.d("Returned:", Boolean.toString(successfulFinish));
            if (successfulFinish) {
                finish();
            } else {
                showProgress(false);
            }
        } catch (Exception e) {
            Log.d("Write to DB", "Concurrent exception in ReviewAct");
        }
    }

    public class UnbanUserTask extends AsyncTask<Void, Void, Boolean> {
        private UserManager manager;
        private Exception error;

        UnbanUserTask(UserManager manager) {
            this.manager = manager;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            error = null;
            return unbanUser();
        }

        public Boolean unbanUser() {
            BlackBoardConnector bbc;
            boolean retVal = false;
            try {
                bbc = new BlackBoardConnector();
                UserManager manager = (UserManager) getApplicationContext();
                String email = manager.getCurrentMember().getEmail();

                retVal = bbc.setBanned(email, false);
                Log.d("DB setBanned Finished", "doInBackground method returned: "
                        + Boolean.toString(retVal));
                bbc.disconnect();
                return retVal;
            } catch (InputMismatchException imee) {
                error = imee;
            } catch (SQLException sqle) {
                error = sqle;
                Log.d("Connection Error", "Check internet for MySQL access." + sqle.getMessage() + sqle.getSQLState());
                for (Throwable e : sqle) {
                    e.printStackTrace(System.err);
                    Log.d("Connection Error", "SQLState: " +
                            ((SQLException) e).getSQLState());

                    Log.d("Connection Error", "Error Code: " +
                            ((SQLException) e).getErrorCode());

                    Log.d("Connection Error", "Message: " + e.getMessage());

                    Throwable t = sqle.getCause();
                    while (t != null) {
                        Log.d("Connection Error", "Cause: " + t);
                        t = t.getCause();
                    }
                }
            } finally {
                return retVal;
            }
        }

    }
}
