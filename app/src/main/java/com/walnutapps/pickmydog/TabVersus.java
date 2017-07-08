package com.walnutapps.pickmydog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Ivan on 2017-06-22.
 */

public class TabVersus extends Fragment {

    DatabaseReference mDatabase;
    StorageReference mStorageRef;

    ImageView dogTopVersusImageView;
    ImageView dogBottomVersusImageView;

    final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.versus_tab, container, false);

        dogTopVersusImageView = (ImageView)rootView.findViewById(R.id.dogTopVersusImageView);
        dogBottomVersusImageView = (ImageView)rootView.findViewById(R.id.dogBottomVersusImageView);

        getTwoDogs();

        return rootView;
    }

    public void getTwoDogs(){
        mDatabase.child("users").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                long allNum = dataSnapshot.getChildrenCount();
                int maxNum = (int)allNum;
                int randomNum1 = new Random().nextInt(maxNum);

                int count = 0;
                Iterable<DataSnapshot> ds = dataSnapshot.getChildren();
                Iterator<DataSnapshot> ids = ds.iterator();



                Log.i("Random num 1: ", String.valueOf(randomNum1));
                while(ids.hasNext() && count < randomNum1) {
                    ids.next();
                    count ++; // used as positioning.
                }
                DataSnapshot randomuser = ids.next();

                String newIdNumber = (String) randomuser.getKey();
                Long numberOfDogs = (Long)randomuser.child("numberOfDogs").getValue();

                StorageReference getDogPicturesStorageReference = mStorageRef.child(newIdNumber).child(String.valueOf(numberOfDogs)).child("floatingMainActionButton");
                getDogPicturesStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap dogPictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        dogTopVersusImageView.setImageBitmap(Bitmap.createScaledBitmap(dogPictureBitmap, dogTopVersusImageView.getWidth(), dogTopVersusImageView.getHeight(), false));
                        }
                        // Data for "images/island.jpg" is returns, use this as needed

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Error, please load again", Toast.LENGTH_SHORT).show();
                    }
                });
                int randomNum2;
                while((randomNum2 = new Random().nextInt(maxNum)) == randomNum1);
                ds = dataSnapshot.getChildren();
                ids = ds.iterator();
                count = 0;

                Log.i("Random num 2: ", String.valueOf(randomNum2));

                while(ids.hasNext() && count < randomNum2) {
                    ids.next();
                    count ++; // used as positioning.
                }
                randomuser = ids.next();


               newIdNumber = (String) randomuser.getKey();
                numberOfDogs = (Long)randomuser.child("numberOfDogs").getValue();

                getDogPicturesStorageReference = mStorageRef.child(newIdNumber).child(String.valueOf(numberOfDogs)).child("floatingMainActionButton");
                getDogPicturesStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap dogPictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        dogBottomVersusImageView.setImageBitmap(Bitmap.createScaledBitmap(dogPictureBitmap, dogTopVersusImageView.getWidth(), dogTopVersusImageView.getHeight(), false));
                    }
                    // Data for "images/island.jpg" is returns, use this as needed

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Error, please load again", Toast.LENGTH_SHORT).show();
                    }
                });


                //Log.i("Random UID: ", newIdNumber);
                //Log.i("Num of dogs: ", String.valueOf(numberOfDogs));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("HERRREEE", "NOW, database error");
            }

        });

    }


}
