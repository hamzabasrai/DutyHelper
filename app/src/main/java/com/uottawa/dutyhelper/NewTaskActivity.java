package com.uottawa.dutyhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewTaskActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseTasks;
    private EditText mTaskTitle;
    private EditText mTaskDescription;
    private EditText mTaskDueDate;
    private RadioGroup mTaskStatus;
    private DatePicker mDatePicker;
    private Button mbtn_assign_user;
    private DatabaseReference mDatabaseUsers;
    private List<String> appUsers;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private boolean[] checkedUsers;
    private ArrayList<Integer> selectedAppUsers = new ArrayList<>();
    private String[] appUsersArray;
    int counter = 0;
    private Task task;
    private List<String> appUsersID;
    ArrayList<String> assignedUsers;

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appUsers.clear();
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    User user = postSnapShot.getValue(User.class);
                    appUsers.add(user.getFirstName() + user.getLastName());
                    appUsersID.add(user.getId());

                }
                adapter = new ArrayAdapter<String>(NewTaskActivity.this, android.R.layout.simple_list_item_multiple_choice,appUsers);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        final View userListView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.users_item,null);
        listView = (ListView) userListView.findViewById(R.id.users_list_view);

        appUsersID = new ArrayList<>();
        task = new Task();
        mDatabaseTasks = FirebaseDatabase.getInstance().getReference("Tasks");
        mTaskTitle = (EditText) findViewById(R.id.task_name);
        mTaskDescription = (EditText) findViewById(R.id.task_description);
        mbtn_assign_user = (Button) findViewById(R.id.btn_assign_user);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        /*mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        appUsers = new ArrayList<>();
        checkedUsers = new boolean[appUsers.size()];
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        mbtn_assign_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View dialogUsers = LayoutInflater.from(NewTaskActivity.this).inflate(R.layout.users_item, null);
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(NewTaskActivity.this);
                builder.setTitle("Assign Users").setView(listView);

                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SparseBooleanArray checkedUsers = listView.getCheckedItemPositions();
                        assignedUsers = new ArrayList<>();
                        for(int i = 0; i<checkedUsers.size(); i++){
                            if(checkedUsers.get(i) == true){
                                assignedUsers.add(appUsersID.get(i));
                            }
                            //Log.d("NewTaskActivity", "Users + " + appUsers.get(i) + "is Selected : " + String.valueOf(checkedUsers.get(i)) );
                        }
                        task.setAssignedUsers(assignedUsers);
                    }
                });

                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(int j = 0; j<checkedUsers.length; j++){
                            checkedUsers[j] = false;
                            selectedAppUsers.clear();
                        }
                    }
                });
                AlertDialog mDialog = builder.create();
                mDialog.show();

            }
        });


        mTaskDueDate = (EditText) findViewById(R.id.due_date);
        mTaskDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(NewTaskActivity.this).inflate(R.layout.dialog_date_picker, null);
                mDatePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                mDatePicker.init(year, month, day, null);

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(NewTaskActivity.this);
                builder.setTitle("Set Due Date")
                        .setView(dialogView)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth() + 1;
                                int day = mDatePicker.getDayOfMonth();
                                mTaskDueDate.setText(day+"/"+month+"/"+year);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
        mTaskStatus = (RadioGroup) findViewById(R.id.status_radio_group);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent goBack = new Intent(NewTaskActivity.this, TaskListActivity.class);

        if (id == R.id.action_cancel) {
            startActivity(goBack);
        } else if (id == R.id.action_save_new) {
            addTask();
            startActivity(goBack);
        }
        return super.onOptionsItemSelected(item);
    }

    public void addTask() {
        String name = mTaskTitle.getText().toString().trim();
        String description = mTaskDescription.getText().toString().trim();
        String dueDate = mTaskDueDate.getText().toString();
        String status = "incomplete";

        if (mTaskStatus.getCheckedRadioButtonId() == R.id.radio_in_progress) {
            status = "in progress";
        }

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(dueDate) && !TextUtils.isEmpty(status)) {
            String id = mDatabaseTasks.push().getKey();
            task.setId(id);
            task.setTitle(name);
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setStatus(status);
            mDatabaseTasks.child(id).setValue(task);

            //assigning task to user
            for(String UserID: assignedUsers){
                ArrayList<String> toAdd = new ArrayList<>();
                toAdd.add(id);
                mDatabaseUsers.child(UserID).child("assignedTasks").setValue(toAdd);
            }
            Toast.makeText(this, "The desired task has been added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You should enter the right info.", Toast.LENGTH_LONG).show();
        }
    }
}
