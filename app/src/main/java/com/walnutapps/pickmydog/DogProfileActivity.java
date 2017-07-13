package com.walnutapps.pickmydog;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class DogProfileActivity extends AppCompatActivity {

    ImageView dogMainPictureImageView;
    ImageView dogPictureImageView1;
    ImageView dogPictureImageView2;
    ImageView dogPictureImageView3;
    ImageView dogPictureImageView4;
    ImageView dogPictureImageView5;

    EditText dogNameEditText;
    EditText dogBreedEditText;
    EditText dogDescriptionEditText;

    FloatingActionButton floatingMainActionButton;
    FloatingActionButton floatingActionButton1;
    FloatingActionButton floatingActionButton2;
    FloatingActionButton floatingActionButton3;
    FloatingActionButton floatingActionButton4;
    FloatingActionButton floatingActionButton5;

    String buttonTagSelected = "";

    String Uid = "";

    Uri selectedimage;

    boolean scaleUp = false;

    private StorageReference mStorageRef;
    DatabaseReference mDatabase;

    HashMap<String, Uri> dogPictureHashMap = new HashMap<>();

    int numberOfDogs = 0;

    boolean[] isImageOccupied = new boolean[6];

    final long ONE_MEGABYTE = 1024 * 1024;

    String[] photoNamesArray = {"floatingMainActionButton","floatingActionButton1", "floatingActionButton2", "floatingActionButton3", "floatingActionButton4", "floatingActionButton5"};


    public void changePicture(View v){
        buttonTagSelected = (String) v.getTag();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }else{
                Log.i("The Views ID: ", (String) v.getTag());
                getPhoto();
            }
        }

    }


    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_menu, menu);
        Log.i("MENu:", "MENu is being inflated");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(dogNameEditText.getText().toString().isEmpty() || dogBreedEditText.getText().toString().isEmpty() || dogDescriptionEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        }else {

            switch (item.getItemId()) {
                case R.id.done:
                    Dog newDog = new Dog(dogNameEditText.getText().toString(), dogBreedEditText.getText().toString(), dogDescriptionEditText.getText().toString(), Uid);
                    String dogId = mDatabase.child("users").child(Uid).child("DogsList").push().getKey();
                    Log.i("THE KEY: ", dogId);
                    mDatabase.child("users").child(Uid).child("DogsList").child(String.valueOf(numberOfDogs)).setValue(dogId);
                    mDatabase.child("users").child(Uid).child("numberOfDogs").setValue(numberOfDogs + 1);
                    mDatabase.child("dogs").child(dogId).setValue(newDog);

                    Iterator it = dogPictureHashMap.entrySet().iterator();
                    while (it.hasNext()) {
                        HashMap.Entry pair = (HashMap.Entry) it.next();
                        Log.i(String.valueOf(pair.getKey()), " = " + pair.getValue());
                        StorageReference dogStorageReference = mStorageRef.child(Uid).child(dogId).child(String.valueOf(pair.getKey()));
                        dogStorageReference.putFile((Uri) pair.getValue());
                        it.remove(); // avoids a ConcurrentModificationException


                    }

                    // StorageReference dogRef = mStorageRef.child("DogPhotos").child(Uid).child(String.valueOf(numberOfDogs)).child(dogPictureHashMap.);

                    //might be a bit dangerous, worried that finish() will call before a users with shitty internet fully executes the above writing to the databse. Finish()
                    //You finish Activity class with finish() method when you need to end the Activity immediatly at specific point. If you press back button, you will go
                    // //through the lifecycle (onPause(), onStop() then onDestroy()) automatically.
                    finish();
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);

        //setting up the top action bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Add a dog");
        setSupportActionBar(myToolbar);

        Uid = getIntent().getStringExtra("uid");
        Log.i("The Uid is :" ,Uid);


        dogMainPictureImageView = (ImageView)findViewById(R.id.dogMainPictureImageView);
        dogPictureImageView1 = (ImageView)findViewById(R.id.dogPictureImageView1);
        dogPictureImageView2 = (ImageView)findViewById(R.id.dogPictureImageView2);
        dogPictureImageView3 = (ImageView)findViewById(R.id.dogPictureImageView3);
        dogPictureImageView4 = (ImageView)findViewById(R.id.dogPictureImageView4);
        dogPictureImageView5 = (ImageView)findViewById(R.id.dogPictureImageView5);

        floatingMainActionButton = (FloatingActionButton)findViewById(R.id.floatingMainActionButton);
        floatingActionButton1 = (FloatingActionButton)findViewById(R.id.floatingActionButton1);
        floatingActionButton2 = (FloatingActionButton)findViewById(R.id.floatingActionButton2);
        floatingActionButton3 = (FloatingActionButton)findViewById(R.id.floatingActionButton3);
        floatingActionButton4 = (FloatingActionButton)findViewById(R.id.floatingActionButton4);
        floatingActionButton5 = (FloatingActionButton)findViewById(R.id.floatingActionButton5);

        dogNameEditText = (EditText)findViewById(R.id.dogNameEditText);
        dogBreedEditText = (EditText) findViewById(R.id.dogBreedEditText);
        dogDescriptionEditText = (EditText)findViewById(R.id.dogDescriptionEditText);


        mStorageRef = FirebaseStorage.getInstance().getReference();



        // TODO: 2017-06-25 Fix this version problem
        //rememeber that this might not work on some versions


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(Uid).child("numberOfDogs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                numberOfDogs = Integer.parseInt(dataSnapshot.getValue().toString());
                Log.i("Number of dogs: ", String.valueOf(numberOfDogs));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for(final String photoName : photoNamesArray) {

            StorageReference getDogPicturesStorageReference = mStorageRef.child(Uid).child(String.valueOf(numberOfDogs)).child(photoName);

            getDogPicturesStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap dogPictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    switch (photoName){
                        case "floatingMainActionButton":
                            dogMainPictureImageView.setImageBitmap(Bitmap.createScaledBitmap(dogPictureBitmap, dogMainPictureImageView.getWidth(), dogMainPictureImageView.getHeight(), false));
                            break;
                        case "floatingActionButton1":
                            dogPictureImageView1.setImageBitmap(dogPictureBitmap);
                            break;
                        case "floatingActionButton2":
                            dogPictureImageView2.setImageBitmap(dogPictureBitmap);
                            break;
                        case "floatingActionButton3":
                            dogPictureImageView3.setImageBitmap(dogPictureBitmap);
                            break;
                        case "floatingActionButton4":
                            dogPictureImageView4.setImageBitmap(dogPictureBitmap);
                            break;
                        case "floatingActionButton5":
                            dogPictureImageView5.setImageBitmap(dogPictureBitmap);
                            break;
                        default:
                            Log.i("INFO: ", "Couldn't get a picture from cloud (Switch statement)");

                    }
                    // Data for "images/island.jpg" is returns, use this as needed
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){

            selectedimage = data.getData();
            Log.i("DOES IS GET HERE:", "Return from media store");
            //dogPictureHashMap.put(buttonTagSelected, selectedimage);
            cropImage();

        }else if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            try {
                Log.i("BEfore bundle", "true");
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                Log.i("After bundle", "true");

//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                File file = createImageFile();
                if (file != null) {
                    FileOutputStream fout;
                    try {
                        fout = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                        fout.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Uri uri = Uri.fromFile(file);
                    dogPictureHashMap.put(buttonTagSelected, uri);
                }




                //RoundedBitmapDrawable roundedPicture = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                //roundedPicture.setCornerRadius(Math.max(profilePictureBitmap.getWidth(), profilePictureBitmap.getHeight()) / 2.0f);
                // roundedPicture.setCircular(true);

                for(int i = 0; i < 6; i++){
                    if(!isImageOccupied[i]){
                        switch (i){
                            case 0:
                                Log.i("DOES IS GET HERE:", "YUP");
                                if(scaleUp) {
                                    Log.i("The image size:", String.valueOf(dogMainPictureImageView.getWidth()));

                                    dogMainPictureImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, dogMainPictureImageView.getWidth(), dogMainPictureImageView.getHeight(), false));
                                    scaleUp = false;
                                    isImageOccupied[0] = true;
                                }else{
                                    dogMainPictureImageView.setImageBitmap(bitmap);
                                }
                                break;
                            case 1:
                                dogPictureImageView1.setImageBitmap(bitmap);

//                                Glide.with(this)
//                                        .load(stream.toByteArray())
//                                        .into(dogPictureImageView1);

                                isImageOccupied[1] = true;
                                break;
                            case 2:
                                dogPictureImageView2.setImageBitmap(bitmap);
//                                Glide.with(this)
//                                        .load(stream.toByteArray())
//                                        .into(dogPictureImageView2);
                                isImageOccupied[2] = true;
                                break;
                            case 3:
                                dogPictureImageView3.setImageBitmap(bitmap);
                                isImageOccupied[3] = true;
                                break;
                            case 4:
                                dogPictureImageView4.setImageBitmap(bitmap);
                                isImageOccupied[4] = true;
                                break;
                            case 5:
                                dogPictureImageView5.setImageBitmap(bitmap);
                                isImageOccupied[5] = true;
                                break;
                            default:
                                Log.i("INFO: ", "Couldn't set or get a picture (Switch statement)");
                                Toast.makeText(this, "Couldn't set that image, try again", Toast.LENGTH_SHORT).show();

                        }
                        break;
                    }
                }
                Log.i("image", "successful");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(requestCode == 2 && resultCode == RESULT_CANCELED){
            Log.i("Crop image:", " CANCELLED");
        }
    }

    private void cropImage() {
        int width = 290;
        int height = 290;

        if(buttonTagSelected.equals("floatingMainActionButton")){
            if(dogMainPictureImageView.getWidth() >= 500){
                width = 500;
                height = 500;
                scaleUp = true;
            }else {
                width = dogMainPictureImageView.getWidth();
                height = dogMainPictureImageView.getHeight();
            }

        }else{
            width = dogPictureImageView1.getWidth();
            height = dogPictureImageView1.getHeight();
        }

        try{
            Log.i("Width: ", String.valueOf(width));
            Log.i("height: ", String.valueOf(height));
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setData(selectedimage); // location of the image, can be empty
            intent.putExtra("outputX", width); // int
            intent.putExtra("outputY", height); // int
            intent.putExtra("scale", true); // boolean
            intent.putExtra("scaleUpIfNeeded", true); // boolean
            intent.putExtra("aspectX", 1); // int
            intent.putExtra("aspectY", 1); // int
            //intent.putExtra("spotlightX", spotlightX); // int
            //intent.putExtra("spotlightY", spotlightY); // int
            intent.putExtra("return-data", true); // boolean
            //intent.putExtra("output", output); // string
            //intent.putExtra("set-as-wallpaper", setAsWallpaper); // boolean
            //intent.putExtra("showWhenLocked", showWhenLocked); // boolean
           // intent.putExtra("outputFormat", Uri.PARCELABLE_WRITE_RETURN_VALUE); // String
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 2);
            }
        }catch (Exception e){
            Log.i("Startingthecrop INtent:", "FAILED");
        }
    }

    //creating a file to save the bitmap in
    public File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mFileTemp = null;
        String root= this.getDir("my_sub_dir", Context.MODE_PRIVATE).getAbsolutePath();
        Log.i("The root: " , root);
        File myDir = new File(root + "/Img");
        if(!myDir.exists()){
            myDir.mkdirs();
        }
        try {
            mFileTemp=File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return mFileTemp;
    }

}
