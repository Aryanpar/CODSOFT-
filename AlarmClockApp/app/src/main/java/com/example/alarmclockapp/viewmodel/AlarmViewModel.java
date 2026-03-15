package com.example.alarmclockapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.alarmclockapp.db.AlarmRepository;
import com.example.alarmclockapp.model.Alarm;

import java.util.List;

public class AlarmViewModel extends AndroidViewModel {
    private AlarmRepository repository;
    private LiveData<List<Alarm>> allAlarms;

    public AlarmViewModel(Application application) {
        super(application);
        repository = new AlarmRepository(application);
        allAlarms = repository.getAllAlarms();
    }

    public LiveData<List<Alarm>> getAllAlarms() {
        return allAlarms;
    }

    public void insert(Alarm alarm, AlarmRepository.OnAlarmInsertedListener listener) {
        repository.insert(alarm, listener);
    }

    public void update(Alarm alarm) {
        repository.update(alarm);
    }

    public void delete(Alarm alarm) {
        repository.delete(alarm);
    }
}
