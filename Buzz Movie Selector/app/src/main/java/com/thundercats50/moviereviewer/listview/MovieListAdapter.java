package com.thundercats50.moviereviewer.listview;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

//If we want to use movie images:
import com.thundercats50.moviereviewer.R;
import com.thundercats50.moviereviewer.activities.LoggedInActivity;
import com.thundercats50.moviereviewer.activities.ReviewActivity;
import com.thundercats50.moviereviewer.activities.WelcomeActivity;
import com.thundercats50.moviereviewer.models.MovieManager;
import com.thundercats50.moviereviewer.models.SingleMovie;
//to remove after integration of rotten tomatoes

/**
 * Created by scottheston on 23/02/16.
 * Consulting tutorial: https://www.youtube.com/watch?v=8ePqYGMxdSY
 *
 * Handles inflating the recycler view with MovieViewHolder objects. Also binds data stored in Movie
 * object to the MovieViewHolder objects
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    //replace all access to movies with information from database
    private List<SingleMovie> movieList;
    private Context mContext;

    private int focusedItem = 0;

    public MovieListAdapter(Context context, List<SingleMovie> listItemsList) {
        this.movieList = listItemsList;
        this.mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        final MovieViewHolder holder = new MovieViewHolder(view);


        holder.recLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int click = holder.getAdapterPosition();
                SingleMovie movie = movieList.get(click);

                MovieManager.movie = movie;

                Intent intent = new Intent(mContext, ReviewActivity.class);
                mContext.startActivity(intent);
            }
        });

        return holder;
    }


    @Override
    public void onBindViewHolder(final MovieViewHolder movieViewHolder, int position) {
        SingleMovie movie = movieList.get(position);
        movieViewHolder.itemView.setSelected(focusedItem == position);

        movieViewHolder.getLayoutPosition();

        //MovieManager.movie = movie;
        movieViewHolder.title.setText(Html.fromHtml(movie.getTitle()));
        movieViewHolder.thumbnail.setImageBitmap(movie.getThumbnail());
        //movieViewHolder.synopsis.setText(Html.fromHtml(movie.getAuthor()));
    }

    public void clearAdapter() {
        movieList.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != movieList ? movieList.size() : 0);
    }
}

