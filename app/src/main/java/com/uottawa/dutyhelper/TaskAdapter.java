package com.uottawa.dutyhelper;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by Hamza on 11/12/2017.
 */

public class TaskAdapter extends ArrayAdapter<Task> {

    private final Context mContext;
    private final List<Task> mTaskList;

    private DatabaseReference mDatabaseTasks;
    private DatabaseReference mDatabaseStatus;

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
                    dR.child("status").setValue("complete");
                } else {
                    dR.child("status").setValue("incomplete");
                }
            }
        });

        return rowView;
    }
}
