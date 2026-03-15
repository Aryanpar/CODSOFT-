package com.example.todolistapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<TaskModel> list;
    private final DBHelper db;
    private OnListChangedListener onListChangedListener;

    public interface OnListChangedListener {
        void onListChanged();
    }

    public TaskAdapter(Context context, ArrayList<TaskModel> list, DBHelper db,
                       OnListChangedListener listener) {
        this.context = context;
        this.list = list;
        this.db = db;
        this.onListChangedListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheck;
        TextView taskTitle, taskDescription, taskPriority, taskDate;
        ImageButton editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheck = itemView.findViewById(R.id.taskCheck);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskPriority = itemView.findViewById(R.id.taskPriority);
            taskDate = itemView.findViewById(R.id.taskDate);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskModel task = list.get(position);

        // Title
        holder.taskTitle.setText(task.title);

        // Description
        if (task.description != null && !task.description.isEmpty()) {
            holder.taskDescription.setVisibility(View.VISIBLE);
            holder.taskDescription.setText(task.description);
        } else {
            holder.taskDescription.setVisibility(View.GONE);
        }

        // Priority badge color
        holder.taskPriority.setText(task.priority);
        switch (task.priority != null ? task.priority : "") {
            case "High":
                holder.taskPriority.setTextColor(context.getResources().getColor(R.color.colorPriorityHigh, null));
                holder.taskPriority.setBackgroundResource(R.drawable.bg_priority_high);
                break;
            case "Low":
                holder.taskPriority.setTextColor(context.getResources().getColor(R.color.colorPriorityLow, null));
                holder.taskPriority.setBackgroundResource(R.drawable.bg_priority_low);
                break;
            default: // Medium
                holder.taskPriority.setTextColor(context.getResources().getColor(R.color.colorPriorityMedium, null));
                holder.taskPriority.setBackgroundResource(R.drawable.bg_priority_medium);
                break;
        }

        // Date
        if (task.dueDate != null && !task.dueDate.isEmpty()) {
            holder.taskDate.setVisibility(View.VISIBLE);
            holder.taskDate.setText("📅 " + task.dueDate);
        } else {
            holder.taskDate.setVisibility(View.GONE);
        }

        // Completed visual state — suppress listener before setting checked
        holder.taskCheck.setOnCheckedChangeListener(null);
        holder.taskCheck.setChecked(task.status == 1);
        applyCompletedStyle(holder, task.status == 1);

        holder.taskCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.status = isChecked ? 1 : 0;
            db.updateStatus(task.id, task.status);
            applyCompletedStyle(holder, isChecked);
            if (onListChangedListener != null) onListChangedListener.onListChanged();
        });

        // Edit
        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTaskActivity.class);
            intent.putExtra("task_id", task.id);
            intent.putExtra("task_title", task.title);
            intent.putExtra("task_desc", task.description);
            intent.putExtra("task_priority", task.priority);
            intent.putExtra("task_date", task.dueDate);
            context.startActivity(intent);
        });

        // Delete
        holder.deleteBtn.setOnClickListener(v -> {
            db.deleteTask(task.id);
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
            if (onListChangedListener != null) onListChangedListener.onListChanged();
        });
    }

    private void applyCompletedStyle(ViewHolder holder, boolean completed) {
        if (completed) {
            holder.taskTitle.setPaintFlags(
                holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.taskTitle.setAlpha(0.5f);
            holder.taskDescription.setAlpha(0.5f);
        } else {
            holder.taskTitle.setPaintFlags(
                holder.taskTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.taskTitle.setAlpha(1f);
            holder.taskDescription.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}