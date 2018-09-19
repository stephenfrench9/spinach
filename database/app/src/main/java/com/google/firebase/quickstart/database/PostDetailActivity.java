package com.google.firebase.quickstart.database;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
        setContentView(R.layout.activity_post_detail);
        Log.d(TAG,"onCreate(): Post Detail Activity is created");

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        mPostOwner = getIntent().getStringExtra(EXTRA_POST_OWNER);
        mUserKey = getUid();

        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
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
        mOne = new VoteListener(mTextView1,"One");
        mTwo = new VoteListener(mTextView2, "Two");

        mPostReference.child("one").addListenerForSingleValueEvent(mOne);
        mPostReference.child("two").addListenerForSingleValueEvent(mTwo);
        Log.d(TAG,"onCreate(): finished");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart(): gonna read the author from the database");
        //get the author from the post
        mPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,",onCreate(),onDataChanged(): start onDataChanged()");
                String author = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        Log.d(TAG,"onStart(): About to add the value event listener to the post online");
        mPostReference.child("author").addListenerForSingleValueEvent(mPostListener);
        Log.d(TAG,"onStart(): Post Detail Activity is finished");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mPostReference.child("one").removeEventListener(mOne);
//        mPostReference.child("two").removeEventListener(mTwo);
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
//            Log.d("BASIL", "onDataChange(): Start");
            Integer votes = dataSnapshot.getValue(Integer.class);
            tvi.setText(votes.toString());
            Log.d("BASIL", id + ": " + votes.toString());
//            Log.d("BASIL", "onDataChange(): End");
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

}
