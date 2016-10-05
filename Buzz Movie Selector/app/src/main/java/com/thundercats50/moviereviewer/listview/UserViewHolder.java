package com.thundercats50.moviereviewer.listview;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.thundercats50.moviereviewer.R;

import java.io.InputStream;


/**
 * Created by neilbarooah on 23/02/16.
 *
 * Responsible for grabbing all the Views defined in list_row.xml
 */

public class UserViewHolder extends RecyclerView.ViewHolder {
    protected ImageView thumbnail;
    protected TextView title;
    protected RelativeLayout recLayout;


    public UserViewHolder(View view) {
        super(view);
        this.title = (TextView) view.findViewById(R.id.title);
        this.recLayout = (RelativeLayout) view.findViewById(R.id.recLayout);

        view.setClickable(true);
    }
}
