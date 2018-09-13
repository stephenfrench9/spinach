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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
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

public class Advise extends Fragment implements View.OnClickListener {

    public DatabaseReference mDb;
    public String mPostKey;
    private StorageReference mStorage;
    private ImageView mThat;
    private ImageView mThis;
    private Button mThatButton;
    private Button mThisButton;


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
                String two = "";
                for(DataSnapshot post : van) { //a for loop over one thing.
                    mPostKey = post.getKey().toString();
                    mDb.child("posts").child(mPostKey).child(getUid()).setValue(333);
                    one = (String) post.child("addressOne").getValue();
                    two = (String) post.child("addressTwo").getValue();

                }
                String[] address = one.split("/");
                String[] address2 = two.split("/");
                if(!one.equals("")) {
                    StorageReference node = mStorage.child(address[0]).child(address[1]).child(address[2]);
                    downloadImage(node, mThis);
                }
                if(!two.equals("")){
                    StorageReference node2 = mStorage.child(address2[0]).child(address2[1]).child(address2[2]);
                    downloadImage(node2, mThat);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        node.addValueEventListener(l);
        Query aNode = mDb.child("posts").orderByChild(getUid()).equalTo(null).limitToFirst(1);
        aNode.addListenerForSingleValueEvent(m);
        mThis = rootView.findViewById(R.id.option1);
        mThat = rootView.findViewById(R.id.option2);
        mStorage = FirebaseStorage.getInstance().getReference();
        mThisButton = rootView.findViewById(R.id.This);
        mThatButton = rootView.findViewById(R.id.That);
        mThisButton.setOnClickListener(this);
        mThatButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        Log.d("HUMOR", "BUTTON PUSHED.");
        DatabaseReference post = mDb.child("posts").child(mPostKey);
        switch(view.getId()) {
            case R.id.This:
                Log.d("HUMOR", "the this button has been pushed");
                post.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Post p = mutableData.getValue(Post.class);
                        String bess = p.author;
                        Log.d("HUMOR", "mThisButton. doTransaction():");
                        Log.d("HUMOR", bess);
                        Log.d("HUMOR", "Here is the value for one: " + String.valueOf(p.one));
                        p.one += 1;
                        mutableData.setValue(p);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        Log.d("CHECKPOINT", "transaction 2 for this is finished. Error: " + databaseError);
                    }
                });
                break;
            case R.id.That:
                Log.d("HUMOR", "The That button has been pushed");
                post.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Post p = mutableData.getValue(Post.class);
                        Log.d("HUMOR", "mThatButton. doTransaction(): we are inserted inserted");
                        p.two += 1;
                        mutableData.setValue(p);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        Log.d("CHECKPOINT", "transaction 2 for this is finished. Error: " + databaseError);
                    }
                });
                break;
        }
    }



}




