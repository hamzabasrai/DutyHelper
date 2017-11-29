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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.dutyhelper.R;
import com.uottawa.dutyhelper.model.Task;
import com.uottawa.dutyhelper.model.User;

import java.util.ArrayList;
import java.util.List;

public class EditTaskActivity extends AppCompatActivity {

    private static final String EXTRA_TASK_ID = "com.uottawa.dutyhelper.task_id";

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseTasks;
    private FirebaseAuth mAuth;

    private List<Task> mTasks = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();
    private Task mTask = new Task();
    private User mUser = new User();

    private EditText mTaskName;
    private EditText mTaskDescription;
    private EditText mTaskDate;
    private TextInputLayout mTaskNameLayout;
    private TextInputLayout mTaskDescLayout;
    private RadioGroup mRadioGroup;



    private String mstatus;
    private String extraTaskId;



    public static Intent newIntent(Context packageContext, String taskId) {
        Intent intent = new Intent(packageContext, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        extraTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        mDatabaseTasks = FirebaseDatabase.getInstance().getReference("tasks");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        mTaskName = (EditText) findViewById(R.id.edit_task_name);
        mTaskDescription = (EditText) findViewById(R.id.edit_task_description);
        mTaskDate = (EditText) findViewById(R.id.edit_due_date);
        mTaskNameLayout = (TextInputLayout) findViewById(R.id.edit_task_name_layout);
        mTaskDescLayout = (TextInputLayout) findViewById(R.id.edit_task_description_layout);

        mRadioGroup = (RadioGroup) findViewById(R.id.status_radio_group);

        if (!mAuth.getCurrentUser().getUid().equals(mTask.getCreatorId())) {
            mTaskName.setFocusable(false);
            mTaskDescription.setFocusable(false);
            mTaskDate.setFocusable(false);
            mRadioGroup.setEnabled(false);
        }

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
    protected void onStart() {
        super.onStart();
        mDatabaseTasks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    mTasks.add(task);
                    if (task.getId().equals(extraTaskId)) {
                        mTask = task;
                    }
                }
                populateTask();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

            mTaskName.setText(mTask.getTitle());
            mTaskDescription.setText(mTask.getDescription());

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

            if (isValidTask()) {

                updateTask();

                Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
                startActivity(goBack);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void populateTask() {
        mTaskName.setText(mTask.getTitle());
        mTaskDescription.setText(mTask.getDescription());
        mTaskDate.setText(mTask.getDueDate());

        switch (mTask.getStatus()) {
            case "incomplete":
                mRadioGroup.check(R.id.radio_incomplete);
                break;
            case "in progress":
                mRadioGroup.check(R.id.radio_in_progress);
                break;
            case "complete":
                mRadioGroup.check(R.id.radio_complete);
                break;
            default:
                break;
        }
    }


    private void updateTask() {
        String userId  = mUser.getId();
        int userPoints = mUser.getPoints();

        String id = mTask.getId();
        String name = mTaskName.getText().toString();
        String description = mTaskDescription.getText().toString();
        String dueDate = mTaskDate.getText().toString();
        String status = "";
        String oldStatus = mTask.getStatus();

        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_incomplete:
                status = "incomplete";
                if (oldStatus.equals("complete")) {
                    mDatabaseUsers.child(userId).child("points").setValue(userPoints - 100);
                }
                break;
            case R.id.radio_in_progress:
                status = "in progress";
                if (oldStatus.equals("complete")) {
                    mDatabaseUsers.child(userId).child("points").setValue(userPoints - 100);
                }
                break;
            case R.id.radio_complete:
                status = "complete";
                if (oldStatus.equals("incomplete") || oldStatus.equals("in progress")) {
                    mDatabaseUsers.child(userId).child("points").setValue(userPoints + 100);
                }
                break;
            default:
                break;
        }

        String creator = mTask.getCreatorId();

        Task task = new Task();
        task.setId(id);
        task.setTitle(name);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setStatus(status);
        task.setAssignedUsers(new ArrayList<String>());
        task.setCreatorId(creator);

        mDatabaseTasks.child(id).setValue(task);
    }

    private void deleteTask(String taskId) {
        mDatabaseTasks.child(taskId).removeValue();
    }

    private boolean isValidTask() {

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
}
