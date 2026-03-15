package com.example.alarmclockapp.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarmclockapp.model.Alarm;

import java.util.List;

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<Alarm>> allAlarms;

    public AlarmRepository(Application application) {
        AlarmDatabase db = AlarmDatabase.getDatabase(application);
        alarmDao = db.alarmDao();
        allAlarms = alarmDao.getAllAlarms();
    }

    public LiveData<List<Alarm>> getAllAlarms() {
        return allAlarms;
    }

    public interface OnAlarmInsertedListener {
        void onAlarmInserted(Alarm alarm);
    }

    public void insert(Alarm alarm, OnAlarmInsertedListener listener) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> {
            long id = alarmDao.insert(alarm);
            alarm.setId((int) id);
            if (listener != null) {
                listener.onAlarmInserted(alarm);
            }
        });
    }

    public void update(Alarm alarm) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> alarmDao.update(alarm));
    }

    public void delete(Alarm alarm) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> alarmDao.delete(alarm));
    }
}
