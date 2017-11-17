package com.uottawa.dutyhelper;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewTaskActivity extends AppCompatActivity {

    private EditText mTask_Name;
    private TextInputLayout mTask_Name_Layout;
    private EditText mTask_Description;
    private TextInputLayout mTask_Description_Layout;
    private Button mAdd_Task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        /*mTask_Name= (EditText) findViewById(R.id.task_name);
        mTask_Name_Layout = (TextInputLayout) findViewById(R.id.task_name_layout);
        mTask_Description = (EditText) findViewById(R.id.task_description);
        mTask_Description_Layout = (TextInputLayout) findViewById(R.id.task_description_layout);
        mAdd_Task = (Button) findViewById(R.id.btn_Add_Task);*/

        /*mAdd_Task.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Task toAdd = new Task(mTask_Name.getText().toString(), mTask_Description.getText().toString());

            }
        });*/



    }
}
