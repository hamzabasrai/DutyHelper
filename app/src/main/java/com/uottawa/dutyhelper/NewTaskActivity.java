package com.uottawa.dutyhelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewTaskActivity extends AppCompatActivity {

    private DatabaseReference databaseTasks;
    private EditText mTaskTitle;
    private EditText mTaskDescription;
    private Button mAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mTaskTitle = (EditText) findViewById(R.id.task_name);
        mTaskDescription = (EditText) findViewById(R.id.task_description);
        mAddTask = (Button) findViewById(R.id.btn_Add_Task);
        databaseTasks = FirebaseDatabase.getInstance().getReference("Tasks");

        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void addTask(){
        String name = mTaskTitle.getText().toString().trim();
        String description = mTaskDescription.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description)){
           Task task = new Task(name,description);
           String id = databaseTasks.push().getKey();
           databaseTasks.child(id).setValue(task);
            Toast.makeText(this, "The desired task has been added", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this, "You should enter the right info.", Toast.LENGTH_LONG).show();
        }

    }


}
