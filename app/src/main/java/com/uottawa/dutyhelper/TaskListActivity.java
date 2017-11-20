package com.uottawa.dutyhelper;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

}
