package com.uottawa.dutyhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uottawa.dutyhelper.R;
import com.uottawa.dutyhelper.model.User;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordConfirmField;
    private EditText mFirstName;
    private EditText mLastName;

    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mPasswordConfirmLayout;
    private TextInputLayout mFirstNameLayout;
    private TextInputLayout mLastNameLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;


    private Button mSignUp;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");


        mFirstName = (EditText) findViewById(R.id.first_name_edit_text);
        mLastName = (EditText) findViewById(R.id.last_name_edit_text);
        mEmailField = (EditText) findViewById(R.id.email_edit_text);
        mPasswordField = (EditText) findViewById(R.id.password_edit_text);
        mPasswordConfirmField = (EditText) findViewById(R.id.password_confirm_edit_text);

        mEmailLayout = (TextInputLayout) findViewById(R.id.email_input_layout);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.password_input_layout);
        mPasswordConfirmLayout = (TextInputLayout) findViewById(R.id.password_confirm_input_layout);
        mFirstNameLayout = (TextInputLayout) findViewById(R.id.task_name);
        mLastNameLayout = (TextInputLayout) findViewById(R.id.last_name_input_layout);

        mSignUp = (Button) findViewById(R.id.btn_sign_up);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFirstNameLayout.setError(null);
                mLastNameLayout.setError(null);
                mEmailLayout.setError(null);
                mPasswordLayout.setError(null);
                mPasswordConfirmLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mFirstName.addTextChangedListener(textWatcher);
        mLastName.addTextChangedListener(textWatcher);
        mEmailField.addTextChangedListener(textWatcher);
        mPasswordField.addTextChangedListener(textWatcher);
        mPasswordConfirmField.addTextChangedListener(textWatcher);
    }

    private void createAccount() {
        if (isValidForm()) {

            final String email = mEmailField.getText().toString();
            final String password = mPasswordField.getText().toString();
            final String firstName = mFirstName.getText().toString();
            final String lastName = mLastName.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign up success, sign-in with new user
                        mAuth.signInWithEmailAndPassword(email,password);

                        //Add user to database
                        String id = mAuth.getCurrentUser().getUid();
                        User user = new User(id, firstName, lastName, email);
                        mDatabaseUsers.child(id).setValue(user);

                        //Send user to task list
                        Intent sendToCurrentTaskList = new Intent(SignUpActivity.this, TaskListActivity.class);
                        startActivity(sendToCurrentTaskList);
                        Toast.makeText(SignUpActivity.this, "Welcome "+ firstName, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignUpActivity.this, R.string.toast_sign_up_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean isValidForm() {

        boolean isValid = true;

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String passwordConfirm = mPasswordConfirmField.getText().toString();
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmailLayout.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameLayout.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (TextUtils.isEmpty(lastName)) {
            mLastNameLayout.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            mPasswordConfirmLayout.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (password.length() < 8) {
            mPasswordLayout.setError(getString(R.string.error_password_length));
            mPasswordConfirmLayout.setError(getString(R.string.error_password_length));
            isValid = false;
        }
        if (!password.equals(passwordConfirm)) {
            mPasswordLayout.setError(getString(R.string.error_password_mismatch));
            mPasswordConfirmLayout.setError(getString(R.string.error_password_mismatch));
            isValid = false;
        }

        return isValid;
    }
}
