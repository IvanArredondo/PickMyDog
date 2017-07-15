package com.walnutapps.pickmydog;

/**
 * Created by Ivan on 2017-06-22.
 */

import android.animation.ObjectAnimator;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    String[] dogIdList;

    byte[]  imageAsBytes;

    ImageView profileCircleImageView;
    ImageView dogPictureImageView;

    RoundedBitmapDrawable dogRoundedPictureBitmap;
    RoundedBitmapDrawable profilePictureRoundedPictureBitmap;

    ProgressBar userLevelProgressBar;
    ProgressBar dogLeveProgressBar;

    TextView userLevelTextView;
    TextView dogLevelTextView;

    int userLevel = -1;
    int dogLevel = -1;

    int userExp = -1;
    int dogExp = -1;

    final long ONE_MEGABYTE = 1024 * 1024;

    public  TabProfile(String uid, int numberOfDogs, String[] dogIdList) {
        this.Uid = uid;
        this.numberOfDogs = numberOfDogs;
        this.dogIdList = dogIdList;
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

        mDatabase.keepSynced(true);

        Log.i("tap profile DOGS:", String.valueOf(numberOfDogs));


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

        userLevelProgressBar = (ProgressBar)rootView.findViewById(R.id.userLevelProgressBar);
        dogLeveProgressBar = (ProgressBar)rootView.findViewById(R.id.dogLevelProgressBar);

        userLevelTextView = (TextView) rootView.findViewById(R.id.userLevelTextView);
        dogLevelTextView = (TextView) rootView.findViewById(R.id.dogLevelTextView);







        Log.i("HERRREEE", "called onCreateView");

        if(profilePictureRoundedPictureBitmap != null && userLevel != -1 && userExp != -1){
            profileCircleImageView.setImageDrawable(profilePictureRoundedPictureBitmap);
            userLevelTextView.setText(String.valueOf(userLevel));

            float userLevelProgress = getLevelProgress(userExp,userLevel);

            ObjectAnimator animationUser = ObjectAnimator.ofInt (userLevelProgressBar, "progress", 0, (int)userLevelProgress); // see this max value coming back here, we animale towards that value
            animationUser.setDuration (1000); //in milliseconds
            animationUser.setInterpolator (new DecelerateInterpolator());
            animationUser.start ();
        }else {

            // TODO: 2017-07-13 consider changing this to save the profile picture in the storage
            mDatabase.child("users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.i("VALUE: ", dataSnapshot.child("profilePicture").toString());
                    Log.i("HERRREEE", "NOW, one before");
                    String base64Image = (String) dataSnapshot.child("profilePicture").getValue();
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

                    userLevel = Integer.parseInt(dataSnapshot.child("level").getValue().toString());
                    userLevelTextView.setText(String.valueOf(userLevel));

                    userExp = Integer.parseInt(dataSnapshot.child("experiencePoints").getValue().toString());

                    float userLevelProgress = getLevelProgress(userExp,userLevel);

                    ObjectAnimator animationUser = ObjectAnimator.ofInt (userLevelProgressBar, "progress", 0, (int)userLevelProgress); // see this max value coming back here, we animale towards that value
                    animationUser.setDuration (1000); //in milliseconds
                    animationUser.setInterpolator (new DecelerateInterpolator());
                    animationUser.start ();



                    // Log.i("HERRREEE", "All DONE" );
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("HERRREEE", "NOW, database error");
                }

            });
        }


        if(dogLevel != -1 && dogExp != -1){
            dogLevelTextView.setText(String.valueOf(dogLevel));

            float dogLevelProgress = getLevelProgress(dogExp, dogLevel);

            ObjectAnimator animationDog = ObjectAnimator.ofInt (dogLeveProgressBar, "progress", 0, (int)dogLevelProgress); // see this max value coming back here, we animale towards that value
            animationDog.setDuration (1000); //in milliseconds
            animationDog.setInterpolator (new DecelerateInterpolator());
            animationDog.start ();

        }else{
            mDatabase.child("dogs").child(dogIdList[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dogLevel = Integer.parseInt(dataSnapshot.child("level").getValue().toString());
                    dogLevelTextView.setText(String.valueOf(dogLevel));

                    dogExp = Integer.parseInt(dataSnapshot.child("totalExp").getValue().toString());

                    float dogLevelProgress = getLevelProgress(dogExp, dogLevel);

                    ObjectAnimator animationUser = ObjectAnimator.ofInt (dogLeveProgressBar, "progress", 0, (int)dogLevelProgress); // see this max value coming back here, we animale towards that value
                    animationUser.setDuration (1000); //in milliseconds
                    animationUser.setInterpolator (new DecelerateInterpolator());
                    animationUser.start ();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        //if there already exists a picture for the dog, consider moving this to TabbedActivity
        // TODO: 2017-07-14 hardcoded as first dog ie dogIdList[0]
        if (dogRoundedPictureBitmap != null) {
            Log.i("dog image: ", "saved in memory");
            dogPictureImageView.setImageDrawable(dogRoundedPictureBitmap);
        } else {
            StorageReference dogProfilePictureStorageReference = mStorageRef.child(Uid).child(dogIdList[0]).child("floatingMainActionButton");
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
                    //Toast.makeText(rootView.getContext(), "dog image download failed", Toast.LENGTH_SHORT).show();
                }
            });

        }
        return rootView;
    }

    public float getLevelProgress(int totalExp, int level){

        float levelProgress = 0;

        if(totalExp == -1 || level == -1){
            Toast.makeText(getContext(), "Unable to get level stats, reconnect", Toast.LENGTH_SHORT).show();
            Log.i("Level Progress: ", String.valueOf(levelProgress));
        }else{
            levelProgress = (((totalExp/100f) - (level*(level+1f))/2f)/(level + 1f))*100f;
            Log.i("Level Progress: ", String.valueOf(levelProgress));
            Toast.makeText(getContext(), "Level Progress" + levelProgress, Toast.LENGTH_SHORT).show();

        }

        return levelProgress;

    }

}
