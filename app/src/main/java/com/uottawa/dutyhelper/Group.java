package com.uottawa.dutyhelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amar Jasarbasic on 2017-11-10.
 */


public class Group {
    private List<String> users;
    private List<String> tasks;
    private String groupName;

    public Group(String groupName) {

        this.users = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.groupName = groupName;

    }

    public List<String> getUsers() {
        return users;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
