package com.example.alarmclockapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarms")
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int hour;
    private int minute;
    private boolean isActive;
    private boolean isVibrate;
    private String toneUri;
    private String label;

    public Alarm(int hour, int minute, boolean isActive, boolean isVibrate, String toneUri, String label) {
        this.hour = hour;
        this.minute = minute;
        this.isActive = isActive;
        this.isVibrate = isVibrate;
        this.toneUri = toneUri;
        this.label = label;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }
    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public boolean isVibrate() { return isVibrate; }
    public void setVibrate(boolean vibrate) { isVibrate = vibrate; }
    public String getToneUri() { return toneUri; }
    public void setToneUri(String toneUri) { this.toneUri = toneUri; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getTimeFormatted() {
        int h = hour;
        String ampm = "AM";
        if (h >= 12) {
            ampm = "PM";
            if (h > 12) h -= 12;
        } else if (h == 0) {
            h = 12;
        }
        return String.format("%02d:%02d %s", h, minute, ampm);
    }
}
