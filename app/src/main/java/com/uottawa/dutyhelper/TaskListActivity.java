package com.uottawa.dutyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskIntent = new Intent(view.getContext(), NewTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Take out the trash", "Garbage day is on Wednesday"));
        tasks.add(new Task("Take out the trash", "Garbage day is on Wednesday"));
        tasks.add(new Task("Take out the trash", "Garbage day is on Wednesday"));
        tasks.add(new Task("Take out the trash", "Garbage day is on Wednesday"));
        tasks.add(new Task("Take out the trash", "Garbage day is on Wednesday"));

        TaskAdapter adapter = new TaskAdapter(getApplicationContext(), tasks);
        ListView listView = (ListView) findViewById(R.id.task_list_view);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
