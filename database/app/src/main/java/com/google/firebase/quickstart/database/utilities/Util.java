package com.google.firebase.quickstart.database.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Util {

    static String TAG = "CHECKPOINT";
    private static FirebaseDatabase mDb;

    public static void downloadImage(StorageReference node, final ImageView iv) {
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"THE FILE WILL BE DOWNLOADED soon");
        final File finalLocalFile = localFile;
        node.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d(TAG,"the file has been downloaded");
                String path = finalLocalFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                iv.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static FirebaseDatabase getDatabase() {
        Log.d("shulin", "getDatabase(): the utility method is called");
        if(mDb == null) {
            Log.d("shulin", "getDatabase(): the utility class had a null database reference, now assigning");
            mDb = FirebaseDatabase.getInstance();
            Log.d("shulin", "getDatabase(): mDb assigned properly");
//            mDb.setPersistenceEnabled(true);
        }
        Log.d("shulin", "getDatabase(): returning the database reference");
        return mDb;
    }

}
