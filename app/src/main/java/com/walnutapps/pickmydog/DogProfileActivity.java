package com.walnutapps.pickmydog;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class DogProfileActivity extends AppCompatActivity {

    ImageView dogMainPictureImageView;
    ImageView dogPictureImageView1;
    ImageView dogPictureImageView2;
    ImageView dogPictureImageView3;
    ImageView dogPictureImageView4;
    ImageView dogPictureImageView5;

    FloatingActionButton floatingMainActionButton;
    FloatingActionButton floatingActionButton1;
    FloatingActionButton floatingActionButton2;
    FloatingActionButton floatingActionButton3;
    FloatingActionButton floatingActionButton4;
    FloatingActionButton floatingActionButton5;

    String buttonTagSelected = null;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_profile);

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

        // TODO: 2017-06-25 Fix this version problem
        //rememeber that this might not work on some versions

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){

            Uri selectedimage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedimage);
                //RoundedBitmapDrawable roundedPicture = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                //roundedPicture.setCornerRadius(Math.max(profilePictureBitmap.getWidth(), profilePictureBitmap.getHeight()) / 2.0f);
               // roundedPicture.setCircular(true);

                switch(buttonTagSelected){
                    case "floatingMainActionButton":
                        dogMainPictureImageView.setImageBitmap(bitmap);

                        // TODO: 2017-06-25 inflate the xml of the profile activity in order to acess its imageview from here
//                        RoundedBitmapDrawable roundedPicture = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
//                        roundedPicture.setCircular(true);
//                        ImageView profilePageDogImageView = (ImageView)findViewById(R.id.dogPictureImageView);
//                        profilePageDogImageView.setImageBitmap(bitmap);
                        break;
                    case "floatingActionButton1":
                        dogPictureImageView1.setImageBitmap(bitmap);
                        break;
                    case "floatingActionButton2":
                        dogPictureImageView2.setImageBitmap(bitmap);
                        break;
                    case "floatingActionButton3":
                        dogPictureImageView3.setImageBitmap(bitmap);
                        break;
                    case "floatingActionButton4":
                        dogPictureImageView4.setImageBitmap(bitmap);
                        break;
                    case "floatingActionButton5":
                        dogPictureImageView5.setImageBitmap(bitmap);
                        break;
                    default:
                        Log.i("INFO: ", "Couldn't set or get a picture (Switch statement)");
                        Toast.makeText(this, "Couldn't set that image, try again", Toast.LENGTH_SHORT).show();
                }
                Log.i("image", "successful");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.i("error", "no data");
        }
    }
}
