package com.google.firebase.quickstart.database.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class Advise extends Fragment {

    private StorageReference storage;
    private StorageReference node;
    private Bitmap result;
    private ImageView picture;

    public Advise() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        picture = container.findViewById(R.id.advise_picture);
        storage = FirebaseStorage.getInstance().getReference();
        node = storage.child("s1feNUUH08grUcZTtZvRqzstOvm2").child("-LLrvsPm9y2Zkke60-lr").child("one");

        ValueEventListener listen = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result = dataSnapshot.getValue(Bitmap.class);
                picture.setImageBitmap(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };





        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.advise, container, false);
    }

}
