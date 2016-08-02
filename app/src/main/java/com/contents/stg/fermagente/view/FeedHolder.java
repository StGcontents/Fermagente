package com.contents.stg.fermagente.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.contents.stg.fermagente.R;

public class FeedHolder extends RecyclerView.ViewHolder {

    private TextView commentView, placeView, timeView, starsView;

    public TextView getCommentView() {
        return commentView;
    }

    public TextView getPlaceView() { return placeView; }

    public TextView getTimeView() { return timeView; }

    public TextView getStarsView() { return starsView; }

    public FeedHolder(View itemView) {
        super(itemView);
        commentView = (TextView) itemView.findViewById(R.id.comment);
        placeView = (TextView) itemView.findViewById(R.id.place_text);
        timeView = (TextView) itemView.findViewById(R.id.time_text);
        starsView = (TextView) itemView.findViewById(R.id.star_text);
    }
}
