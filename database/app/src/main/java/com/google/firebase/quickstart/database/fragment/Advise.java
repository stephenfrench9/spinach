package com.google.firebase.quickstart.database.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class Advise extends Fragment implements View.OnClickListener {

    private StorageReference storage;
    private StorageReference node;
    private TextView mTextView;
    private String nodePath;
    private Bitmap result;
    private ImageView mImageView;

    public Advise() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.advise, container, false);
        mImageView = rootView.findViewById(R.id.advise_picture);
        mTextView = rootView.findViewById(R.id.intro);
        storage = FirebaseStorage.getInstance().getReference();
        node = storage.child("s1feNUUH08grUcZTtZvRqzstOvm2").child("-LLrvsPm9y2Zkke60-lr").child("one");
        nodePath=node.getPath();
        ValueEventListener listen = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result = dataSnapshot.getValue(Bitmap.class);
                mImageView.setImageBitmap(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        node.getFile(localFile);
        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
        mImageView.setImageBitmap(bitmap);

        mTextView.setText("This is some text");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.advise, container, false);
    }

    @Override
    public void onClick(View view) {
        mTextView.setText("now we are setted?");
    }

}

