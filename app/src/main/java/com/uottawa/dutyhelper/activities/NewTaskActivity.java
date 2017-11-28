package com.uottawa.dutyhelper.activities;

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

import com.uottawa.dutyhelper.util.DBHandler;
import com.uottawa.dutyhelper.R;
import com.uottawa.dutyhelper.model.Task;
import com.uottawa.dutyhelper.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewTaskActivity extends AppCompatActivity {

    private EditText mTaskTitle;
    private EditText mTaskDescription;
    private EditText mTaskDueDate;
    private RadioGroup mTaskStatus;
    private Button mBtnAssignUser;

    private TextInputLayout mTitleLayout;
    private TextInputLayout mDescLayout;
    private TextInputLayout mDueDateLayout;

    private DatePicker mDatePicker;
    private ListView mUserListView;
    private ArrayAdapter<String> mUserListAdapter;

    private List<String> mAssignedUserIds;
    private SparseBooleanArray mCheckedUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        mTaskTitle = (EditText) findViewById(R.id.task_name);
        mTaskDescription = (EditText) findViewById(R.id.task_description);
        mTaskDueDate = (EditText) findViewById(R.id.task_due_date);
        mTaskStatus = (RadioGroup) findViewById(R.id.status_radio_group);
        mBtnAssignUser = (Button) findViewById(R.id.btn_assign_user);

        mTitleLayout = (TextInputLayout) findViewById(R.id.task_name_layout);
        mDescLayout = (TextInputLayout) findViewById(R.id.task_description_layout);
        mDueDateLayout = (TextInputLayout) findViewById(R.id.task_due_date_layout);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitleLayout.setError(null);
                mDescLayout.setError(null);
                mDueDateLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mTaskTitle.addTextChangedListener(textWatcher);
        mTaskDueDate.addTextChangedListener(textWatcher);
        mTaskDescription.addTextChangedListener(textWatcher);

        mTaskDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogDatePicker = LayoutInflater.from(NewTaskActivity.this).inflate(R.layout.dialog_date_picker, null);
                mDatePicker = (DatePicker) dialogDatePicker.findViewById(R.id.date_picker);
                mDatePicker.init(year, month, day, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(NewTaskActivity.this);
                builder.setTitle("Set Due Date")
                        .setView(dialogDatePicker)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth() + 1;
                                int day = mDatePicker.getDayOfMonth();
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

                final List<User> users = DBHandler.get().getUsers();
                List<String> userNames = new ArrayList<>();
                for (User user : users) {
                    String name = user.getFirstName() + " " + user.getLastName();
                    userNames.add(name);
                }

                mAssignedUserIds = new ArrayList<>();

                View dialogAssignView = LayoutInflater.from(NewTaskActivity.this).inflate(R.layout.dialog_assign_user, null);
                mUserListView = (ListView) dialogAssignView.findViewById(R.id.users_list_view);
                mUserListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                mUserListAdapter = new ArrayAdapter<>(NewTaskActivity.this, android.R.layout.simple_list_item_multiple_choice, userNames);
                mUserListView.setAdapter(mUserListAdapter);

                if (mCheckedUsers != null) {
                    for (int i = 0; i < mCheckedUsers.size() + 1; i++) {
                        mUserListView.setItemChecked(i, mCheckedUsers.get(i));
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(NewTaskActivity.this);
                builder.setTitle("Assign Users")
                        .setView(dialogAssignView)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCheckedUsers = mUserListView.getCheckedItemPositions();
                                for (int i = 0; i < mCheckedUsers.size(); i++) {
                                    if (mCheckedUsers.get(i)) {
                                        mAssignedUserIds.add(users.get(i).getId());
                                    }
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAssignedUserIds.clear();
                                mCheckedUsers.clear();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            if (isValidTask()) {
                addTask();
                startActivity(goBack);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addTask() {

        String name = mTaskTitle.getText().toString().trim();
        String description = mTaskDescription.getText().toString().trim();
        String dueDate = mTaskDueDate.getText().toString();
        String status = "incomplete";
        int statusId = mTaskStatus.getCheckedRadioButtonId();

        if (statusId == R.id.radio_in_progress) {
            status = "in progress";
        }

        Task task = new Task();
        task.setTitle(name);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setStatus(status);
        task.setAssignedUsers(mAssignedUserIds);

        DBHandler.get().addTask(task);
        Toast.makeText(this, "Task Added", Toast.LENGTH_LONG).show();
    }

    private boolean isValidTask() {

        boolean isValid = true;

        String name = mTaskTitle.getText().toString().trim();
        String description = mTaskDescription.getText().toString().trim();
        String dueDate = mTaskDueDate.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mTitleLayout.setError("Required Field");
            isValid = false;
        }
        if (TextUtils.isEmpty(description)) {
            mDescLayout.setError("Required Field");
            isValid = false;
        }
        if (TextUtils.isEmpty(dueDate)) {
            mDueDateLayout.setError("Required Field");
            isValid = false;
        }

        return isValid;
    }
}
