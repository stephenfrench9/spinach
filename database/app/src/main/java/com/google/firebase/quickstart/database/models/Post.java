package com.google.firebase.quickstart.database.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public String addressOne;
    public String addressTwo;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String addressOne, String addressTwo) {
        this.uid = uid;
        this.author = author;
        this.addressOne = addressOne;
        this.addressTwo = addressTwo;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("addressOne", addressOne);
        result.put("addressTwo", addressTwo);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put(uid, true);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
