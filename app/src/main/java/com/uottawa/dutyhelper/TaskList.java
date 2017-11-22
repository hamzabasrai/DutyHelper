package com.uottawa.dutyhelper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Zaid Saeed on 21/11/2017.
 */

public class TaskList extends ArrayAdapter<Task> {
    private Activity context;
    private List<Task> taskList;

    public TaskList(Activity context, List<Task> taskList ){
        super(context, R.layout.list_layout, taskList);
        this.context = context;
        this.taskList = taskList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout,null,true);

        TextView mTaskName = (TextView) listViewItem.findViewById(R.id.task_title);

        TextView mTaskDescription = (TextView) listViewItem.findViewById(R.id.task_description);

        Task task = taskList.get(position);

        mTaskName.setText(task.getTitle());
        mTaskDescription.setText(task.getDescription());

        return listViewItem;

    }


}
