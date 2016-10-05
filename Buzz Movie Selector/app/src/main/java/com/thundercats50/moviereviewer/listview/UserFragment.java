package com.thundercats50.moviereviewer.listview;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.thundercats50.moviereviewer.R;
import com.thundercats50.moviereviewer.database.BlackBoardConnector;
import com.thundercats50.moviereviewer.models.User;
import com.thundercats50.moviereviewer.listview.UserListAdapter;
import com.thundercats50.moviereviewer.database.RepositoryConnector;
import com.thundercats50.moviereviewer.models.SingleMovie;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thundercats50.moviereviewer.R;
import com.thundercats50.moviereviewer.activities.LoginActivity;
import com.thundercats50.moviereviewer.database.RepositoryConnector;
import com.thundercats50.moviereviewer.models.MovieManager;
import com.thundercats50.moviereviewer.models.SingleMovie;
import com.thundercats50.moviereviewer.database.RepositoryConnector;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
//to remove after MovieSet integration

/**
 * This fragment is representative of the endless recycler view that exists on the search activity.
 * This fragment handles taking the queries from the Search Activity, making ther API call, parsing
 * the JSON returned and storing the relevant data in SingleMovie objects that will be used by the
 * MovieListAdapter to populate the MovieViewHolder objects
 */

public class UserFragment extends Fragment {
    private static final String TAG = "RecyclerViewExample";
    private List<User> userList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private UserListAdapter adapter;

    private int position = 0;
    private String count;
    private String after_id;

    //Possibly useful for JSON query: (Originally reddit JSON queries)
    private int pageCount = 1;
    private String nearCompleteCall;

    private ProgressDialog progressDialog;

    public UserFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        //Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view2);

        // add the line under each row
        // if you are creating a card views, it would be best to delete this decoration
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getActivity())
                        .color(Color.BLACK)
                        .build());

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Keeps track of how far the recycler view has scrolled and updates the list when the end
        // has been reached
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("SCROLL PAST UPDATE", "You hit me");

                //maintain scroll position
                int lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                pageCount++;
                // TODO: loadmore method
            }
        });
        return rootView;
    }


    /**
     * This method runs the query and creates and puts the initial SingleMovie objects into movieList
     * @param type determines the type of query. 0 for user query, 1 for new releases or DVDs
     * @param subkey the string associated with the query
     */
    public void updateList(int type, String subkey) {

        // "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=yedukp76ffytfuy24zsqk7f5&q=Superman&page_limit=10";

        //declare the adapter and attach it to the recyclerview
        adapter = new UserListAdapter(getActivity(), userList);
        mRecyclerView.setAdapter(adapter);

        // Clear the adapter because new data is being added from a new subkey
        adapter.clearAdapter();

        showPD();

        getUserList();
    }

    /**
     * Loads more User objects into the movieList as needed as the user scrolls
     * @param query the full API call
     */
    public void loadMore(String query) {

        // Declare the adapter and attach it to the recycler-view
        adapter = new UserListAdapter(getActivity(), userList);
        mRecyclerView.setAdapter(adapter);

        showPD();

        getUserList();
    }

    /**
     * get user list from DB
     */
    public void getUserList() {
        GetUsersTask task = new GetUsersTask();
        task.execute();
    }

    // Reload the fragment list holding the recyclerviews
    private void reloadFragment() {
        Fragment newFragment = new MovieFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, newFragment);
        transaction.commit();
    }

    private void showPD() {
        if(progressDialog == null) {
            progressDialog  = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    // function to hide the loading dialog box
    private void hidePD() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog= null;
        }
    }

    // Stop app from running
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePD();
    }

    private class GetUsersTask extends AsyncTask<Void, Void, HashSet<User>> {

        @Override
        public HashSet<User> doInBackground(Void... params) {
            return getUsers();
        }

        @Override
        public void onPostExecute(final HashSet<User> set) {
            adapter = new UserListAdapter(getActivity(), userList);
            mRecyclerView.setAdapter(adapter);
            adapter.clearAdapter();
            Iterator<User> iterator = set.iterator();
            while(iterator.hasNext()) {
                User item = iterator.next();
                //new ImageDownloader(item).execute(item.getThumbnailURL());
                userList.add(item);
            }
            adapter.notifyDataSetChanged();
        }

        private HashSet<User> getUsers() {
            try {
                BlackBoardConnector bbc = new BlackBoardConnector();
                HashSet<User> retVal =  bbc.getAllUsers();
                bbc.disconnect();
                return retVal;
            } catch (SQLException e) {
                Log.d("BBC", "Could not retrieve full user list");
            }
            return new HashSet<User>();
        }
    }
}