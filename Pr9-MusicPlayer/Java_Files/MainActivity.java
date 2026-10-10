package com.example.musicservice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

/**
 * Experiment 9 - UI controller for MusicService. The Activity can be closed
 * while the service keeps playing; reopening it re-binds to the same service.
 */
public class MainActivity extends AppCompatActivity {

    private MusicService service;
    private boolean bound = false;
    private boolean userSeeking = false;

    private TextView tvStatus, tvTime;
    private SeekBar seekBar;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            updateUi();
            handler.postDelayed(this, 500);
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((MusicService.MusicBinder) binder).getService();
            bound = true;
            updateUi();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            bound = false;
        }
    };

    private ActivityResultLauncher<String> notificationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvTime = findViewById(R.id.tvTime);
        seekBar = findViewById(R.id.seekBar);

        // Android 13+: ask permission so the playback notification is visible
        notificationPermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), granted -> { });
        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            Intent i = new Intent(this, MusicService.class).setAction(MusicService.ACTION_PLAY);
            ContextCompat.startForegroundService(this, i);     // START the service
        });
        findViewById(R.id.btnPause).setOnClickListener(v -> {
            if (service != null) service.pause();
        });
        findViewById(R.id.btnStop).setOnClickListener(v -> {
            if (service != null) service.stopPlayback();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser && service != null) service.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
                userSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
                userSeeking = false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MusicService.class), connection, BIND_AUTO_CREATE);
        handler.post(ticker);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(ticker);
        if (bound) {
            unbindService(connection);      // music continues because the service was STARTED
            bound = false;
            service = null;
        }
    }

    private void updateUi() {
        if (service == null) {
            tvStatus.setText("Status: connecting to service...");
            return;
        }
        int duration = service.getDuration();
        int position = service.getCurrentPosition();
        tvStatus.setText(service.isPlaying() ? "Status: Playing (in background service)" : "Status: Paused / Stopped");
        if (!userSeeking) {
            seekBar.setMax(Math.max(duration, 1));
            seekBar.setProgress(position);
        }
        tvTime.setText(format(position) + " / " + format(duration));
    }

    private String format(int millis) {
        int totalSeconds = millis / 1000;
        return String.format(Locale.getDefault(), "%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}
