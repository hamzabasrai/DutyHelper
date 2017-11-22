package com.uottawa.dutyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private Button mLoginButton;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initializing layout views
        mEmailField = (EditText) findViewById(R.id.email_edit_text);
        mPasswordField = (EditText) findViewById(R.id.password_edit_text);
        mEmailLayout = (TextInputLayout) findViewById(R.id.email_input_layout);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.password_input_layout);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mSignUpButton = (Button) findViewById(R.id.btn_sign_up);

        //Initializing OnClickListener
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpScreen = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpScreen);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmailLayout.setError(null);
                mPasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmailLayout.setError(null);
                mPasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
//            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
       //
    }

    private void login() {
        if (validateForm()) {
            String password = mPasswordField.getText().toString();
            String email = mEmailField.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(LoginActivity.this, TaskListActivity.class);
                        startActivity(intent);
                    } else {
                        mEmailLayout.setError("Incorrect Email or Password");
                        mPasswordLayout.setError("Incorrect Email or Password");
                    }
                }
            });
        }
    }

    public boolean validateForm() {
        boolean isValid = true;
        String password = mPasswordField.getText().toString();
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailLayout.setError("Required Field");
            isValid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isEmpty(email)) {
            mEmailLayout.setError("Invalid Email");
            isValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError("Required Field");
            isValid = false;
        }
        return isValid;
    }
}
