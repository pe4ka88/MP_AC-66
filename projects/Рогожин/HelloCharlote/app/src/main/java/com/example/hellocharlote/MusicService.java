package com.example.hellocharlote;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    private static final String TAG = "MusicService";

    private MediaPlayer mediaPlayer;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "game_settings";
    private static final String KEY_MUSIC = "music_enabled";
    private static final String KEY_VOLUME = "music_volume";

    // Плейлист
    private final int[] tracks = {
            R.raw.track1,
            R.raw.track2,
            R.raw.track3,
            R.raw.track4
    };
    private int currentTrack = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        startCurrentTrack();
    }

    private void startCurrentTrack() {
        boolean musicEnabled = prefs.getBoolean(KEY_MUSIC, true);
        int volume = prefs.getInt(KEY_VOLUME, 70);
        float vol = volume / 100f;

        Log.d(TAG, "Starting track " + currentTrack + ", musicEnabled=" + musicEnabled + ", volume=" + volume);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(this, tracks[currentTrack]);
        mediaPlayer.setVolume(vol, vol);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d(TAG, "Track " + currentTrack + " completed");
            currentTrack = (currentTrack + 1) % tracks.length;
            startCurrentTrack(); // запускаем следующий трек
        });

        if (musicEnabled) {
            mediaPlayer.start();
            Log.d(TAG, "Track " + currentTrack + " started playing");
        } else {
            Log.d(TAG, "Music is disabled, track not started");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean musicEnabled = prefs.getBoolean(KEY_MUSIC, true);
        int volume = prefs.getInt(KEY_VOLUME, 70);
        float vol = volume / 100f;

        Log.d(TAG, "onStartCommand called, musicEnabled=" + musicEnabled + ", volume=" + volume);

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(vol, vol);
            if (musicEnabled && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                Log.d(TAG, "Music started/resumed");
            } else if (!musicEnabled && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Log.d(TAG, "Music paused");
            }
        } else {
            Log.d(TAG, "mediaPlayer is null, starting current track");
            startCurrentTrack();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}