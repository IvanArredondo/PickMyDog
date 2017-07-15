package com.walnutapps.pickmydog;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.data;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    TextView logoTextView;

    //Declaring database reference
    DatabaseReference mDatabase;



    public void signupRedirect(View view){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Done in order to allow querying facebook to grab the profile picture;
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Not sure if needed
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //offline capabilities enabled
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        //Set PickMyDog logo and font
        logoTextView = (TextView)findViewById(R.id.logoTextView);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Pacifico-Regular.ttf");
        logoTextView.setTypeface(custom_font);

        //initializing local database;
        //DatabaseHandler localDB = new DatabaseHandler(this);

        //initialize cloud database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);


        //Initialize facebook login Button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("info: ", "facebook:onSuccess: " + loginResult.getAccessToken().getUserId());
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d("Info: ", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("info: ", "facebook:onError", error);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("main pge verificatin: ", String.valueOf(currentUser.isEmailVerified()));
        }
        updateUI(currentUser);      //comment out when you want to test the sign in activity
    }

    /*
    Check if user is signed in (non-null) and update UI accordingly.
    */
    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        // TODO: 2017-07-03 check if email sign up that it is email verified
        //add && user.isEmailVerified() to if statement
        if(user != null){
            Intent intent = new Intent(getApplicationContext(),TabbedActivity.class);
            intent.putExtra("Uid", user.getUid());
            Log.i("User params: ", user.getUid() + "  " + user.getDisplayName() + "  " + user.getEmail() + "  " + user.getProviderId());
            startActivity(intent);        //comment this out when you want to test the sign in activity
        } else {
            checkLocalDatabase();
            Log.i("User info: " , "Firebase user = null");

        }

    }

    private void checkLocalDatabase() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(final AccessToken token){
        Log.d("facebook Token: ", " " + token );

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("facebook sign in: ", " SUCCESS");
                            FirebaseUser user = mAuth.getCurrentUser();
                            storeFacebookProfileInDatabase(token);
                            Log.i("USER :  ", "ADDED TO DATABASE" +user.getUid());
                        }else{
                            Log.d("facebook sign in: ", "FAILURE");
                            Toast.makeText(MainActivity.this, "Authentication  failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //will store the users profile picture the first time the log in. Done because dont have access to facebook once they open and close the app (b/c it goes straight to the next activity)
    private void storeFacebookProfileInDatabase(AccessToken token) {
        Bundle params = new Bundle();
        params.putString("fields", "id,email,gender,cover,picture.type(large)");
        /* make the API call */
        new GraphRequest(
                token,
                "me",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();
                                if (data.has("picture")) {
                                    URL profilePicUrl = new URL(data.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    HttpsURLConnection conn1 = (HttpsURLConnection) profilePicUrl.openConnection();
                                    conn1.connect();
                                    InputStream inputStream = conn1.getInputStream();
                                    Bitmap profilePicBitMap = BitmapFactory.decodeStream(inputStream);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    profilePicBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] bytes = baos.toByteArray();
                                    Log.i("THE BYTES: ", " " + bytes.toString());
                                    String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

                                    writeNewUser(mAuth.getCurrentUser(), base64Image);
                                    Log.i("FACEBOOK PICTURE GOT : ", " " + response.toString());
                                }else{
                                    Log.i("FACEBOOK PICTURE GOT : ", " IN THE ELSE");
                                }
                            } catch (Exception e) {
                                Log.i("FACEBOOK PICTURE GOT : ", " IN THE EXCEPTION");
                                e.printStackTrace();
                            }
                        }
                      //  Log.i("FACEBOOK PICTURE GOT : ", " " + response.toString());
                    }
                }
        ).executeAsync();



    }

    public void signOut(){
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        updateUI(null);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void writeNewUser(FirebaseUser user, String profilePicture) {

        String name = user.getDisplayName();
        String email = user.getEmail();
        String uid = user.getUid();
        int numberOfDogs = 0;


        Log.i("USER :  ", uid + "  ADDED TO DATABASE");


        User newUser = new User(uid, name, email, profilePicture, numberOfDogs);
        mDatabase.child("users").child(uid).setValue(newUser);
        Log.i("USER :  ", "ADDED TO DATABASE");

        updateUI(user);
    }



}


