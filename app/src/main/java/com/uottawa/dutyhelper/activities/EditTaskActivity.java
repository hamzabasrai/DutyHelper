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
import java.util.Calendar;
import java.util.List;

public class EditTaskActivity extends AppCompatActivity {

    private static final String EXTRA_TASK_ID = "com.uottawa.dutyhelper.task_id";

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseTasks;
    private FirebaseAuth mAuth;

    private List<Task> mTasks = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();
    private List<String> mAssignedUserIds = new ArrayList<>();
    private Task mTask = new Task();
    private User mUser;

    private EditText mTaskName;
    private EditText mTaskDescription;
    private EditText mTaskDueDate;
    private TextInputLayout mTaskNameLayout;
    private TextInputLayout mTaskDescLayout;
    private TextInputLayout mTaskDateLayout;
    private RadioGroup mRadioGroup;
    private Button mBtnAssignUser;
    private ListView mUserListView;
    private SparseBooleanArray mCheckedUsers;

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

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        extraTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        mDatabaseTasks = FirebaseDatabase.getInstance().getReference("tasks");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        mTaskName = (EditText) findViewById(R.id.edit_task_name);
        mTaskDescription = (EditText) findViewById(R.id.edit_task_description);
        mTaskDueDate = (EditText) findViewById(R.id.edit_due_date);
        mTaskNameLayout = (TextInputLayout) findViewById(R.id.edit_task_name_layout);
        mTaskDescLayout = (TextInputLayout) findViewById(R.id.edit_task_description_layout);
        mTaskDateLayout = (TextInputLayout) findViewById(R.id.edit_due_date_layout);
        mRadioGroup = (RadioGroup) findViewById(R.id.edit_status_radio_group);
        mBtnAssignUser = (Button) findViewById(R.id.edit_btn_assign_user);

        mTaskDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogDatePicker = LayoutInflater.from(EditTaskActivity.this).inflate(R.layout.dialog_date_picker, null);
                final DatePicker datePicker = (DatePicker) dialogDatePicker.findViewById(R.id.date_picker);
                datePicker.init(year, month, day, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
                builder.setTitle("Set Due Date")
                        .setView(dialogDatePicker)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int year = datePicker.getYear();
                                int month = datePicker.getMonth() + 1;
                                int day = datePicker.getDayOfMonth();
                                String date = String.format("%s/%s/%s", day, month, year);
                                mTaskDueDate.setText(date);

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

        mBtnAssignUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> userNames = new ArrayList<>();
                for (User user : mUsers) {
                    String name = user.getFirstName() + " " + user.getLastName();
                    userNames.add(name);
                }

                View dialogAssignView = LayoutInflater.from(EditTaskActivity.this).inflate(R.layout.dialog_assign_user, null);
                mUserListView = (ListView) dialogAssignView.findViewById(R.id.users_list_view);
                mUserListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                ArrayAdapter adapter = new ArrayAdapter<>(EditTaskActivity.this, android.R.layout.simple_list_item_multiple_choice, userNames);
                mUserListView.setAdapter(adapter);

                if (mCheckedUsers != null) {
                    for (int i = 0; i < mCheckedUsers.size() + 1; i++) {
                        mUserListView.setItemChecked(i, mCheckedUsers.get(i));
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
                builder.setTitle("Assign Users")
                        .setView(dialogAssignView)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCheckedUsers = mUserListView.getCheckedItemPositions();
                                for (int i = 0; i < mCheckedUsers.size(); i++) {
                                    if (mCheckedUsers.get(i)) {
                                        mAssignedUserIds.add(mUsers.get(i).getId());
                                    }
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAssignedUserIds.clear();
                                if (mCheckedUsers != null) {
                                    mCheckedUsers.clear();
                                }
                            }
                        })
                        .show();
            }
        });

        if (!mAuth.getCurrentUser().getUid().equals(mTask.getCreatorId())) {
            mTaskName.setFocusable(false);
            mTaskDescription.setFocusable(false);
            mTaskDueDate.setFocusable(false);
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTaskNameLayout.setError(null);
                mTaskDescLayout.setError(null);
                mTaskDateLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mTaskName.addTextChangedListener(textWatcher);
        mTaskDescription.addTextChangedListener(textWatcher);
        mTaskDueDate.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseTasks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTasks.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    mTasks.add(task);
                    if (task.getId().equals(extraTaskId)) {
                        mTask = task;
                        if (mTask.getAssignedUsers() != null) {
                            mAssignedUserIds = mTask.getAssignedUsers();
                        }
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
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                    if (user.getId().equals(mAuth.getCurrentUser().getUid())) {
                        mUser = user;
                    }
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
        mTaskDueDate.setText(mTask.getDueDate());

        switch (mTask.getStatus()) {
            case "incomplete":
                mRadioGroup.check(R.id.edit_radio_incomplete);
                break;
            case "in progress":
                mRadioGroup.check(R.id.edit_radio_in_progress);
                break;
            case "complete":
                mRadioGroup.check(R.id.edit_radio_complete);
                break;
            default:
                break;
        }
    }


    private void updateTask() {
        String userId = mUser.getId();
        int userPoints = mUser.getPoints();

        String taskId = mTask.getId();
        List<String> existingAssignees = new ArrayList<>();
        if (mTask.getAssignedUsers() != null) {
            existingAssignees = mTask.getAssignedUsers();
        }

        String name = mTaskName.getText().toString();
        String description = mTaskDescription.getText().toString();
        String dueDate = mTaskDueDate.getText().toString();
        String status = "";
        String oldStatus = mTask.getStatus();

        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.edit_radio_incomplete:
                status = "incomplete";
                if (oldStatus.equals("complete")) {
                    mDatabaseUsers.child(userId).child("points").setValue(userPoints - 100);
                }
                break;
            case R.id.edit_radio_in_progress:
                status = "in progress";
                if (oldStatus.equals("complete")) {
                    mDatabaseUsers.child(userId).child("points").setValue(userPoints - 100);
                }
                break;
            case R.id.edit_radio_complete:
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
        task.setId(taskId);
        task.setTitle(name);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setStatus(status);
        task.setAssignedUsers(mAssignedUserIds);
        task.setCreatorId(creator);

        mDatabaseTasks.child(taskId).setValue(task);

        for (String assignId : mAssignedUserIds) {
            if (!existingAssignees.contains(assignId)) {
                for (User user : mUsers) {
                    if (user.getId().equals(assignId)) {
                        List<String> assignedTasks = new ArrayList<>();
                        if (user.getAssignedTasks() != null) {
                            assignedTasks = user.getAssignedTasks();
                        }
                        assignedTasks.add(taskId);
                        user.setAssignedTasks(assignedTasks);
                        mDatabaseUsers.child(user.getId()).setValue(user);
                    }
                }
            }
        }

        for (String deleteId : existingAssignees) {
            if (!mAssignedUserIds.contains(deleteId)) {
                for (User user : mUsers) {
                    if (user.getId().equals(deleteId)) {
                        List<String> assignedTasks = user.getAssignedTasks();
                        assignedTasks.remove(taskId);
                        user.setAssignedTasks(assignedTasks);
                        mDatabaseUsers.child(user.getId()).setValue(user);
                    }
                }
            }
        }


    }

    private void deleteTask(String taskId) {
        mDatabaseTasks.child(taskId).removeValue();
    }

    private boolean isValidTask() {

        boolean isValid = true;

        String name = mTaskName.getText().toString().trim();
        String description = mTaskDescription.getText().toString().trim();
        String dueDate = mTaskDueDate.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mTaskNameLayout.setError("Required Field");
            isValid = false;
        }
        if (TextUtils.isEmpty(description)) {
            mTaskDescLayout.setError("Required Field");
            isValid = false;
        }
        if (TextUtils.isEmpty(dueDate)) {
            mTaskDateLayout.setError("Required Field");
            isValid = false;
        }
        return isValid;
    }
}
