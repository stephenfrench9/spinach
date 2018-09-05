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

public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileproviderThree";
    private int REQUEST_IMAGE_CAPTURE = 1;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]


    private String mAddressOne;
    private String mAddressTwo;
    private FloatingActionButton mSubmitButton;
    private Button mTakePicture;
    private ImageView mNewPicture;
    private String mTempPhotoPath;

    private StorageReference mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        mSubmitButton = findViewById(R.id.fab_submit_post);
        mTakePicture = findViewById(R.id.picture);
        mNewPicture = findViewById(R.id.newPicture);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        mRoot = FirebaseStorage.getInstance().getReference();
        mAddressOne = "/uid/grapses/fighs/";
        mAddressTwo = "/uid/post-key/addressTwo";
    }

    private void writeToCloudStorage(StorageReference destinationNode, Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        final String uid = getUid();
        Log.d("CHECKPOINTS", "the callback was called in main for the completion of the camera activity");

        if(resultCode == RESULT_OK) {
            Bitmap bitmap = resamplePic(this, mTempPhotoPath);
            // Get the data from an ImageView as bytes
            mNewPicture.setImageBitmap(bitmap);
            Toast.makeText(this, "the camera successfully took a foto", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "the camera did not successfully took a foto", Toast.LENGTH_LONG).show();
        }
    }

    private void submitPost() {

        final String addressOne = mAddressOne;
        final String addressTwo = mAddressTwo;

        // Title is required
        if (TextUtils.isEmpty(addressOne)) {
            Toast.makeText(this, "there was a ship on the horizon today", Toast.LENGTH_LONG);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(addressTwo)) {
            Toast.makeText(this, "there was a ship on the horizon today", Toast.LENGTH_LONG);
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
                            writeNewPost(userId, user.username, addressOne, addressTwo);
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

    private void setEditingEnabled(boolean enabled) {

        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String addressOne, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        String key = mDatabase.child("posts").push().getKey();
        addressOne = getUid().toString()+ "/" + key + "/one";
        Post post = new Post(userId, username, addressOne, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        StorageReference destinationNode = mRoot.child(getUid().toString()).child(key).child("one");
        Bitmap bitmap = resamplePic(this, mTempPhotoPath);

        writeToCloudStorage(destinationNode, bitmap);


        mDatabase.updateChildren(childUpdates);
    }

    private void launchCamera() {
        Log.d("CHECKPOINTS", "helper method called: launch the camera");

        // Create the capture image intent
        Log.d("CHECKPOINTS", "start launch camera call");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("CHECKPOINTS", "intent created");

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            Log.d("CHECKPOINTS", "there does exist a camera activity");

            File photoFile = null;
            try {
                Log.d("CHECKPOINTS", "we are going to try to create a temp file");
                photoFile = createTempImageFile(this);
                Log.d("CHECKPOINTS", "we created a temp file");
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("CHECKPOINTS", "oh man, time to throw an error");
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Log.d("CHECKPOINTS", "temp file was created, proceeding...");
                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();
                Log.d("CHECKPOINTS", "We have an absolute path: " + mTempPhotoPath);
                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);
                Log.d("CHECKPOINTS", "the photo uri was recorded successfully");
                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d("CHECKPOINTS", "Added the photo uri to the take picture intent");
                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    static File createTempImageFile(Context context) throws IOException {
        Log.d("CHECKPOINTS", "helper method called: createTempImageFile");

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



    // [END write_fan_out]
}
