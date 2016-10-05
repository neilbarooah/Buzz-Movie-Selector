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

import com.thundercats50.moviereviewer.R;
import com.thundercats50.moviereviewer.activities.ReviewActivity;
import com.thundercats50.moviereviewer.models.MovieManager;
import com.thundercats50.moviereviewer.models.SingleMovie;
import com.thundercats50.moviereviewer.models.User;
import com.thundercats50.moviereviewer.models.UserManager;
import com.thundercats50.moviereviewer.activities.StatusActivity;

import java.util.List;

import java.util.List;

/**
 * Created by neilbarooah on 17/03/16.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder>{

        //replace all access to movies with information from database
        private List<User> userList;
        private Context mContext;

        private int focusedItem = 0;

        public UserListAdapter(Context context, List<User> listItemsList) {
            this.userList = listItemsList;
            this.mContext = context;
        }

        @Override
        public UserViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
            final UserViewHolder holder = new UserViewHolder(view);


            holder.recLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    int click = holder.getAdapterPosition();
                    User user = userList.get(click);

                    UserManager.currentMember = user;

                    Intent intent = new Intent(mContext, StatusActivity.class);
                    mContext.startActivity(intent);
                }
            });

            return holder;
        }


        @Override
        public void onBindViewHolder(final UserViewHolder userViewHolder, int position) {
            User user = userList.get(position);
            userViewHolder.itemView.setSelected(focusedItem == position);

            userViewHolder.getLayoutPosition();
            userViewHolder.title.setText(user.getEmail());
        }

        public void clearAdapter() {
            userList.clear();
            notifyDataSetChanged();
        }


        @Override
        public int getItemCount() {
            return (null != userList ? userList.size() : 0);
        }
    }


