package com.example.todolistapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {

    EditText title, desc, date;
    Spinner priority;
    Button save;
    ImageButton backBtn;
    TextView headerTitle;

    DBHelper db;

    // If editing, this holds the existing task id; -1 means new task
    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Bind views
        title = findViewById(R.id.title);
        desc = findViewById(R.id.description);
        date = findViewById(R.id.date);
        priority = findViewById(R.id.priority);
        save = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);
        headerTitle = findViewById(R.id.headerTitle);

        db = new DBHelper(this);

        // Priority spinner
        String[] priorities = {"High", "Medium", "Low"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                priorities
        );
        priority.setAdapter(adapter);

        // Check if we are editing an existing task
        taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId != -1) {
            // Edit mode: pre-fill fields
            headerTitle.setText("Edit Task");
            save.setText("Update Task");

            title.setText(getIntent().getStringExtra("task_title"));
            desc.setText(getIntent().getStringExtra("task_desc"));
            date.setText(getIntent().getStringExtra("task_date"));

            String existingPriority = getIntent().getStringExtra("task_priority");
            if (existingPriority != null) {
                for (int i = 0; i < priorities.length; i++) {
                    if (priorities[i].equals(existingPriority)) {
                        priority.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            headerTitle.setText("Add New Task");
            save.setText("Save Task");
        }

        // Back button — close this screen
        backBtn.setOnClickListener(v -> finish());

        // Date picker
        date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    AddEditTaskActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        date.setText(selectedDate);
                    },
                    year, month, day
            ).show();
        });

        // Save / Update
        save.setOnClickListener(v -> {
            String taskTitle = title.getText().toString().trim();
            String taskDesc = desc.getText().toString().trim();
            String taskPriority = priority.getSelectedItem().toString();
            String taskDate = date.getText().toString().trim();

            if (taskTitle.isEmpty()) {
                title.setError("Task title is required");
                title.requestFocus();
                return;
            }

            if (taskId == -1) {
                db.addTask(taskTitle, taskDesc, taskPriority, taskDate);
                Toast.makeText(this, "✅ Task added!", Toast.LENGTH_SHORT).show();
            } else {
                db.updateTask(taskId, taskTitle, taskDesc, taskPriority, taskDate);
                Toast.makeText(this, "✅ Task updated!", Toast.LENGTH_SHORT).show();
            }

            finish();
        });
    }
}