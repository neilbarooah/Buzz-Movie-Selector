package com.thundercats50.moviereviewer.listview;

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

public class MovieFragment extends Fragment{
    private static final String TAG = "RecyclerViewExample";
    private List<SingleMovie> movieList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MovieListAdapter adapter;

    private int position = 0;
    private String count;
    private String after_id;

    //Possibly useful for JSON query: (Originally reddit JSON queries)
    private int pageCount = 1;
    private static final String key = "?apikey=yedukp76ffytfuy24zsqk7f5";
    private static final String baseURL = "http://api.rottentomatoes.com/api/public/v1.0/";
    private final String jsonEnd = "&page=";
    private String nearCompleteCall;

    private ProgressDialog progressDialog;

    public MovieFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        //Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

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
                loadMore(nearCompleteCall + jsonEnd + pageCount);
            }
        });


        //Useful to keep if we want to add buttons to the fragment itself:
//        Button mButton = (Button) rootView.findViewById(R.id.back_button);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.fragSubreddit = gaming;
//                reloadFragment();
//            }
//        });
//
//        Button nButton = (Button) rootView.findViewById(R.id.reviews_button);
//        nButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.fragSubreddit = pics;
//                reloadFragment();
//
//            }
//        });

        return rootView;
    }

    /**
     * Method to search movie by genre and then populate onto recycler view
     * @param genre genre of the movie
     */
    public void searchByGenre(String genre) {
        //TODO: Implementation of searching by genre
    }

    /**
     * Method to search movie by major and then populate onto recycler view
     * @param major of the user
     */
    public void searchByMajor(String major) {
        GetReviewsTask task = new GetReviewsTask(mRecyclerView, adapter, major);
        task.execute();
    }



    /**
     * This method runs the query and creates and puts the initial SingleMovie objects into movieList
     * @param type determines the type of query. 0 for user query, 1 for new releases or DVDs
     * @param subkey the string associated with the query
     */
    public void updateList(int type, String subkey) {

        // build the API call based on the type. 0 = regular query, 1 = special query
        if (type == 0) {
            nearCompleteCall = baseURL + "movies.json" + key + "&q=" + subkey;
        } else {
            nearCompleteCall = baseURL + subkey + key;
        }

        // "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=yedukp76ffytfuy24zsqk7f5&q=Superman&page_limit=10";

        //declare the adapter and attach it to the recyclerview
        adapter = new MovieListAdapter(getActivity(), movieList);
        mRecyclerView.setAdapter(adapter);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());


        // Clear the adapter because new data is being added from a new subkey
        adapter.clearAdapter();

        showPD();

        runQuery(queue, nearCompleteCall + jsonEnd + pageCount);
    }

    /**
     * Loads more SingleMovie objects into the movieList as needed as the user scrolls
     * @param query the full API call
     */
    public void loadMore(String query) {

        // Declare the adapter and attach it to the recycler-view
        adapter = new MovieListAdapter(getActivity(), movieList);
        mRecyclerView.setAdapter(adapter);

        showPD();


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        runQuery(queue, query);
    }

    /**
     * The meat of the JSON handling. Takes the API call, gets the JSON response, parses it,
     * creates a movie object, and sets its Title and Thumbnail attributes
     * @param queue the volley queue
     * @param query the API call
     */
    private void runQuery(RequestQueue queue, String query) {
        Log.d(TAG, query);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, query, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());
                hidePD();

                // Parse json data.
                // Declare the json objects that we need and then for loop through the children array.
                // Do the json parse in a try catch block to catch the exceptions
                try {
                    //JSONObject data = response.getJSONObject();
                    //after_id = data.getString("after");
                    JSONArray arrayTitles = response.getJSONArray("movies");
                    for (int i = 0; i < arrayTitles.length(); i++) {
                        // thumbnails is a subobject of movies, so need separate object to handle
                        JSONObject currentMovie = arrayTitles.getJSONObject(i);
                        JSONObject thumbnails = currentMovie.getJSONObject("posters");
                        SingleMovie item = new SingleMovie();
                        item.setTitle(currentMovie.getString("title"));
                        item.setId(currentMovie.getLong("id"));
                        item.setSynopsis(currentMovie.getString("synopsis"));
                        //Log.e("Is this value null", "The value is " + thumbnails.getString("thumbnail"));
                        item.setThumbnailURL(thumbnails.getString("thumbnail"));
//                        item.setCriticReview(currentMovie.getString("critics_consensus"));
//                        item.setMpaaRating(currentMovie.getString("mpaa_rating"));
                        // pass the url of the thumbnail to the ImageDownloader
                        new ImageDownloader(item).execute(thumbnails.getString("thumbnail"));
                        movieList.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Update list by notifying the adapter of changes
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePD();
            }
        });

        queue.add(jsObjRequest);
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


    /**
     * Handles asynchronously downloading the thumbnail bitmap and sets it as the thumbnail
     * of the SingleMovie object
     */
    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        SingleMovie movie;

        public ImageDownloader(SingleMovie movie) {
            this.movie = movie;
        }

        /**
         * asyncronously gets the Bitmap from the url and returns it
         * @param url the thumbnail url
         * @return Bitmap thumbnail
         */
        protected Bitmap doInBackground(String... url) {
            String urldisplay = url[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", " "+e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        /**
         * set the thumbnail of the SingleMovie to the returned bitmap
         * @param result the returned bitmap from doInBackground
         */
        protected void onPostExecute(Bitmap result) {
            movie.setThumbnail(result);
        }
    }

    private class GetReviewsTask extends AsyncTask<Void, Void, HashSet<SingleMovie>> {

        private RecyclerView mRecyclerView;
        private MovieListAdapter adapter;
        private String major;

        public GetReviewsTask(RecyclerView mRecyclerView, MovieListAdapter adapter, String major) {
            this.mRecyclerView = mRecyclerView;
            this.adapter = adapter;
            this.major = major;
        }

        @Override
        protected HashSet<SingleMovie> doInBackground(Void... params) {
            return searchByMajor();
        }

        @Override
        protected void onPostExecute(final HashSet<SingleMovie> result) {
            //declare the adapter and attach it to the recyclerview
            adapter = new MovieListAdapter(getActivity(), movieList);
            mRecyclerView.setAdapter(adapter);
            adapter.clearAdapter();
            Iterator<SingleMovie> iterator = result.iterator();
            while(iterator.hasNext()) {
                SingleMovie item = iterator.next();
                //new ImageDownloader(item).execute(item.getThumbnailURL());
                movieList.add(item);
            }
            adapter.notifyDataSetChanged();
        }

        public HashSet<SingleMovie> searchByMajor() {
            try {
                RepositoryConnector rpc = new RepositoryConnector();
                HashSet<SingleMovie> result = rpc.getAllByMajor(major);
                rpc.disconnect();
                return result;
            } catch(Exception e) {
                Log.d("DB_Exception", e.getMessage());
                return null;
            }
        }
    }
}