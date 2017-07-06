package com.walnutapps.pickmydog;

/**
 * Created by Ivan on 2017-06-22.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class  TabProfile extends Fragment {

    DatabaseReference mDatabase;

    StorageReference mStorageRef;

    public String Uid;
    int numberOfDogs;

    byte[]  imageAsBytes;

    ImageView profileCircleImageView;
    ImageView dogPictureImageView;

    RoundedBitmapDrawable dogRoundedPictureBitmap;
    RoundedBitmapDrawable profilePictureRoundedPictureBitmap;

    public  TabProfile(String uid, int numberOfDogs) {
        this.Uid = uid;
        this.numberOfDogs = numberOfDogs;
       // super.getActivity().getClass().in
    }

    public void addDogClick(View v){
        Log.i("Add Dog IN Tab Profile", "Clicked");

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Log.i("THE TAB USER Id:", Uid);


        Log.i("HERRREEE", "All DONE" );
    }

    private void setProfilePicture(byte[] bytes) {






    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.profile_tab, container, false);

        profileCircleImageView = (ImageView) rootView.findViewById(R.id.profileCircleImageView);
        dogPictureImageView = (ImageView) rootView.findViewById(R.id.dogPictureImageView);


        Log.i("HERRREEE", "called onCreateView");

        if(profilePictureRoundedPictureBitmap != null){
            profileCircleImageView.setImageDrawable(profilePictureRoundedPictureBitmap);
        }else {

            mDatabase.child("users").child(Uid).child("profilePicture").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.i("VALUE: ", dataSnapshot.toString());
                    Log.i("HERRREEE", "NOW, one before");
                    String base64Image = (String) dataSnapshot.getValue();
                    //Log.i("HERRREEE", "NOW, one before" );
                    if (base64Image == null) {
                        Log.d("inside getting pic:", "No pic found");
                    } else if (base64Image.equals("To be Determined")) {
                        //set up the add picture button
                    } else {
                        imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                        // Log.i("HERRREEE", "NOW, one before" );

                        Bitmap profilePictureBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        Log.i("HERRREEE", "NOW");

                        profilePictureRoundedPictureBitmap = RoundedBitmapDrawableFactory.create(getResources(), profilePictureBitmap);
                        //roundedPicture.setCornerRadius(Math.max(profilePictureBitmap.getWidth(), profilePictureBitmap.getHeight()) / 2.0f);
                        profilePictureRoundedPictureBitmap.setCircular(true);

                        profileCircleImageView.setImageDrawable(profilePictureRoundedPictureBitmap);

                        Log.i("HERRREEE", "NOW SET THE PIC");
                    }

                    // Log.i("HERRREEE", "All DONE" );
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("HERRREEE", "NOW, database error");
                }

            });
        }


        final long ONE_MEGABYTE = 1024 * 1024;

        //if there already exists a picture for the dog
        if (dogRoundedPictureBitmap != null) {
            dogPictureImageView.setImageDrawable(dogRoundedPictureBitmap);
        } else {
            StorageReference dogProfilePictureStorageReference = mStorageRef.child(Uid).child(String.valueOf(numberOfDogs)).child("floatingMainActionButton");
            dogProfilePictureStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap dogProfilePictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Log.i("HERRREEE", "NOW");

                    dogRoundedPictureBitmap = RoundedBitmapDrawableFactory.create(getResources(), dogProfilePictureBitmap);
                    //roundedPicture.setCornerRadius(Math.max(profilePictureBitmap.getWidth(), profilePictureBitmap.getHeight()) / 2.0f);
                    dogRoundedPictureBitmap.setCircular(true);
                    dogPictureImageView.setImageDrawable(dogRoundedPictureBitmap);


                    Log.i("HERRREEE", "NOW SET THE PIC");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(rootView.getContext(), "dog image download failed", Toast.LENGTH_SHORT).show();
                }
            });

        }
        return rootView;
    }

}
