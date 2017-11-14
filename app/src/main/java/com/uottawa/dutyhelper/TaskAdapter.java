package com.uottawa.dutyhelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hamza on 11/12/2017.
 */

public class TaskAdapter extends ArrayAdapter<Task> {

    private final Context mContext;
    private final List<Task> mTaskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        super(context, R.layout.task_list_item_view, taskList);
        mContext = context;
        mTaskList = taskList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_item_view, parent, false);
        Task task = mTaskList.get(position);

        ImageView taskIcon = (ImageView) rowView.findViewById(R.id.task_icon);
        TextView taskTitle = (TextView) rowView.findViewById(R.id.task_title);
        TextView taskDescription = (TextView) rowView.findViewById(R.id.task_description);

        taskIcon.setImageResource(R.drawable.splash_icon);
        taskTitle.setText(task.getTitle());
        taskDescription.setText(task.getDescription());

        return rowView;
    }
}
