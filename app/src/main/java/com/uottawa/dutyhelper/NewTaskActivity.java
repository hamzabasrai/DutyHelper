package com.uottawa.dutyhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class NewTaskActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseTasks;
    private EditText mTaskTitle;
    private EditText mTaskDescription;
    private EditText mTaskDueDate;
    private RadioGroup mTaskStatus;
    private DatePicker mDatePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mDatabaseTasks = FirebaseDatabase.getInstance().getReference("Tasks");
        mTaskTitle = (EditText) findViewById(R.id.task_name);
        mTaskDescription = (EditText) findViewById(R.id.task_description);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


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
            Task task = new Task(id, name, description, dueDate);
            task.setStatus(status);
            mDatabaseTasks.child(id).setValue(task);
            Toast.makeText(this, "The desired task has been added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You should enter the right info.", Toast.LENGTH_LONG).show();
        }
    }
}
