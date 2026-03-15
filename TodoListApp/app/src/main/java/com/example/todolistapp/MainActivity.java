package com.example.todolistapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton addBtn;
    TextView taskCountText;
    TextView filterAll, filterActive, filterCompleted;
    View emptyState;

    DBHelper db;
    ArrayList<TaskModel> list;

    // 0 = all, 1 = active (status=0), 2 = completed (status=1)
    private int currentFilter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView    = findViewById(R.id.recyclerView);
        addBtn          = findViewById(R.id.addTaskBtn);
        taskCountText   = findViewById(R.id.taskCountText);
        filterAll       = findViewById(R.id.filterAll);
        filterActive    = findViewById(R.id.filterActive);
        filterCompleted = findViewById(R.id.filterCompleted);
        emptyState      = findViewById(R.id.emptyState);

        db = new DBHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTasks();

        addBtn.setOnClickListener(v ->
            startActivity(new Intent(this, AddEditTaskActivity.class))
        );

        filterAll.setOnClickListener(v -> {
            currentFilter = 0;
            updateFilterChips();
            loadTasks();
        });

        filterActive.setOnClickListener(v -> {
            currentFilter = 1;
            updateFilterChips();
            loadTasks();
        });

        filterCompleted.setOnClickListener(v -> {
            currentFilter = 2;
            updateFilterChips();
            loadTasks();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    void loadTasks() {
        list = new ArrayList<>();

        Cursor cursor;
        if (currentFilter == 1) {
            cursor = db.getTasksByStatus(0); // active only
        } else if (currentFilter == 2) {
            cursor = db.getTasksByStatus(1); // completed only
        } else {
            cursor = db.getTasks();           // all
        }

        while (cursor.moveToNext()) {
            list.add(new TaskModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5)
            ));
        }
        cursor.close();

        // Pending count in header
        int activeCount = db.countActiveTasks();
        taskCountText.setText(activeCount + " task" + (activeCount == 1 ? "" : "s") + " pending");

        // Empty state toggle
        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }

        TaskAdapter adapter = new TaskAdapter(this, list, db, this::loadTasks);
        recyclerView.setAdapter(adapter);
    }

    private void updateFilterChips() {
        // Selected = purple background + white text
        // Unselected = white background + purple text
        applyChip(filterAll,       currentFilter == 0);
        applyChip(filterActive,    currentFilter == 1);
        applyChip(filterCompleted, currentFilter == 2);
    }

    private void applyChip(TextView chip, boolean selected) {
        chip.setBackgroundResource(
            selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected
        );
        chip.setTextColor(selected
            ? 0xFFFFFFFF
            : getResources().getColor(R.color.colorPrimary, null));
    }
}