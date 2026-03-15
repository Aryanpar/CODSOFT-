package com.example.alarmclockapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclockapp.model.Alarm;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AlarmAdapter extends ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder> {

    private OnAlarmToggleListener listener;

    public interface OnAlarmToggleListener {
        void onToggle(Alarm alarm, boolean isActive);
        void onDelete(Alarm alarm);
    }

    public AlarmAdapter(OnAlarmToggleListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Alarm> DIFF_CALLBACK = new DiffUtil.ItemCallback<Alarm>() {
        @Override
        public boolean areItemsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
            return oldItem.getHour() == newItem.getHour() &&
                    oldItem.getMinute() == newItem.getMinute() &&
                    oldItem.isActive() == newItem.isActive();
        }
    };

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = getItem(position);
        
        // Format time and am/pm separately
        int hour = alarm.getHour();
        String ampm = "AM";
        if (hour >= 12) {
            ampm = "PM";
            if (hour > 12) hour -= 12;
        } else if (hour == 0) {
            hour = 12;
        }
        
        holder.timeText.setText(String.format("%02d:%02d", hour, alarm.getMinute()));
        holder.ampmText.setText(ampm);
        holder.labelText.setText(alarm.getLabel() == null || alarm.getLabel().isEmpty() ? "Scheduled Alarm" : alarm.getLabel());
        
        // Remove listener before setting state to avoid loops
        holder.alarmSwitch.setOnCheckedChangeListener(null);
        holder.alarmSwitch.setChecked(alarm.isActive());

        holder.alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onToggle(alarm, isChecked);
        });

        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(alarm);
            return true;
        });
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView ampmText;
        TextView labelText;
        SwitchMaterial alarmSwitch;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.textViewTime);
            ampmText = itemView.findViewById(R.id.textViewAmPm);
            labelText = itemView.findViewById(R.id.textViewLabel);
            alarmSwitch = itemView.findViewById(R.id.switchAlarm);
        }
    }
}
