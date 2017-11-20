package com.uottawa.dutyhelper;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Hamza on 11/7/2017.
 */

public class Task {
    //private UUID mId;
    private String mTitle;
    private String mDescription;
    private Date mDeadline;
    private List<User> mAssignedUsers;

    public Task(){
        //blank constructor
    }

    public Task(String title, String description) {
        //mId = UUID.randomUUID();
        mTitle = title;
        mDescription = description;
    }

    //public UUID getId() {
        //return mId;
    //}

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

    public Date getDeadline() {
        return mDeadline;
    }

    public void setDeadline(Date deadline) {
        mDeadline = deadline;
    }

    public List<User> getAssignedUsers() {
        return mAssignedUsers;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
        mAssignedUsers = assignedUsers;
    }
}
