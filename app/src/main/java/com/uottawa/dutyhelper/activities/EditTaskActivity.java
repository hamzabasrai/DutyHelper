package com.uottawa.dutyhelper.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.dutyhelper.R;
import com.uottawa.dutyhelper.model.Task;
import com.uottawa.dutyhelper.model.User;

public class EditTaskActivity extends AppCompatActivity {

    private static final String EXTRA_TASK_ID = "com.uottawa.dutyhelper.task_id";
    private static final String EXTRA_TASK_NAME = "com.uottawa.dutyhelper.task_name";
    private static final String EXTRA_TASK_DESC = "com.uottawa.dutyhelper.task_desc";
    private static final String EXTRA_TASK_DATE = "com.uottawa.dutyhelper.task_date";
    private static final String EXTRA_TASK_STATUS = "com.uottawa.dutyhelper.taskStatus";
    private static final String EXTRA_TASK_CREATOR = "com.uottawa.dutyhelper.creator";


    private EditText mTaskName;
    private EditText mTaskDescription;
    private EditText mTaskDate;
    private TextInputLayout mTaskNameLayout;
    private TextInputLayout mTaskDescLayout;

    private DatabaseReference databaseTasks;
    private DatabaseReference databaseUsers;

    private String extraTaskId;
    private String extraTaskName;
    private String extraTaskDescription;
    private String extraTaskDate;
    private String extraCreator;

    private RadioGroup radioGroup;
    private RadioButton incomplete;
    private RadioButton inprogress;
    private RadioButton complete;
    private String mstatus;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private int points;



    public static Intent newIntent(Context packageContext, String taskId,
                                   String taskName, String taskDescription,
                                   String taskDate, String status, String creator) {
        Intent intent = new Intent(packageContext, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        intent.putExtra(EXTRA_TASK_NAME, taskName);
        intent.putExtra(EXTRA_TASK_DESC, taskDescription);
        intent.putExtra(EXTRA_TASK_DATE, taskDate);
        intent.putExtra(EXTRA_TASK_STATUS, status);
        intent.putExtra(EXTRA_TASK_CREATOR,creator);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseTasks = FirebaseDatabase.getInstance().getReference("tasks");

        mAuth = FirebaseAuth.getInstance();

        extraTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        extraTaskName = getIntent().getStringExtra(EXTRA_TASK_NAME);
        extraTaskDescription = getIntent().getStringExtra(EXTRA_TASK_DESC);
        extraTaskDate = getIntent().getStringExtra(EXTRA_TASK_DATE);
        mstatus = getIntent().getStringExtra(EXTRA_TASK_STATUS);
        extraCreator= getIntent().getStringExtra(EXTRA_TASK_CREATOR);


        mTaskName = (EditText) findViewById(R.id.edit_task_name);
        mTaskDescription = (EditText) findViewById(R.id.edit_task_description);
        mTaskDate = (EditText) findViewById(R.id.edit_due_date);
        mTaskNameLayout = (TextInputLayout) findViewById(R.id.edit_task_name_layout);
        mTaskDescLayout = (TextInputLayout) findViewById(R.id.edit_task_description_layout);

        mTaskName.setText(extraTaskName);
        mTaskDescription.setText(extraTaskDescription);
        mTaskDate.setText(extraTaskDate);

        radioGroup = (RadioGroup) findViewById(R.id.status_radio_group);
        incomplete = (RadioButton) findViewById(R.id.radio_incomplete);
        inprogress = (RadioButton)findViewById(R.id.radio_in_progress);
        complete = (RadioButton)findViewById(R.id.radio_complete);

        currentUser= mAuth.getCurrentUser();

        if(!currentUser.getUid().equals(extraCreator)){
            mTaskName.setFocusable(false);
            mTaskDescription.setFocusable(false);
            mTaskDate.setFocusable(false);
        }

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child: children){
                    User user = child.getValue(User.class);
                    //Toast.makeText(getApplicationContext(),user.getId() , Toast.LENGTH_LONG).show();
                    if(user.getId().equals(currentUser.getUid()))
                    points = child.getValue(User.class).getPoints();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });










        fillButtons();
        radioListener();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTaskNameLayout.setError(null);
                mTaskDescLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mTaskName.addTextChangedListener(textWatcher);
        mTaskDescription.addTextChangedListener(textWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        final Intent goBack = new Intent(EditTaskActivity.this, TaskListActivity.class);

        if (id == R.id.action_delete) {

            mTaskName.setText(extraTaskName);
            mTaskDescription.setText(extraTaskDescription);

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(EditTaskActivity.this);
            builder.setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete task?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTask(extraTaskId);
                            Toast.makeText(EditTaskActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                            startActivity(goBack);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        } else if (id == R.id.action_save_edit) {

            if (isValidForm()) {
                String taskName = mTaskName.getText().toString();
                String taskDescription = mTaskDescription.getText().toString();
                updateTask(extraTaskId, taskName, taskDescription, mstatus);

                Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
                startActivity(goBack);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTask(String id, String name, String description, String status) {
        DatabaseReference dR = databaseTasks.child(id);
        Task updatedTask = new Task(id, name, description, "", status,currentUser.getUid());
        dR.setValue(updatedTask);
    }

    private void deleteTask(String id) {
        DatabaseReference dR = databaseTasks.child(id);
        dR.removeValue();
    }

    private boolean isValidForm() {

        boolean isValid = true;

        String name = mTaskName.getText().toString();
        String description = mTaskDescription.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mTaskNameLayout.setError("Required Field");
            isValid = false;
        }
        if (TextUtils.isEmpty(description)) {
            mTaskDescLayout.setError("Required Field");
            isValid = false;
        }
        return isValid;
    }

    public void radioListener() {
        incomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mstatus="incomplete";
                databaseUsers.child(currentUser.getUid()).child("points").setValue(points-100);
            }
        });
        inprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mstatus="inprogress";
                databaseUsers.child(currentUser.getUid()).child("points").setValue(points-100);
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mstatus="complete";
                databaseUsers.child(currentUser.getUid()).child("points").setValue(points+100);
            }
        });
    }

    public void fillButtons() {
        if(mstatus.equals("incomplete")){
            incomplete.toggle();
            //databaseUsers.child(currentUser.getUid()).child("points").setValue(points-100);
        }

        if(mstatus.equals("inprogress")){
            inprogress.toggle();
        }

        if (mstatus.equals("complete")){
            complete.toggle();

        }
    }
}
