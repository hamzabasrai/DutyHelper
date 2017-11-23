package com.uottawa.dutyhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseTasks;

    private FloatingActionButton mFAB;
    private LinearLayout addTaskLayout;
    private LinearLayout newGroupLayout;

    private ListView mTasksListView;
    private boolean subMenuExpanded = false;

    private List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        mDatabaseTasks = FirebaseDatabase.getInstance().getReference("Tasks");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTasksListView = (ListView) findViewById(R.id.task_list_view);
        addTaskLayout = (LinearLayout) findViewById(R.id.layout_add_task);
        newGroupLayout = (LinearLayout) findViewById(R.id.layout_new_group);

        tasks = new ArrayList<>();

        mFAB = (FloatingActionButton) findViewById(R.id.main_fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subMenuExpanded) {
                    closeSubMenu();
                } else {
                    openSubMenu();
                }
            }
        });

        FloatingActionButton addTaskFab = (FloatingActionButton) findViewById(R.id.fab_add_task);
        addTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, NewTaskActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton newGroupFab = (FloatingActionButton) findViewById(R.id.fab_new_group);
        newGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskListActivity.this, "Will lead to new page", Toast.LENGTH_SHORT).show();
            }
        });


        mTasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(TaskListActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(TaskListActivity.this);
                }
                builder.setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete task?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });

        closeSubMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseTasks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tasks.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Task task = postSnapshot.getValue(Task.class);
                    tasks.add(task);
                }
                TaskAdapter adapter = new TaskAdapter(TaskListActivity.this, tasks);
                mTasksListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_group) {
            Intent addMemberToGroup = new Intent(TaskListActivity.this, addUserToGroup.class);
            startActivity(addMemberToGroup);
        }
        if (id == R.id.action_settings) {
            Intent sendToSettings = new Intent(TaskListActivity.this, ProfileSettingActivity.class);
            startActivity(sendToSettings);

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    private void closeSubMenu() {
        addTaskLayout.setVisibility(View.INVISIBLE);
        newGroupLayout.setVisibility(View.INVISIBLE);
        mFAB.setImageResource(R.drawable.ic_add);
        subMenuExpanded = false;
    }

    private void openSubMenu() {
        addTaskLayout.setVisibility(View.VISIBLE);
        newGroupLayout.setVisibility(View.VISIBLE);
        mFAB.setImageResource(R.drawable.ic_clear);
        subMenuExpanded = true;
    }


}
