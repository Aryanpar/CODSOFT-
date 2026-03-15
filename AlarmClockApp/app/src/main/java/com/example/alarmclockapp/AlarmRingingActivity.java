package com.example.alarmclockapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmRingingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ensure the activity shows even when locked
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        setContentView(R.layout.activity_alarm_ringing);

        alarmId = getIntent().getIntExtra("ALARM_ID", -1);
        
        android.app.NotificationManager mgr = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mgr != null && alarmId != -1) {
            mgr.cancel(alarmId);
        }

        TextView timeText = findViewById(R.id.textViewRingTime);
        Button btnSnooze = findViewById(R.id.btnSnooze);
        Button btnDismiss = findViewById(R.id.btnDismiss);
        View glow = findViewById(R.id.glowCircle);

        timeText.setText(new SimpleDateFormat("hh:mm", Locale.getDefault()).format(new Date()));

        // Simple pulse animation for the background glow
        AlphaAnimation pulse = new AlphaAnimation(0.1f, 0.4f);
        pulse.setDuration(1000);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        glow.startAnimation(pulse);

        startRinging();

        btnSnooze.setOnClickListener(v -> snooze());
        btnDismiss.setOnClickListener(v -> dismiss());
    }

    private void startRinging() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (notification == null) {
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
            
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, notification);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void snooze() {
        stopRinging();
        
        // Schedule a snooze alarm in 5 minutes
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ALARM_ID", alarmId);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 
                alarmId + 1000, // Use a different ID for snooze instances
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long snoozeTime = System.currentTimeMillis() + (5 * 60000); // 5 minutes

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
        }

        Toast.makeText(this, "Snoozed for 5 minutes", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void dismiss() {
        stopRinging();
        // In a more complete app, we would update the alarm state in DB to 'off' if it's not repeating
        finish();
    }

    private void stopRinging() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRinging();
    }
}
