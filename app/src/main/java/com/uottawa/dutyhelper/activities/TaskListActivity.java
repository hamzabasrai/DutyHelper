package com.uottawa.dutyhelper.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.dutyhelper.R;
import com.uottawa.dutyhelper.model.User;
import com.uottawa.dutyhelper.util.TaskAdapter;
import com.uottawa.dutyhelper.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseTasks;
    private DatabaseReference mUsertasks;
    private FirebaseAuth mAuth;

    private FloatingActionButton mFAB;
    private LinearLayout addTaskLayout;
    private LinearLayout newGroupLayout;

    private ListView mTasksListView;
    private boolean subMenuExpanded = false;

    private List<Task> mTasks;
    private List<String> mUsertaskList;
    private List<Task> mFilter;
    private Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        mAuth = FirebaseAuth.getInstance();

        mDatabaseTasks = FirebaseDatabase.getInstance().getReference("tasks");
        mUsertasks = FirebaseDatabase.getInstance().
                getReference("users").
                child(mAuth.getCurrentUser().getUid());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTasksListView = (ListView) findViewById(R.id.task_list_view);
        addTaskLayout = (LinearLayout) findViewById(R.id.layout_add_task);
        newGroupLayout = (LinearLayout) findViewById(R.id.layout_new_group);

        mTasks = new ArrayList<>();
        mSwitch = (Switch) findViewById(R.id.switch3);
        mUsertaskList = new ArrayList<>();
        mFilter= new ArrayList<>();





        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                mDatabaseTasks.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTasks.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Task task = postSnapshot.getValue(Task.class);
                            mTasks.add(task);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if(isChecked) {
                    mTasksListView.setAdapter(null);
                    mUsertaskList.clear();
                    mFilter.clear();
                    mUsertasks.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.hasChild("assignedTasks")) {
                                            mUsertasks.child("assignedTasks").
                                                    addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                                                            List<String> messages = dataSnapshot.getValue(t);

                                                            mUsertaskList= new ArrayList<>(messages);
                                                            for (String id: mUsertaskList){
                                                                for(Task task: mTasks){
                                                                    if(id.equals(task.getId())){
                                                                        mFilter.add(task);
                                                                    }
                                                                }
                                                            }
                                                            TaskAdapter adapter = new TaskAdapter(TaskListActivity.this, mFilter);
                                                            mTasksListView.setAdapter(adapter);

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }

                                                    });

                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                }else{
                    mDatabaseTasks.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mTasks.clear();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Task task = postSnapshot.getValue(Task.class);
                                mTasks.add(task);
                            }
                            TaskAdapter adapter = new TaskAdapter(TaskListActivity.this, mTasks);
                            mTasksListView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

        }
    });



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
                Intent intent = new Intent(TaskListActivity.this, NewGroupActivity.class);
                startActivity(intent);
            }
        });

        mTasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Task selected = mTasks.get(position);
                String taskId = selected.getId();

                Intent intent = EditTaskActivity.newIntent(TaskListActivity.this, taskId);
                startActivity(intent);
            }
        });


        mTasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                final int position = pos;

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(TaskListActivity.this);
                builder.setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete task?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //deletes entry from database
                                String taskId = mTasks.get(position).getId();
                                DatabaseReference dR = mDatabaseTasks.child(taskId);
                                dR.removeValue();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
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
                mTasks.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Task task = postSnapshot.getValue(Task.class);
                    mTasks.add(task);
                }
                TaskAdapter adapter = new TaskAdapter(TaskListActivity.this, mTasks);
                mTasksListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onPostResume() {
        closeSubMenu();
        super.onPostResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_group) {

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
        mFAB.setImageResource(R.drawable.ic_cancel);
        subMenuExpanded = true;
    }
}
