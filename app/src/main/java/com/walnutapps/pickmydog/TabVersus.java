package com.walnutapps.pickmydog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Ivan on 2017-06-22.
 */

public class TabVersus extends Fragment {

    String Uid;
    String[] dogIdList;
    int totalUserExp;

    DatabaseReference mDatabase;
    StorageReference mStorageRef;

    ImageView dogTopVersusImageView;
    ImageView dogBottomVersusImageView;

    String newIdNumber = "";
    String topDogId;
    String bottomDogId;

    final long ONE_MEGABYTE = 1024 * 1024;
    String[] photoNamesArray = {"floatingMainActionButton", "floatingActionButton1", "floatingActionButton2", "floatingActionButton3", "floatingActionButton4", "floatingActionButton5"};
    ArrayList<Bitmap> imagesBitmapArrayList = new ArrayList<>();
    Bitmap[] imagesBitmapArray = new Bitmap[6];

    MyViewPager mImageViewPager1;
    ViewPager mImageViewPager2;

    int topDogTotalExp = -1;
    int bottomDogTotalExp = -1;

    int randomNumLast = -1;

    public TabVersus(String Uid, String[] dogIdList, int totalUserExp) {
        this.Uid = Uid;
        this.dogIdList = dogIdList;
        this.totalUserExp = totalUserExp;
    }

    private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {
            int count = 0;

            for (final String photoName : photoNamesArray) {
                StorageReference getDogPicturesStorageReference = mStorageRef.child(newIdNumber).child(String.valueOf(0)).child(photoName);

                final int finalCount = count;
                getDogPicturesStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap dogPictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imagesBitmapArray[finalCount] = dogPictureBitmap;


                        // Data for "images/island.jpg" is returns, use this as needed
                    }
                });
                count++;
            }
            return null;
        }


        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            //showDialog("Downloaded " + result + " bytes");
        }
    }

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


        mImageViewPager1 = (MyViewPager) rootView.findViewById(R.id.pager1);
        //mImageViewPager1.setPageMargin(0);
        //mImageViewPager1.setPadding(0,0,0,0);
        mImageViewPager1.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        mImageViewPager1.setPadding(141, 0, 141, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        mImageViewPager1.setPageMargin(0);
        mImageViewPager2 = (ViewPager) rootView.findViewById(R.id.pager2);
        mImageViewPager2.setPadding(141, 0, 141, 0);
        mImageViewPager2.setClipToPadding(false);
        mImageViewPager2.setPageMargin(0);
        // mImageViewPager2.setOffscreenPageLimit(6);
        // mImageViewPager2.setCurrentItem(1, true);
        // dogTopVersusImageView = (ImageView)rootView.findViewById(R.id.imageView);
//        mImageViewPager1.setOnTouchListener(new View.OnTouchListener() {
//            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
//                @Override
//                public boolean onDoubleTap(MotionEvent e) {
//                    Log.i("Please ", "work");
//                    return super.onDoubleTap(e);
//                }
//            });
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });

    mImageViewPager1.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.i("Please ", "work");
                        givePointsToUser(0);
                        givePointsToDog(0);
                        getTwoDogs();
                        return super.onDoubleTap(e);
                    }
                });
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });
        mImageViewPager2.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.i("Please ", "work");
                    givePointsToUser(1);
                    givePointsToDog(1);
                    getTwoDogs();
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });




        getTwoDogs();

        return rootView;
    }

    // TODO: 2017-07-13 also note currently hardcoded to get only the first dog of a user, so definitely change to above soon
    public void getTwoDogs() {
        mDatabase.child("dogs").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //finding number of users and then setting a random number between 0 inclusive and maxnum exclusive
//                long allNum = dataSnapshot.getChildrenCount();
//                int maxNum = (int)allNum;
//                int randomNum1 = new Random().nextInt(maxNum);
//
//                int count = 0;
//                Iterable<DataSnapshot> ds = dataSnapshot.getChildren();
//                Iterator<DataSnapshot> ids = ds.iterator();
//
//
//
//                Log.i("Random num 1: ", String.valueOf(randomNum1));
//                //moving along the users until the randomth user
//                while(ids.hasNext() && count < randomNum1) {
//                    ids.next();
//                    count ++; // used as positioning.
//                }
//                DataSnapshot randomuser = ids.next();
//
//                newIdNumber = (String) randomuser.getKey();
                topDogId = getRandomDog(dataSnapshot);


                final ViewPagerAdapter viewPagerAdapter1 = new ViewPagerAdapter(getContext());



                StorageReference getPicturesStorageReference;
                for (String photoName : photoNamesArray) {
                    getPicturesStorageReference = mStorageRef.child(newIdNumber).child(String.valueOf(topDogId)).child(photoName);

                    getPicturesStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap dogPictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            //imagesBitmapArray[finalCount] = dogPictureBitmap;

                            viewPagerAdapter1.addToBitmapArray(dogPictureBitmap);
                            viewPagerAdapter1.setReady();


                            // Data for "images/island.jpg" is returns, use this as needed
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            viewPagerAdapter1.noitfyNoMorePics();
                        }
                    });
                }

//                int randomNum2;
//                while((randomNum2 = new Random().nextInt(maxNum)) == randomNum1);
//                ds = dataSnapshot.getChildren();
//                ids = ds.iterator();
//                count = 0;
//
//                Log.i("Random num 2: ", String.valueOf(randomNum2));
//
//                while(ids.hasNext() && count < randomNum2) {
//                    ids.next();
//                    count ++; // used as positioning.
//                }
//                randomuser = ids.next();
//
//
//               newIdNumber = (String) randomuser.getKey();
                bottomDogId = getRandomDog(dataSnapshot);

                final ViewPagerAdapter viewPagerAdapter2 = new ViewPagerAdapter(getContext());

                StorageReference getPicturesStorageReference2;

                for (String photoName : photoNamesArray) {
                    getPicturesStorageReference2 = mStorageRef.child(newIdNumber).child(String.valueOf(bottomDogId)).child(photoName);

                    getPicturesStorageReference2.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap dogPictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            //imagesBitmapArray[finalCount] = dogPictureBitmap;

                            viewPagerAdapter2.addToBitmapArray(dogPictureBitmap);
                            viewPagerAdapter2.setReady();
                            //wrappedAdapter.notifyDataSetChanged();


                            // Data for "images/island.jpg" is returns, use this as needed
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            viewPagerAdapter2.noitfyNoMorePics();
                        }
                    });
                }

//                getDogPicturesStorageReference = mStorageRef.child(newIdNumber).child(String.valueOf(numberOfDogs)).child("floatingMainActionButton");
//                getDogPicturesStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Bitmap dogPictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
//                        dogBottomVersusImageView.setImageBitmap(Bitmap.createScaledBitmap(dogPictureBitmap, dogBottomVersusImageView.getWidth(), dogBottomVersusImageView.getHeight(), false));
//                    }
//                    // Data for "images/island.jpg" is returns, use this as needed
//
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Toast.makeText(getContext(), "Error, please load again", Toast.LENGTH_SHORT).show();
//                    }
//                });


                mImageViewPager1.setAdapter(viewPagerAdapter1);
                mImageViewPager1.setCurrentItem(viewPagerAdapter1.getCount() * ViewPagerAdapter.LOOPS_COUNT / 2, false);
                //viewPagerAdapter1.notifyDataSetChanged();
                //mImageViewPager1.setCurrentItem(0, true);
                mImageViewPager2.setAdapter(viewPagerAdapter2);
                mImageViewPager2.setCurrentItem(viewPagerAdapter2.getCount() * ViewPagerAdapter.LOOPS_COUNT / 2, false);
                //viewPagerAdapter2.notifyDataSetChanged();


                //Log.i("Random UID: ", newIdNumber);
                //Log.i("Num of dogs: ", String.valueOf(numberOfDogs));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("HERRREEE", "NOW, database error");
            }

        });


    }

//    private void getImages() {
//        int count = 0;
//
//        for(final String photoName : photoNamesArray) {
//            StorageReference getDogPicturesStorageReference = mStorageRef.child(Uid).child(String.valueOf(dogNumber)).child(photoName);
//
//            final int finalCount = count;
//            getDogPicturesStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//                    Bitmap dogPictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    imagesBitmapArrayList.add(dogPictureBitmap);
//
//
//                    // Data for "images/island.jpg" is returns, use this as needed
//                }
//            });
//            count ++;
//        }

    public String getRandomDog(DataSnapshot dataSnapshot) {

        String randomDogId = "";
        long allNum = dataSnapshot.getChildrenCount();
        int maxNum = (int) allNum;
        int randomNum;

        while ((randomNum = new Random().nextInt(maxNum)) == randomNumLast) ;

        int count = 0;

        Iterable<DataSnapshot> ds = dataSnapshot.getChildren();
        Iterator<DataSnapshot> ids = ds.iterator();

        Log.i("randomNum: ", String.valueOf(randomNum));

        while (ids.hasNext() && count < randomNum) {
            ids.next();
            count++; // used as positioning.
        }
        DataSnapshot randomDogSnapshot = ids.next();


        randomDogId = (String) randomDogSnapshot.getKey();
        for (String myDogs : dogIdList) {

            if (myDogs.equals(randomDogId)) {
                return getRandomDog(dataSnapshot);

            }
        }
        newIdNumber = randomDogSnapshot.child("ownerId").getValue().toString();
        if(topDogTotalExp == -1) {
            topDogTotalExp = Integer.parseInt(randomDogSnapshot.child("totalExp").getValue().toString());
        }else{
            bottomDogTotalExp = Integer.parseInt(randomDogSnapshot.child("totalExp").getValue().toString());
        }

        randomNumLast = randomNum;
        Log.i("dog ID:", randomDogId);
        return randomDogId;
    }

    //checks whether the top or bottom dog were double clicked and then awards points accoringly
    private void givePointsToDog(int position){
        if(position == 0) {
            int newTotalExp = topDogTotalExp + 20;
            mDatabase.child("dogs").child(topDogId).child("totalExp").setValue(newTotalExp);
            //this calculates out of the difference to the next level, the percentage it has gone so far.
            //double topDogPercentLeft = ((newTotalExp/100d) - (((Math.sqrt(((8d*newTotalExp)+1d)-1d))*((Math.sqrt(((8d*newTotalExp)+1d)-1d))+1d))/2d))/(((Math.sqrt(((8d*newTotalExp)+1d)-1d))+1d)) * 100d;
            //Log.i("Added to", String.valueOf(topDogPercentLeft));
            int topDogUpdatedLevel = (int)((Math.sqrt((8*(newTotalExp/100))+1)-1)/2);
            Log.i("topDogUpdatedLevel to", String.valueOf(topDogUpdatedLevel));
        }
        else if(position == 1){
            mDatabase.child("dogs").child(bottomDogId).child("totalExp").setValue(20 + bottomDogTotalExp);
            Log.i("Added to", "top dog");
        }else{
            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
        }

        topDogTotalExp = -1;
        bottomDogTotalExp = -1;
        randomNumLast = -1;
    }

    public void givePointsToUser(int position){
        if(position == 0){
            if(topDogTotalExp > bottomDogTotalExp || topDogTotalExp == bottomDogTotalExp){
                totalUserExp = totalUserExp + 20;
                mDatabase.child("users").child(Uid).child("experiencePoints").setValue(totalUserExp);
                Toast.makeText(getContext(), "Correct, you chose with the majority!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Sorry most people think otherwise!", Toast.LENGTH_SHORT).show();
            }
        }else if (position == 1){
            if(bottomDogTotalExp > topDogTotalExp || bottomDogTotalExp == topDogTotalExp){
                totalUserExp = totalUserExp + 20;
                mDatabase.child("users").child(Uid).child("experiencePoints").setValue(totalUserExp);
                Toast.makeText(getContext(), "Correct, you chose with the majority!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Sorry most people think otherwise!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
        }

    }
}



