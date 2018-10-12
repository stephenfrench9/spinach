package com.google.firebase.quickstart.database;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.utilities.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;



public class PostDetailActivity extends BaseActivity {

    private static final String TAG = "PostDetailActivity";
    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_POST_OWNER = "post_owner";

    private DatabaseReference mPostReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private String mUserKey;
    private String mPostOwner;
    private ImageView mImageView1;
    private ImageView mImageView2;
    private TextView mTextView1;
    private TextView mTextView2;

    private ValueEventListener mOne;
    private ValueEventListener mTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("silver","onCreate(): start ");

        setContentView(R.layout.activity_post_detail);

        ActionBar greg = getSupportActionBar();
        greg.hide();


        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        mPostOwner = getIntent().getStringExtra(EXTRA_POST_OWNER);
        mUserKey = getUid();

        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = Util.getDatabase().getReference()
                .child("posts").child(mPostKey);

        // Initialize Views
        mImageView1 = findViewById(R.id.databasePicture);
        mImageView2 = findViewById(R.id.databasePicture2);
        mTextView1 = findViewById(R.id.textViewOne);
        mTextView2 = findViewById(R.id.textViewTwo);

        //load the pictures
        StorageReference node = FirebaseStorage.getInstance().getReference().child(mPostOwner).child(mPostKey).child("one");
        StorageReference node2 = FirebaseStorage.getInstance().getReference().child(mPostOwner).child(mPostKey).child("two");
        GlideApp.with(this).load(node).into(mImageView1);
        GlideApp.with(this).load(node2).into(mImageView2);

        //load the votes
        mOne = new VoteListener(mTextView1,"This: ");
        mTwo = new VoteListener(mTextView2, "That: ");

        mPostReference.child("one").addListenerForSingleValueEvent(mOne);
        mPostReference.child("two").addListenerForSingleValueEvent(mTwo);
        Log.d("silver","onCreate(): done ");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("silver", "onConfigurationChanged(): start");
        Toast.makeText(this, "the configuration has changed.", Toast.LENGTH_LONG).show();
        Log.d("silver", "onConfigurationChanged(): end");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("silver", "onStart(): start");
        //get the author from the post
//        mPostListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                String author = dataSnapshot.getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };

//        mPostReference.child("author").addListenerForSingleValueEvent(mPostListener);
        Log.d("silver", "onStart(): end");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("silver", "onStop(): start");
//        mPostReference.child("one").removeEventListener(mOne);
//        mPostReference.child("two").removeEventListener(mTwo);
        Log.d("silver", "onStop(): end");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("silver", "onDestroy(): start");
        Log.d("silver", "onDestroy(): end");
    }

    class VoteListener implements ValueEventListener {
        private TextView tvi;
        private String id;
        VoteListener(TextView t, String i) {
            tvi = t;
            id = i;
        }
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Integer votes = dataSnapshot.getValue(Integer.class);
            tvi.setText(id + votes.toString());


        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

}
