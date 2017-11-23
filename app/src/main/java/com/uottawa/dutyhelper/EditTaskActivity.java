package com.uottawa.dutyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class EditTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent goBack = new Intent(EditTaskActivity.this, TaskListActivity.class);

        if(id == R.id.action_delete) {
            Toast.makeText(this, "Task Will be deleted", Toast.LENGTH_SHORT).show();
            startActivity(goBack);
        } else if (id == R.id.action_save_new) {
            Toast.makeText(this, "Task will be updated", Toast.LENGTH_SHORT).show();
            startActivity(goBack);
        }
        return super.onOptionsItemSelected(item);
    }
}
