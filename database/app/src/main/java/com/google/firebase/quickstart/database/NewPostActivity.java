package com.google.firebase.quickstart.database;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.models.Post;
import com.google.firebase.quickstart.database.models.User;
import com.google.firebase.quickstart.database.utilities.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileproviderThree";
    private int REQUEST_IMAGE_CAPTURE = 1;

    private DatabaseReference mDatabase;
    private FloatingActionButton mSubmitButton;
    private Button mTakePicture;
    private Button mTakePicture2;
    private ImageView mNewPicture;
    private ImageView mNewPicture2;
    private String mTempPhotoPath;
    private String mTempPhotoPath2;
    private boolean mPictureOne;
    private StorageReference mRoot;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("silver", "onSaveInstanceState(): start");

        Log.d("silver", "onSaveInstanceState(): saving mPictureOne: " + String.valueOf(mPictureOne));
        outState.putBoolean("mPictureOne", mPictureOne);
        Log.d("silver", "onSaveInstanceState(): saving mTempPhotoPath: " + String.valueOf(mTempPhotoPath));
        outState.putString("mTempPhotoPath", mTempPhotoPath);
        Log.d("silver", "onSaveInstanceState(): saving mTempPhotoPath2: " + String.valueOf(mTempPhotoPath2));
        outState.putString("mTempPhotoPath2", mTempPhotoPath2);

        Log.d("silver", "onSaveInstanceState(): end");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("silver", "onCreate(): start");

        setContentView(R.layout.activity_new_post);
        ActionBar greg = getSupportActionBar();
        greg.hide();

        // [START initialize_database_ref]
        mDatabase = Util.getDatabase().getReference();
        // [END initialize_database_ref]
        mPictureOne = false;
        mTempPhotoPath = "";
        mTempPhotoPath2 = "";
        mSubmitButton = findViewById(R.id.fab_submit_post);
        mTakePicture = findViewById(R.id.picture);
        mTakePicture2 = findViewById(R.id.picture2);
        mNewPicture = findViewById(R.id.newPicture);
        mNewPicture2 = findViewById(R.id.newPicture2);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        mRoot = FirebaseStorage.getInstance().getReference();
        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPictureOne=true;
                launchCamera();
            }
        });
        mTakePicture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPictureOne=false;
                launchCamera();
            }
        });

        if(savedInstanceState == null) {//recover some class variables
            mPictureOne = savedInstanceState.getBoolean("mPictureOne");
            Log.d("silver","onCreate(): recovered mPictureOne: " + String.valueOf(mPictureOne));
            mTempPhotoPath = savedInstanceState.getString("mTempPhotoPath");
            Log.d("silver","onCreate(): recovered mTempPhotoPath: " + mTempPhotoPath);
            mTempPhotoPath2 = savedInstanceState.getString("mTempPhotoPath2");
            Log.d("silver","onCreate(): recovered mTempPhotoPath: " + mTempPhotoPath2);
        }

        Log.d("silver", "onCreate(): end");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("silver", "onDestroy(): start");
        Log.d("silver", "onDestroy(): end");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("silver", "onActivityResult(): start");
        final String uid = getUid();
        Log.d("silver", "onActivityResult(): one");

        if(resultCode == RESULT_OK) {
            Log.d("silver", "onActivityResult(): two");

            Bitmap bitmap;
            Log.d("silver", "onActivityResult(): three");

            // Get the data from an ImageView as bytes

            if(mPictureOne) {
                Log.d("silver", "onActivityResult(): four");

//                bitmap = resamplePic(this, mTempPhotoPath);
                try {
                    Log.d("silver", "onActivityResult(): make Bitmap");
                    Log.d("silver", "onActivityResult(): value of mTempPhotoPath is " + mTempPhotoPath);
                    Bitmap compressedImageBitmap = new Compressor(this).compressToBitmap(new File(mTempPhotoPath));
                    Log.d("silver", "onActivityResult(): Bitmap Successfully made, place in the image view");
                    mNewPicture.setImageBitmap(compressedImageBitmap);
                    Log.d("silver", "onActivityResult(): bitmap successfully placed into the image view");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    Bitmap compressedImageBitmap2 = new Compressor(this).compressToBitmap(new File(mTempPhotoPath2));
                    mNewPicture2.setImageBitmap(compressedImageBitmap2);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//
//                bitmap = resamplePic(this, mTempPhotoPath2);
//                mNewPicture2.setImageBitmap(bitmap);
            }

            Toast.makeText(this, "the camera successfully took a foto", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "the camera did not successfully took a foto", Toast.LENGTH_LONG).show();
        }

        Log.d("silver", "onActivityResult(): end");

    }

    private void submitPost() {
        if (mNewPicture.getDrawable()==null) {
            Toast.makeText(this, "take two photos", Toast.LENGTH_LONG).show();
            return;
        }

        if (mNewPicture2.getDrawable()==null) {
            Toast.makeText(this, "take two photos", Toast.LENGTH_LONG).show();
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, "dummy", "dummy");
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }

    private void writeNewPost(String userId, String username, String address, String address2) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        address = getUid().toString() + "/" + key + "/one";
        address2 = getUid().toString() + "/" + key + "/two";
        Post post = new Post(userId, username, address, address2);
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        StorageReference destinationNode = mRoot.child(getUid().toString()).child(key).child("one");
        StorageReference destinationNode2 = mRoot.child(getUid().toString()).child(key).child("two");

        Bitmap bitmap = null;
        Bitmap bitmap2 = null;
        try {
            bitmap = new Compressor(this).compressToBitmap(new File(mTempPhotoPath));
            bitmap2 = new Compressor(this).compressToBitmap(new File(mTempPhotoPath2));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeToCloudStorage(destinationNode, bitmap);
        writeToCloudStorage(destinationNode2, bitmap2);
        mDatabase.updateChildren(childUpdates);
        mDatabase.child("posts").child(key).child(getUid().toString()).setValue(99);//child(key).child(getUid().toString()).setValue(true);
    }

    private void writeToCloudStorage(StorageReference destinationNode, Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] bitmapT = baos.toByteArray();

        UploadTask uploadTask = destinationNode.putBytes(bitmapT);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    };

    private void launchCamera() {
        Log.d("silver", "launchCamera(): start");

        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go

            File photoFile = null;
            try {
                photoFile = createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                if(mPictureOne) {
                    mTempPhotoPath = photoFile.getAbsolutePath();
                } else {
                    mTempPhotoPath2 = photoFile.getAbsolutePath();
                }
                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);
                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        Log.d("silver", "launchCamera(): end");
    }

    private void setEditingEnabled(boolean enabled) {
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    static File createTempImageFile(Context context) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    static Bitmap resamplePic(Context context, String imagePath) {

        // Get device screen size information
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);

        int targetH = metrics.heightPixels;
        int targetW = metrics.widthPixels;

        // Get the dimensions of the original bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath);
    }
}
