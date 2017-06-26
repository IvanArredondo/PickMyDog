package com.walnutapps.pickmydog;

/**
 * Created by Ivan on 2017-06-22.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TabProfile extends Fragment {

    DatabaseReference mDatabase;

    String Uid;

    byte[]  imageAsBytes;

    ImageView profileCircleImageView;

    public TabProfile(String uid) {
        this.Uid = uid;
    }

    public void addDogClick(View v){
        Log.i("Add Dog IN Tab Profile", "Clicked");

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Log.i("THE TAB USER Id:", Uid);

        mDatabase.child("users").child(Uid).child("profilePicture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("VALUE: " , dataSnapshot.toString());
                Log.i("HERRREEE", "NOW, one before" );
                String base64Image = (String) dataSnapshot.getValue();
                //Log.i("HERRREEE", "NOW, one before" );
                imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
               // Log.i("HERRREEE", "NOW, one before" );
                setProfilePicture(imageAsBytes);

                // Log.i("HERRREEE", "All DONE" );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("HERRREEE", "NOW, database error" );
            }

        });
        Log.i("HERRREEE", "All DONE" );
    }

    private void setProfilePicture(byte[] bytes) {




        Bitmap profilePictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Log.i("HERRREEE", "NOW");

        RoundedBitmapDrawable roundedPicture = RoundedBitmapDrawableFactory.create(getResources(),profilePictureBitmap);
        //roundedPicture.setCornerRadius(Math.max(profilePictureBitmap.getWidth(), profilePictureBitmap.getHeight()) / 2.0f);
        roundedPicture.setCircular(true);

        profileCircleImageView.setImageDrawable(roundedPicture);

        Log.i("HERRREEE", "NOW SET THE PIC");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_tab, container, false);

        profileCircleImageView = (ImageView)rootView.findViewById(R.id.profileCircleImageView);

        Log.i("HERRREEE", "called onCreateView");

        return rootView;
    }




}
