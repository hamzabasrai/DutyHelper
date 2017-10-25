package com.uottawa.dutyhelper;

/**
 * Created by pfara on 10/24/2017.
 */

public class User {
    String userID;
    String firstName;
    String lastName;
    String age;

    public User(){

    }

    public User(String userID, String firstName, String lastName, String age) {
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
