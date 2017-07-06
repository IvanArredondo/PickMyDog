package com.walnutapps.pickmydog;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ToggleButton phoneToggleButton;
    ToggleButton emailToggleButton;
    Boolean phoneChecked = true;

    EditText phoneEditText;
    EditText emailEditText;
    EditText fullNameEditText;
    EditText passwordEditText;


    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseUser currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        final View phoneOptionView = (View)findViewById(R.id.phoneOptionView);
        final View emailOptionView = (View)findViewById(R.id.emailOptionView);

        phoneToggleButton = (ToggleButton) findViewById(R.id.phoneToggleButton);
        emailToggleButton = (ToggleButton)findViewById(R.id.emailToggleButton);

        phoneEditText = (EditText)findViewById(R.id.phoneEditText);
        emailEditText = (EditText)findViewById(R.id.emailEditText);
        fullNameEditText = (EditText)findViewById(R.id.fullNamEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        phoneToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                phoneChecked = true;

                phoneOptionView.setVisibility(View.VISIBLE);

                emailOptionView.setVisibility(View.INVISIBLE);

                emailEditText.setVisibility(View.INVISIBLE);

                phoneEditText.setVisibility(View.VISIBLE);

            }
        });

        emailToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                phoneChecked = false;

                emailOptionView.setVisibility(View.VISIBLE);

                phoneOptionView.setVisibility(View.INVISIBLE);

                phoneEditText.setVisibility(View.INVISIBLE);
                emailEditText.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            //currentUser.reload();
            Log.i("verification status: ", String.valueOf(currentUser.isEmailVerified()));
        }
        updateUI(currentUser);
    }


    public void signUpUser(View v){

        Log.d("email: ", "signIn:" + emailEditText.getText().toString());

        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Authenticating", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser(user);
                            sendEmailVerification();
                            Toast.makeText(SignUpActivity.this, "email verification sent", Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Authenticating", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Please enter valid email",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...

                    }
                });
    }

    private void sendEmailVerification() {
        // Disable button

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button

                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                                //currentUser = user;
                                //updateUI(user);
                        } else {
                            Log.e("email verification", "sendEmailVerification", task.getException());
                            Toast.makeText(SignUpActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void updateUI(FirebaseUser user) {

        // TODO: 2017-07-03 add check for email verification
        //&& user.isEmailVerified() add to the if statement
        if (user != null ) {
            Intent intent = new Intent(getApplicationContext(),TabbedActivity.class);
            intent.putExtra("Uid", user.getUid());
            Log.i("User params: ", user.getUid() + "  " + user.getDisplayName() + "  " + user.getEmail() + "  " + user.getProviderId());
            startActivity(intent);        //comment this out when you want to test the sign in activity

        }else{
            Log.i("User info: " , "Firebase user = null");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    private void writeNewUser(FirebaseUser user) {

        String name = fullNameEditText.getText().toString();
        String email = user.getEmail();
        String uid = user.getUid();
        int numberOfDogs = 0;


        Log.i("USER :  ", uid + "  ADDED TO DATABASE");


        User newUser = new User(uid, name, email, "To be Determined", numberOfDogs);
        mDatabase.child("users").child(uid).setValue(newUser);
        Log.i("USER :  ", "ADDED TO DATABASE");
    }
}
