package com.uottawa.dutyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewTaskActivity extends AppCompatActivity {
    Spinner userSpinner;
    private DatabaseReference mDatabaseTasks;
    private EditText mTaskTitle;
    private EditText mTaskDescription;
    private CalendarView mCalendar;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mTaskTitle = (EditText) findViewById(R.id.task_name);
        mTaskDescription = (EditText) findViewById(R.id.task_description);
        mDatabaseTasks = FirebaseDatabase.getInstance().getReference("Tasks");
        mCalendar = (CalendarView) findViewById(R.id.calendarView);
        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = String.valueOf(dayOfMonth)+"-"+ String.valueOf(month+1)+"-"+String.valueOf(year);
            }
        });
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

        if(id == R.id.action_cancel) {
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
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description)) {
            String id = mDatabaseTasks.push().getKey();
            Task task = new Task(id, name, description, date);
            mDatabaseTasks.child(id).setValue(task);
            Toast.makeText(this, "The desired task has been added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You should enter the right info.", Toast.LENGTH_LONG).show();
        }

    }


}
