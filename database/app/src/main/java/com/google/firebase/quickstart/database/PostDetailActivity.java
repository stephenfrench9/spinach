package com.google.firebase.quickstart.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.models.User;
import com.google.firebase.quickstart.database.models.Comment;
import com.google.firebase.quickstart.database.models.Post;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;


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

    private ImageView mImageView;
    private ImageView mImageView2;
    private Button mLikeButton;




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

        User user = new User("work", "days");

        Log.d(TAG,"onCreate(): assign the views");
        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(mPostKey);

        // Initialize Views
        mAuthorView = findViewById(R.id.post_author);
        mImageView = findViewById(R.id.databasePicture);
        mImageView2 = findViewById(R.id.databasePicture2);
        mLikeButton = findViewById(R.id.LIKE);


        mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("user-likes").child(mUserKey).child(mPostKey).setValue(true);
            }
        });
        Log.d(TAG,"onCreate(): About to access the database");

        StorageReference node = FirebaseStorage.getInstance().getReference().child(mPostOwner).child(mPostKey).child("one");
        StorageReference node2 = FirebaseStorage.getInstance().getReference().child(mPostOwner).child(mPostKey).child("two");


        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("CHECKPOINT","THE FILE WILL BE DOWNLOADED NEXT");
        Log.d(TAG,"onCreate(): Gonna Download some pictures");
        final File finalLocalFile = localFile;
        node.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d("CHECKPOINT","the file has been downloaded");
                String path = finalLocalFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                mImageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        File localFile2 = null;
        try {
            localFile2 = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalLocalFile2 = localFile2;
        node2.getFile(localFile2).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d("CHECKPOINT","the file has been downloaded");
                String path = finalLocalFile2.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                mImageView2.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        Log.d(TAG,"onCreate(): finished");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart(): gonna read the author from the database");
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
}
