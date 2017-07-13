package com.walnutapps.pickmydog;

import android.graphics.Bitmap;

/**
 * Created by Ivan on 2017-06-24.
 */

public class Dog {

    private String name;
    private String breed;
    private String description;
    private String ownerId;
    private int wins =0;
    private int losses =0;
    private int totalExp =0;
    private int level =0;



    public Dog() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public Dog(String name, String breed, String description, String ownerId){
        this.name = name;
        this.breed = breed;
        this.description = description;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(int totalExp) {
        this.totalExp = totalExp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
