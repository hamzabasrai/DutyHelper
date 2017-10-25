package com.uottawa.dutyhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }


    String userID;
    String firstName;
    String lastName;
    String age;

    public signup(){

    }

    public signup(String userID, String firstName, String lastName, String age) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public String getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAge() {
        return age;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
