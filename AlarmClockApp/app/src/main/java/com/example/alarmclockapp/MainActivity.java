package com.example.alarmclockapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclockapp.model.Alarm;
import com.example.alarmclockapp.viewmodel.AlarmViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class MainActivity extends AppCompatActivity implements AlarmAdapter.OnAlarmToggleListener {

    private AlarmViewModel alarmViewModel;
    private AlarmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewAlarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AlarmAdapter(this);
        recyclerView.setAdapter(adapter);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        alarmViewModel.getAllAlarms().observe(this, alarms -> {
            adapter.submitList(alarms);
            findViewById(R.id.textViewEmpty).setVisibility(alarms.isEmpty() ? View.VISIBLE : View.GONE);
        });

        ExtendedFloatingActionButton fab = findViewById(R.id.fabAddAlarm);
        fab.setOnClickListener(v -> showTimePicker());
    }

    private void showTimePicker() {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Set Alarm Time")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            showLabelDialog(picker.getHour(), picker.getMinute());
        });

        picker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    private void showLabelDialog(int hour, int minute) {
        final EditText input = new EditText(this);
        input.setHint("e.g. Wake up, Gym, Meeting");
        
        new AlertDialog.Builder(this)
                .setTitle("Alarm Label")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String label = input.getText().toString();
                    if (label.isEmpty()) label = "Alarm";
                    
                    Alarm newAlarm = new Alarm(hour, minute, true, true, null, label);
                    alarmViewModel.insert(newAlarm, insertedAlarm -> {
                        runOnUiThread(() -> {
                            AlarmScheduler.scheduleAlarm(MainActivity.this, insertedAlarm);
                            Toast.makeText(MainActivity.this, "Alarm set!", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Skip", (dialog, which) -> {
                    Alarm newAlarm = new Alarm(hour, minute, true, true, null, "Alarm");
                    alarmViewModel.insert(newAlarm, insertedAlarm -> {
                        runOnUiThread(() -> {
                            AlarmScheduler.scheduleAlarm(MainActivity.this, insertedAlarm);
                            Toast.makeText(MainActivity.this, "Alarm set!", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .show();
    }

    @Override
    public void onToggle(Alarm alarm, boolean isActive) {
        alarm.setActive(isActive);
        alarmViewModel.update(alarm);
        
        if (isActive) {
            AlarmScheduler.scheduleAlarm(this, alarm);
            Toast.makeText(this, "Alarm Enabled", Toast.LENGTH_SHORT).show();
        } else {
            AlarmScheduler.cancelAlarm(this, alarm);
            Toast.makeText(this, "Alarm Disabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDelete(Alarm alarm) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Alarm")
                .setMessage("Are you sure you want to remove this alarm?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    AlarmScheduler.cancelAlarm(this, alarm);
                    alarmViewModel.delete(alarm);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}