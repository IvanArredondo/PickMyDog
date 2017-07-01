package com.walnutapps.pickmydog;

import android.graphics.Bitmap;

/**
 * Created by Ivan on 2017-06-24.
 */

public class User {
    private String uid;
    private String name;
    private String email;
    private String phoneNumber;
    private  String profilePicture;
    private  int numberOfDogs;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    // TODO: 2017-06-30 Change this to get rid of string and sync with local sqlite daabe w firebase
    public User(String uid, String name, String email, String profilePicture, int numberOfDogs) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.numberOfDogs = numberOfDogs;
    }

    // getting ID
    public String getID(){
        return this.uid;
    }

    // setting id
    public void setID(String uid){
        this.uid = uid;
    }

    // getting name
    public String getName(){
        return this.name;
    }

    // setting name
    public void setName(String name){
        this.name = name;
    }

    // getting phone number
    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    // setting phone number
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumberOfDogs() {
        return numberOfDogs;
    }

    public void setNumberOfDogs(int numberOfDogs) {
        this.numberOfDogs = numberOfDogs;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
