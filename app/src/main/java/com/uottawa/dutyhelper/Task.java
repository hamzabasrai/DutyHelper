package com.uottawa.dutyhelper;

import java.util.Date;
import java.util.List;

/**
 * Created by Hamza on 11/7/2017.
 */

public class Task {
    private String mId;
    private String mTitle;
    private String mDescription;
    private String mDeadline;
    private List<User> mAssignedUsers;
    private String date;

    public Task() {
        //necessary blank constructor
    }

    public Task(String id, String title, String description, String date) {
        mId = id;
        mTitle = title;
        mDescription = description;
        this.date = date;
    }

    public Task(String id, String title, String description){
        mId = id;
        mTitle = title;
        mDescription = description;
    }

    public String getId() {
        return mId;
    }

    public void setId(String Id) {
        mId = Id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDeadline() {
        return mDeadline;
    }

    public void setDeadline(String deadline) {
        mDeadline = deadline;
    }

    public List<User> getAssignedUsers() {
        return mAssignedUsers;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
        mAssignedUsers = assignedUsers;
    }
}
