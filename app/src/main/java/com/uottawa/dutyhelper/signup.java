package com.uottawa.dutyhelper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mFirstName;
    private EditText mLastName;
    private String uid;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private Button mSignUp;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("users");


        mEmailField = (EditText) findViewById(R.id.field_email_1);
        mPasswordField = (EditText) findViewById(R.id.field_password_1);

        mFirstName = (EditText) findViewById(R.id.field_firstname);
        mLastName = (EditText) findViewById(R.id.field_lastname);
        mSignUp = (Button) findViewById(R.id.signUp_btn_1);

        findViewById(R.id.signUp_btn_1).setOnClickListener(this);


    }

    private void createAccount() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }else{
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signup.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        String uid = mRef.push().getKey();
        User user = new User(uid, mFirstName.getText().toString(), mLastName.getText().toString(),
                email);
            Toast.makeText(signup.this, user.getid(),
                    Toast.LENGTH_SHORT).show();
        mRef.child(uid).setValue(user);
        }
    }

    public boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        }
        if (TextUtils.isEmpty(mFirstName.getText().toString())) {
            mFirstName.setError("Required.");
            valid = false;
        }
        if (TextUtils.isEmpty(mLastName.getText().toString())) {
            mLastName.setError("Required.");
            valid = false;
        }

        if (mPasswordField.getText().toString().length() < 8) {
            mPasswordField.setError("Must be longer than 8 characters");
            valid = false;
        } else {
            mEmailField.setError(null);
            mPasswordField.setError(null);
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUp_btn_1) {
            createAccount();
        }
    }
}
