package com.google.firebase.quickstart.database.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.quickstart.database.models.Post;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import static android.media.ThumbnailUtils.extractThumbnail;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public ImageView mPicture1;
    public ImageView mPicture2;

    public PostViewHolder(View itemView) {
        super(itemView);

        mPicture1 = itemView.findViewById(R.id.picture1);
        mPicture2 = itemView.findViewById(R.id.picture2);

    }

    public void bindToPost(Post post, String postKey) {
        StorageReference node = FirebaseStorage.getInstance().getReference().child(post.uid).child(postKey).child("one");
        StorageReference node2 = FirebaseStorage.getInstance().getReference().child(post.uid).child(postKey).child("two");

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("CHECKPOINT","THE FILE WILL BE DOWNLOADED NEXT");
        final File finalLocalFile = localFile;
        node.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d("CHECKPOINT","onSucces callback: the file has been downloaded");
                String path = finalLocalFile.getPath();
                Bitmap bitmap = extractThumbnail(BitmapFactory.decodeFile(path), 100,100);
                mPicture1.setImageBitmap(bitmap);
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
                Bitmap bitmap = extractThumbnail(BitmapFactory.decodeFile(path), 100, 100);
                mPicture2.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        Log.d("CHECKPOINT", "bindToPost: completed");
    }
}
