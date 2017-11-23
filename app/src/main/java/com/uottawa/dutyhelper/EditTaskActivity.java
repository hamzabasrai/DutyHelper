package com.uottawa.dutyhelper;

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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditTaskActivity extends AppCompatActivity {

    private static final String EXTRA_TASK_ID = "com.uottawa.dutyhelper.task_id";
    private static final String EXTRA_TASK_NAME = "com.uottawa.dutyhelper.task_name";
    private static final String EXTRA_TASK_DESC = "com.uottawa.dutyhelper.task_desc";


    private EditText mTaskName;
    private EditText mTaskDescription;
    private TextInputLayout mTaskNameLayout;
    private TextInputLayout mTaskDescLayout;

    private DatabaseReference databaseTasks;

    private String extraTaskId;
    private String extraTaskName;
    private String extraTaskDescription;


    public static Intent newIntent(Context packageContext, String taskId, String taskName, String taskDescription) {
        Intent intent = new Intent(packageContext, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        intent.putExtra(EXTRA_TASK_NAME, taskName);
        intent.putExtra(EXTRA_TASK_DESC, taskDescription);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        databaseTasks = FirebaseDatabase.getInstance().getReference("Tasks");

        extraTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        extraTaskName = getIntent().getStringExtra(EXTRA_TASK_NAME);
        extraTaskDescription = getIntent().getStringExtra(EXTRA_TASK_DESC);

        mTaskName = (EditText) findViewById(R.id.edit_task_name);
        mTaskDescription = (EditText) findViewById(R.id.edit_task_description);
        mTaskNameLayout = (TextInputLayout) findViewById(R.id.edit_task_name_layout);
        mTaskDescLayout = (TextInputLayout) findViewById(R.id.edit_task_description_layout);

        mTaskName.setText(extraTaskName);
        mTaskDescription.setText(extraTaskDescription);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        final Intent goBack = new Intent(EditTaskActivity.this, TaskListActivity.class);

        if (id == R.id.action_delete) {

            mTaskName.setText(extraTaskName);
            mTaskDescription.setText(extraTaskDescription);

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(EditTaskActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
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

            if (isValidForm()) {
                String taskName = mTaskName.getText().toString();
                String taskDescription = mTaskDescription.getText().toString();
                updateTask(extraTaskId, taskName, taskDescription);
                Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
                startActivity(goBack);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTask(String id, String name, String description) {
        DatabaseReference dR = databaseTasks.child(id);
        Task updatedTask = new Task(id, name, description);
        dR.setValue(updatedTask);
    }

    private void deleteTask(String id) {
        DatabaseReference dR = databaseTasks.child(id);
        dR.removeValue();
    }

    private boolean isValidForm() {

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
