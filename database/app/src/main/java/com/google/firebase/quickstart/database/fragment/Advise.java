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
import com.google.firebase.quickstart.database.utilities.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class Advise extends Fragment {

    public  Button mButton;
    private Bitmap mResult;
    private DatabaseReference mDb;
    private ImageView mImageView;
    private StorageReference mStorage;
    private StorageReference mNode;
    private String mNodePath;
    public  TextView mTextView;
    private File mLocalFile;




    public Advise() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.advise, container, false);
        Log.d("ADVISE", "the logger is working");
        mDb = FirebaseDatabase.getInstance().getReference();

        DatabaseReference node = mDb.child("user-posts");

        //read the node -> I get a datasnapshot, which has many children.
        ValueEventListener l = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> van = dataSnapshot.getChildren();
                String person = "";
                for(DataSnapshot user : van) {
                    if(Util.getUid().equals(user.getKey())) {
                        person = person + ", " + user.getKey();
                        Log.d("CHECKPOINT", user.getKey());
                    }
                    Log.d("CHECKPOINT", user.getKey());
                    Log.d("CHECKPOINT", Util.getUid());
                    Log.d("CHECKPOINT", String.valueOf(Util.getUid().equals(user.getKey())));
                }


                mTextView.setText(person + ", " + Util.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        node.addValueEventListener(l);
        //iterate over the children
        //
        //call getKey on each of the children, add to a collection
        //
        //display the collection in the main screen.

        mImageView = rootView.findViewById(R.id.advise_picture);
        mTextView = rootView.findViewById(R.id.intro);
        mStorage = FirebaseStorage.getInstance().getReference();
        mNode = mStorage.child("s1feNUUH08grUcZTtZvRqzstOvm2").child("-LLrvsPm9y2Zkke60-lr").child("one");
        mNodePath = mNode.getPath();

//        mButton.setVisibility(View.INVISIBLE);

        ValueEventListener listen = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mResult = dataSnapshot.getValue(Bitmap.class);
                mImageView.setImageBitmap(mResult);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mLocalFile = null;
        try {
            mLocalFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }


        Util.downloadImage(mNode, mImageView);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}

