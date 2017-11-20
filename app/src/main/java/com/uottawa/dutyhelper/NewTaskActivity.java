package com.uottawa.dutyhelper;

import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewTaskActivity extends AppCompatActivity {

    private DatabaseReference databaseTasks;
    private TextInputEditText mTask_Name;
    private TextInputEditText mTask_description;
    private Button mAdd_Task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mTask_Name = (TextInputEditText) findViewById(R.id.task_name);
        mTask_description = (TextInputEditText) findViewById(R.id.task_description);
        mAdd_Task = (Button) findViewById(R.id.btn_Add_Task);
        databaseTasks = FirebaseDatabase.getInstance().getReference("Tasks");

        mAdd_Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

    }

    public void addTask(){
        String name = mTask_Name.getText().toString().trim();
        String description = mTask_description.getText().toString().trim();

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
