package com.example.musicservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * Experiment 9 - Foreground Service that plays music in the background.
 * It is both STARTED (so it keeps running after the Activity is closed)
 * and BOUND (so the Activity can read position / call pause, seek).
 */
public class MusicService extends Service {

    public static final String ACTION_PLAY = "com.example.musicservice.PLAY";
    public static final String ACTION_PAUSE = "com.example.musicservice.PAUSE";
    public static final String ACTION_STOP = "com.example.musicservice.STOP";

    private static final String CHANNEL_ID = "music_playback";
    private static final int NOTIFICATION_ID = 101;

    private MediaPlayer player;
    private final IBinder binder = new MusicBinder();

    /** Binder that gives the Activity a direct reference to this service. */
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        player = MediaPlayer.create(this, R.raw.sample_music);   // audio file in res/raw
        if (player != null) {
            player.setLooping(true);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : null;
        if (ACTION_PAUSE.equals(action)) {
            pause();
        } else if (ACTION_STOP.equals(action)) {
            stopPlayback();
        } else {
            play();
        }
        return START_NOT_STICKY;
    }

    // ------------------------------------------------------------ playback controls
    public void play() {
        if (player == null) {
            Toast.makeText(this, "Audio file could not be loaded", Toast.LENGTH_LONG).show();
            stopSelf();
            return;
        }
        if (!player.isPlaying()) {
            player.start();
        }
        // Promote to foreground service (shows notification) so Android does not kill it
        startForeground(NOTIFICATION_ID, buildNotification(true));
    }

    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
        stopForeground(Service.STOP_FOREGROUND_DETACH);       // keep notification, allow swipe-away
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, buildNotification(false));
    }

    public void stopPlayback() {
        if (player != null) {
            if (player.isPlaying()) player.pause();
            player.seekTo(0);
        }
        stopForeground(Service.STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    public void seekTo(int millis) {
        if (player != null) player.seekTo(millis);
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public int getCurrentPosition() {
        return player != null ? player.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return player != null ? player.getDuration() : 0;
    }

    // ------------------------------------------------------------ notification
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Music playback", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Shows the currently playing music");
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
    }

    private PendingIntent actionIntent(String action, int requestCode) {
        Intent i = new Intent(this, MusicService.class).setAction(action);
        return PendingIntent.getService(this, requestCode, i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Notification buildNotification(boolean playing) {
        PendingIntent open = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle("Background Music Player")
                .setContentText(playing ? "Playing: Sample Melody" : "Paused")
                .setContentIntent(open)
                .setOngoing(playing)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        if (playing) {
            b.addAction(android.R.drawable.ic_media_pause, "Pause", actionIntent(ACTION_PAUSE, 1));
        } else {
            b.addAction(android.R.drawable.ic_media_play, "Play", actionIntent(ACTION_PLAY, 2));
        }
        b.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", actionIntent(ACTION_STOP, 3));
        return b.build();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }
}
