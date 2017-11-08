package com.uottawa.dutyhelper;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Hamza on 11/7/2017.
 */

public class Task {
    private UUID mId;
    private String mDescription;
    private Date mDeadline;
    private List<User> mAssignedUsers;

    public Task(String description, Date deadline, List<User> assignedUsers) {
        mId = UUID.randomUUID();
        mDescription = description;
        mDeadline = deadline;
        mAssignedUsers = assignedUsers;
    }

    public UUID getId() {
        return mId;
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
