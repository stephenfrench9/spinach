package com.google.firebase.quickstart.database;

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
import com.google.firebase.quickstart.database.models.Post;
import com.google.firebase.quickstart.database.utilities.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class PostDetailActivity extends BaseActivity {

    private static final String TAG = "PostDetailActivity";
    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_POST_OWNER = "post_owner";

    private DatabaseReference mPostReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private String mUserKey;
    private String mPostOwner;
    private TextView mAuthorView;
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
        mAuthorView = findViewById(R.id.post_author);
        mImageView1 = findViewById(R.id.databasePicture);
        mImageView2 = findViewById(R.id.databasePicture2);
        mTextView1 = findViewById(R.id.textViewOne);
        mTextView2 = findViewById(R.id.textViewTwo);

        //load the pictures
        StorageReference node = FirebaseStorage.getInstance().getReference().child(mPostOwner).child(mPostKey).child("one");
        StorageReference node2 = FirebaseStorage.getInstance().getReference().child(mPostOwner).child(mPostKey).child("two");
        Util.downloadImage(node, mImageView1);
        Util.downloadImage(node2, mImageView2);

        //load the votes
        mOne = new VoteListener(mTextView1);
        mTwo = new VoteListener(mTextView2);

        mPostReference.child("one").addListenerForSingleValueEvent(mOne);
        mPostReference.child("two").addValueEventListener(mTwo);
        mPostReference.child("two").removeEventListener(mTwo);
//        mPostReference.child("one").setValue(11);
        mPostReference.child("two").setValue(11);

//        mPostReference.child("one").setValue(21);
        mPostReference.child("two").setValue(21);

//        mPostReference.child("one").setValue(31);
        mPostReference.child("two").setValue(31);


        mPostReference.child("one").removeEventListener(mOne);

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
                Post thisPost = dataSnapshot.getValue(Post.class);
                mAuthorView.setText(thisPost.author);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        Log.d(TAG,"onStart(): About to add the value event listener to the post online");
        mPostReference.addValueEventListener(mPostListener);
        Log.d(TAG,"onStart(): Post Detail Activity is finished");
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    class VoteListener implements ValueEventListener {
        private TextView tvi;
        VoteListener(TextView t) {
            tvi = t;
        }
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            Log.d("BASIL", "onDataChange(): Start");
            Integer votes = dataSnapshot.getValue(Integer.class);
            tvi.setText(votes.toString());
            Log.d("BASIL", String.valueOf(tvi.getId()) + ": " + votes.toString());
//            Log.d("BASIL", "onDataChange(): End");
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
