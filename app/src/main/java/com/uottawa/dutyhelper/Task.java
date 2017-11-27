package com.uottawa.dutyhelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hamza on 11/7/2017.
 */

public class Task {

    private String mId;
    private String mTitle;
    private String mDescription;
    private String mDueDate;
    private String mStatus;
    private List<String> mAssignedUsers;
    private List<String> mItemsNeeded;

    public Task() {
        //necessary blank constructor
    }

    public Task(String id, String title, String description, String dueDate) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mDueDate = dueDate;
        mStatus = "incomplete";
        mAssignedUsers = new ArrayList<>();
        mItemsNeeded = new ArrayList<>();
    }
    public Task(String id, String title, String description, String dueDate, String status) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mDueDate = dueDate;
        mStatus = status;
        mAssignedUsers = new ArrayList<>();
        mItemsNeeded = new ArrayList<>();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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

    public String getDueDate() {
        return mDueDate;
    }

    public void setDueDate(String dueDate) {
        mDueDate = dueDate;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public List<String> getAssignedUsers() {
        return mAssignedUsers;
    }

    public void setAssignedUsers(List<String> assignedUsers) {
        mAssignedUsers = assignedUsers;
    }

    public List<String> getItemsNeeded() {
        return mItemsNeeded;
    }

    public void setItemsNeeded(List<String> itemsNeeded) {
        mItemsNeeded = itemsNeeded;
    }
}
