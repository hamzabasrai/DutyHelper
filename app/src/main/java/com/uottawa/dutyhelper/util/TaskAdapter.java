package com.uottawa.dutyhelper.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.dutyhelper.R;
import com.uottawa.dutyhelper.model.Task;
import com.uottawa.dutyhelper.model.User;

import java.util.List;

/**
 * Created by Hamza on 11/12/2017.
 */

public class TaskAdapter extends ArrayAdapter<Task> {

    private final Context mContext;
    private final List<Task> mTaskList;

    private DatabaseReference mDatabaseTasks;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseUsers;
    //private DatabaseReference mDatabaseStatus;
    private int points;

    public TaskAdapter(Context context, List<Task> taskList) {
        super(context, R.layout.task_list_item_view, taskList);
        mContext = context;
        mTaskList = taskList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_item_view, parent, false);
        final Task task = mTaskList.get(position);
        mDatabaseTasks = FirebaseDatabase.getInstance().getReference("Tasks");
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();
        currentUser= mAuth.getCurrentUser();

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child: children){
                    User user = child.getValue(User.class);
                    //Toast.makeText(getApplicationContext(),user.getId() , Toast.LENGTH_LONG).show();
                    if(user.getId().equals(currentUser.getUid()))
                        points = child.getValue(User.class).getPoints();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageView taskIcon = (ImageView) rowView.findViewById(R.id.task_icon);
        TextView taskTitle = (TextView) rowView.findViewById(R.id.task_title);
        TextView taskDescription = (TextView) rowView.findViewById(R.id.task_description);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);

        taskIcon.setImageResource(R.drawable.splash_icon);
        taskTitle.setText(task.getTitle());
        taskDescription.setText(task.getDescription());

        checkBox.setChecked(task.getStatus().equals("complete"));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String taskId = task.getId();
                DatabaseReference dR = mDatabaseTasks.child(taskId);
                if (isChecked){
                    //getCurrentPointFromUser();
                    databaseUsers.child(currentUser.getUid()).child("points").setValue(points+100);

                } else {
                    dR.child("status").setValue("incomplete");
                    databaseUsers.child(currentUser.getUid()).child("points").setValue(points-100);
                }
            }
        });

        return rowView;
    }
}
