package com.google.firebase.quickstart.database.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.quickstart.database.R;
import com.google.firebase.quickstart.database.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public TextView bodyView;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        bodyView = itemView.findViewById(R.id.post_body);
    }

    public void bindToPost(Post post) {
        titleView.setText(post.addressOne);
        authorView.setText(post.author);
        bodyView.setText(post.addressTwo);
    }
}
