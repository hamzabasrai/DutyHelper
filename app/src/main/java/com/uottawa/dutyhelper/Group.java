package com.uottawa.dutyhelper;

import java.util.LinkedList;

/**
 * Created by Amar Jasarbasic on 2017-11-10.
 */


public class Group {
    protected LinkedList<User> users;
    protected LinkedList<Task> tasks;
    protected String groupName;

    public Group(String groupName){

        this.users = new LinkedList<User>();
        this.tasks = new LinkedList<Task>();
        this.groupName = groupName;


    }

    public LinkedList<User> getUsers() {
        return users;
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setUsers(LinkedList<User> users) {
        this.users = users;
    }

    public void setTasks(LinkedList<Task> tasks) {
        this.tasks = tasks;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
