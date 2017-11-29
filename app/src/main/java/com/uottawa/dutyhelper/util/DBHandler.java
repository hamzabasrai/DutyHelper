package com.uottawa.dutyhelper.util;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.dutyhelper.model.Group;
import com.uottawa.dutyhelper.model.Task;
import com.uottawa.dutyhelper.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hamza on 11/27/2017.
 *
 * Sinlgeton class to be used for all Firebase queries
 */

public class DBHandler {

    private static DBHandler sDBHandler;

    private DatabaseReference mDatabaseTasks;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseGroups;

    private List<Task> mTaskList;
    private List<User> mUserList;
    private List<Group> mGroupList;

    public static DBHandler get() {
        if (sDBHandler == null) {
            sDBHandler = new DBHandler();
        }
        return sDBHandler;
    }

    private DBHandler() {
        mDatabaseTasks = FirebaseDatabase.getInstance().getReference("tasks");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseGroups = FirebaseDatabase.getInstance().getReference("groups");

        mTaskList = new ArrayList<>();
        mUserList = new ArrayList<>();
        mGroupList = new ArrayList<>();
    }


    public List<Task> getTasks() {
        mDatabaseTasks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTaskList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    mTaskList.add(task);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mTaskList;
    }

    public List<User> getUsers() {
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUserList.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mUserList;
    }

    public List<Group> getGroups() {
        mDatabaseGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGroupList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    mGroupList.add(group);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mGroupList;
    }

    public void addTask(Task task) {
        String taskId = mDatabaseTasks.push().getKey();
        task.setId(taskId);
        mDatabaseTasks.child(taskId).setValue(task);
        if(task.getAssignedUsers()!=null){
            for (String assigneeId : task.getAssignedUsers()) {
                User user = getUser(assigneeId);
                user.getAssignedTasks().add(taskId);
                mDatabaseUsers.child(assigneeId).setValue(user);
        }

        }
    }

    public void updateTask(Task task) {
        DatabaseReference taskRef = mDatabaseTasks.child(task.getId());
        taskRef.setValue(task);
    }

    public void deleteTask(String taskId) {
        mDatabaseTasks.child(taskId).removeValue();
    }

    public void addUser(User user) {
        mDatabaseUsers.child(user.getId()).setValue(user);
    }

    public void createGroup(Group group) {
        String groupId = mDatabaseUsers.push().getKey();
        group.setId(groupId);
        mDatabaseUsers.child(groupId).setValue(group);

        for (String userId : group.getUsers()) {
            mDatabaseUsers.child(userId).child("group").setValue(groupId);
        }
    }

    public void updateGroup(Group group) {
        DatabaseReference groupRef = mDatabaseGroups.child(group.getId());
        groupRef.setValue(group);

        for (String userId : group.getUsers()) {
            mDatabaseUsers.child(userId).child("group").setValue(group.getId());
        }
    }

    public void deleteGroup(String groupId) {
        mDatabaseGroups.child(groupId).removeValue();
    }

    public Task getTask(String taskId) {
        getTasks();
        for (Task task : mTaskList) {
            if (task.getId().equals(taskId)) {
                return task;
            }
        }
        return null;
    }

    public User getUser(String userId) {
        getUsers();
        for (User user : mUserList) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public Group getGroup(String groupId) {
        getGroups();
        for (Group group : mGroupList) {
            if(group.getId().equals(groupId)) {
                return group;
            }
        }
        return null;
    }



}
