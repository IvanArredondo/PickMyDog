package com.walnutapps.pickmydog;

import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    public void signupRedirect(View view){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //Initialize facebook login Button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("info: ", "facebook:onSuccess: " + loginResult);
                //handleFacebookAccessToken(loginResult.getAccessToken());
                // TODO: 2017-06-22  

            }

            @Override
            public void onCancel() {
                Log.d("Info: ", "facebook:onCancel");
                // TODO: 2017-06-22  
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("info: ", "facebook:onError", error);
                // TODO: 2017-06-22
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){

        }else{
            Log.i("Info: ", "Not signed in");
        }

        TextView logoTextView = (TextView)findViewById(R.id.logoTextView);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Pacifico-Regular.ttf");

        logoTextView.setTypeface(custom_font);

        //first commit

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
