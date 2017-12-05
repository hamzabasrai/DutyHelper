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
    private List<String> mAssignedResources = new ArrayList<>();
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
    private Button mBtnAssignResources;
    private ListView mResourcesListView;
    private ListView mUserListView;
    private SparseBooleanArray mCheckedResources;
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
        mBtnAssignResources = (Button) findViewById(R.id.edit_btn_add_resources);

        mTaskDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogDatePicker = LayoutInflater.from(EditTaskActivity.this).inflate(R.layout.dialog_date_picker, null);
                final DatePicker datePicker = (DatePicker) dialogDatePicker.findViewById(R.id.date_picker);
                datePicker.init(year, month, day, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
                builder.setTitle(R.string.dialog_title_due_date)
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

                View dialogUsers = LayoutInflater.from(EditTaskActivity.this).inflate(R.layout.dialog_assign_user, null);
                mUserListView = (ListView) dialogUsers.findViewById(R.id.users_list_view);
                mUserListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditTaskActivity.this, android.R.layout.simple_list_item_multiple_choice, userNames);
                mUserListView.setAdapter(adapter);

                if (mCheckedUsers != null) {
                    for (int i = 0; i < mUserListView.getCount(); i++) {
                        mUserListView.setItemChecked(i, mCheckedUsers.get(i));
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
                builder.setTitle(R.string.dialog_title_assign_users)
                        .setView(dialogUsers)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAssignedUserIds.clear();
                                mCheckedUsers = mUserListView.getCheckedItemPositions();
                                for (int i = 0; i < mUserListView.getCount(); i++) {
                                    if (mCheckedUsers.get(i)) {
                                        mAssignedUserIds.add(mUsers.get(i).getId());
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        mBtnAssignResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] resources = getResources().getStringArray(R.array.task_resources);

                View dialogResources = LayoutInflater.from(EditTaskActivity.this).inflate(R.layout.dialog_assign_resources, null);
                mResourcesListView = (ListView) dialogResources.findViewById(R.id.resourcesListView);
                mResourcesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditTaskActivity.this, android.R.layout.simple_list_item_multiple_choice, resources);
                mResourcesListView.setAdapter(adapter);

                if (mCheckedResources != null) {
                    for (int i = 0; i < mResourcesListView.getCount(); i++) {
                        mResourcesListView.setItemChecked(i, mCheckedResources.get(i));
                    }

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
                builder.setTitle(R.string.dialog_title_add_resources)
                        .setView(dialogResources)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                mAssignedResources.clear();
                                mCheckedResources = mResourcesListView.getCheckedItemPositions();
                                for (int i = 0; i < mResourcesListView.getCount(); i++) {
                                    if (mCheckedResources.get(i)) {
                                        mAssignedResources.add(resources[i]);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

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
                        mAssignedUserIds = task.getAssignedUsers();
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
                populateUserDialog();
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
            builder.setTitle(R.string.dialog_title_delete_task)
                    .setMessage(R.string.dialog_message_delete_task)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTask(mTask);
                            Toast.makeText(EditTaskActivity.this, R.string.toast_task_deleted, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, R.string.toast_task_updated, Toast.LENGTH_SHORT).show();
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

        if (mTask.getResources() != null) {
            String[] allResources = getResources().getStringArray(R.array.task_resources);
            mCheckedResources = new SparseBooleanArray(allResources.length);
            List<String> resources = mTask.getResources();
            for (int i = 0; i < allResources.length; i++) {
                String current = allResources[i];
                if (resources.contains(current)) {
                    mCheckedResources.put(i, true);
                } else {
                    mCheckedResources.put(i, false);
                }
            }
        }
    }

    private void populateUserDialog() {
        mCheckedUsers = new SparseBooleanArray(mUsers.size());
        List<String> assignedUsers = mTask.getAssignedUsers();
        for (int i = 0; i < mUsers.size(); i++) {
            String current = mUsers.get(i).getId();
            if (assignedUsers.contains(current)) {
                mCheckedUsers.put(i, true);
            } else {
                mCheckedUsers.put(i, false);
            }
        }
    }


    private void updateTask() {
        String userId = mUser.getId();
        int userPoints = mUser.getPoints();

        String taskId = mTask.getId();
        List<String> existingAssignees = mTask.getAssignedUsers();

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
        task.setResources(mAssignedResources);
        if (mAssignedUserIds.isEmpty()) {
            task.setAssignedUsers(mTask.getAssignedUsers());
        } else {
            task.setAssignedUsers(mAssignedUserIds);
        }
        task.setCreatorId(creator);

        mDatabaseTasks.child(taskId).setValue(task);
        updateAssignedUsers(existingAssignees, taskId);
    }

    private void updateAssignedUsers(List<String> existingAssignees, String taskId) {

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
                    if (user.getId().equals(deleteId) && user.getAssignedTasks() != null) {
                        List<String> assignedTasks = user.getAssignedTasks();
                        assignedTasks.remove(taskId);
                        user.setAssignedTasks(assignedTasks);
                        mDatabaseUsers.child(user.getId()).setValue(user);
                    }
                }
            }
        }
    }

    private void deleteTask(Task task) {
        for (String deleteId : task.getAssignedUsers()) {
            for (User user : mUsers) {
                if (user.getId().equals(deleteId) && user.getAssignedTasks() != null) {
                    List<String> assignedTasks = user.getAssignedTasks();
                    assignedTasks.remove(task.getId());
                    user.setAssignedTasks(assignedTasks);
                    mDatabaseUsers.child(user.getId()).setValue(user);
                }
            }
        }
        mDatabaseTasks.child(task.getId()).removeValue();
    }


    private boolean isValidTask() {

        boolean isValid = true;

        String name = mTaskName.getText().toString().trim();
        String description = mTaskDescription.getText().toString().trim();
        String dueDate = mTaskDueDate.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mTaskNameLayout.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (TextUtils.isEmpty(description)) {
            mTaskDescLayout.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (TextUtils.isEmpty(dueDate)) {
            mTaskDateLayout.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (mAssignedUserIds.isEmpty()) {
            Toast.makeText(EditTaskActivity.this, R.string.error_assign_user, Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }
}
