package com.uottawa.dutyhelper;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class NewTaskActivity extends AppCompatActivity {

    private EditText mTask_Name = (EditText) findViewById(R.id.task_name);
    private TextInputLayout mTask_Name_Layout = (TextInputLayout) findViewById(R.id.task_name_layout);
    private EditText mTask_Description = (EditText) findViewById(R.id.task_description);
    private TextInputLayout mTask_Description_Layout = (TextInputLayout) findViewById(R.id.task_description_layout);
    private Button mAdd_Task = (Button) findViewById(R.id.btn_Add_Task);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
    }
}
