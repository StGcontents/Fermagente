package com.contents.stg.fermagente.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contents.stg.fermagente.R;
import com.contents.stg.fermagente.ctrl.Observer;
import com.contents.stg.fermagente.model.Post;
import com.contents.stg.fermagente.model.PostCollection;

import java.text.SimpleDateFormat;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedHolder> implements Observer {

    private LayoutInflater inflater;

    public FeedRecyclerAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PostCollection.instance().subscribe(this);
    }

    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_post, parent, false);
        return new FeedHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedHolder holder, int position) {
        Post post = PostCollection.instance().get(position);
        holder.getCommentView().setText(post.getComment());
        holder.getPlaceView().setText(post.getPlace());
        holder.getTimeView().setText(new SimpleDateFormat("hh:mm").format(post.getDate()));
        holder.getStarsView().setText("" + post.getStars());
    }

    @Override
    public int getItemCount() {
        return PostCollection.instance().size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void alert() {
        System.out.println(getItemCount());
        notifyDataSetChanged();
    }
}
