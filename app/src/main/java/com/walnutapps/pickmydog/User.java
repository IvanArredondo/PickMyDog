package com.walnutapps.pickmydog;

import android.graphics.Bitmap;

/**
 * Created by Ivan on 2017-06-24.
 */

public class User {

    public String name;
    public String email;
    public String uid;
    String profilePicture;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String uid, String profilePicture) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.profilePicture = profilePicture;
    }
}
