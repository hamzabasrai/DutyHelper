package com.uottawa.dutyhelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfara on 10/25/2017.
 */

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String group;
    private int points;
    private List<String> assignedTasks;

    public User(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.group = null;
        this.points = 0;
        this.assignedTasks = new ArrayList<>();
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<String> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(List<String> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }
}

