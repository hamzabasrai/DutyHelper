package com.uottawa.dutyhelper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.util.Log;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";
    //initialzie Firebase Auth
    private FirebaseAuth mAuth;
    //create edit text field for user and pass
    private EditText mEmailField;
    private EditText mPasswordField;
    //sign-up button
    public Button signUpButton;

    //a method that defines the functionality of the sign-up button in the main activity
    //called in onCreate method of MainActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initializing user and password
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        //initializing Onclick listener which is implemented in this class
        findViewById(R.id.signIn_btn).setOnClickListener(this);
        findViewById(R.id.signUp_btn).setOnClickListener(this);

    }


    @Override
    public void onStart() {
        super.onStart();
        // checks if user already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    //need to update this to change activities probably
    private void updateUI(FirebaseUser currentUser) {
        return;
    }


    //sign in method
    public void signIn() {
        //forebase sign in function
        mAuth.signInWithEmailAndPassword(mEmailField.getText().toString(), mPasswordField.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, task.getException().toString() + mEmailField.toString(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //create an account we should maybe make an intent to a new activity to fill out all user info


    //makes sure the inputed text is correctly inputed
    public  boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUp_btn) {
            Intent signUpScreen = new Intent(MainActivity.this, signup.class);
            startActivity(signUpScreen);
        } else if (i == R.id.signIn_btn) {
            signIn();
        } //else if (i == R.id.sign_out_button) {
        //signOut();
        //}
    }
}
