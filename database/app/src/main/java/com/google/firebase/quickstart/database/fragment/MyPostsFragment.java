package com.google.firebase.quickstart.database.fragment;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {

    public MyPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        Query q =  databaseReference.child("user-posts")
                .child(getUid());

//        Query q =  databaseReference.child("posts")
//                .orderByChild(getUid());
        Log.d("RPF", "we have inserted to directly below the query.");
        Log.d("RPF", "The user is: " + getUid());
        return q;
    }
}
