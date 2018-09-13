package com.google.firebase.quickstart.database.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.quickstart.database.models.Post;
import com.google.firebase.quickstart.database.utilities.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static com.google.firebase.quickstart.database.utilities.Util.downloadImage;
import static com.google.firebase.quickstart.database.utilities.Util.getUid;

public class Advise extends Fragment {

    private DatabaseReference mDb;
    private ImageView mImageView;
    private StorageReference mStorage;
    public  TextView mTextView;


    public Advise() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.advise, container, false);
        mDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference node = mDb.child("user-posts");
        ValueEventListener l = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> van = dataSnapshot.getChildren();
                String person = "";
                for(DataSnapshot user : van) {
                    if(getUid().equals(user.getKey())) {
                        person = person + ", " + user.getKey();
                        Log.d("CHECKPOINT", user.getKey());
                    }
                    Log.d("CHECKPOINT", user.getKey());
                    Log.d("CHECKPOINT", getUid());
                    Log.d("CHECKPOINT", String.valueOf(getUid().equals(user.getKey())));
                }


//                mTextView.setText(person + ", " + getUid());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ValueEventListener m = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> van = dataSnapshot.getChildren();
                String one = "";
                String postKey = "";
                for(DataSnapshot post : van) { //a for loop over one thing.
                    postKey = post.getKey().toString();
                    mDb.child("posts").child(postKey).child(getUid()).setValue(333);
                    one = (String) post.child("addressOne").getValue();
                }

                String addressS = "";
                for(String location : one.split("/")) {
                    addressS = addressS + "\n" + location;
                }
                Log.d("CHECKPOINT", "onCreateView(), onDataChange(): " + addressS);
                mTextView.setText(addressS);
                String[] address = one.split("/");

                if(!one.equals("")) {
                    StorageReference node = mStorage.child(address[0]).child(address[1]).child(address[2]);
                    downloadImage(node, mImageView);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        node.addValueEventListener(l);
        Query aNode = mDb.child("posts").orderByChild(getUid()).equalTo(null).limitToFirst(1);
        aNode.addListenerForSingleValueEvent(m);
        mImageView = rootView.findViewById(R.id.advise_picture);
        mTextView = rootView.findViewById(R.id.intro);
        mStorage = FirebaseStorage.getInstance().getReference();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

