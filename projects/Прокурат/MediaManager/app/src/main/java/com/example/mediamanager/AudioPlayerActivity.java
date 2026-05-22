package com.example.mediamanager;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class AudioPlayerActivity extends AppCompatActivity {

    private TextView tvFileName, tvProgress;
    private SeekBar seekBar;
    private Button btnBack, btnPlay, btnPause, btnStop, btnRewind, btnForward;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private Uri audioUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        initViews();
        audioUri = getIntent().getData();

        if (audioUri != null) {
            String name = getFileName(audioUri);
            tvFileName.setText("Файл: " + name);
            setupMediaPlayer();
        } else {
            Toast.makeText(this, "Ошибка: файл не найден", Toast.LENGTH_SHORT).show();
            finish();
        }

        setListeners();
    }

    private String getFileName(Uri uri) {
        String fileName = uri.getLastPathSegment();
        if (fileName != null && fileName.contains(":")) {
            fileName = fileName.substring(fileName.lastIndexOf(":") + 1);
        }
        return fileName != null ? fileName : "audio_file";
    }

    private void initViews() {
        tvFileName = findViewById(R.id.tvFileName);
        tvProgress = findViewById(R.id.tvProgress);
        seekBar = findViewById(R.id.seekBar);
        btnBack = findViewById(R.id.btnBack);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        btnRewind = findViewById(R.id.btnRewind);
        btnForward = findViewById(R.id.btnForward);
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, audioUri);
            mediaPlayer.prepare();

            seekBar.setMax(mediaPlayer.getDuration());
            updateProgress();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка загрузки аудио", Toast.LENGTH_SHORT).show();
        }
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                updateProgress();
            }
        });

        btnPause.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        btnStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(0);
                    seekBar.setProgress(0);
                    tvProgress.setText("00:00 / " + formatTime(mediaPlayer.getDuration()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnRewind.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() - 10000;
                if (newPosition < 0) newPosition = 0;
                mediaPlayer.seekTo(newPosition);
            }
        });

        btnForward.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() + 10000;
                if (newPosition > mediaPlayer.getDuration()) newPosition = mediaPlayer.getDuration();
                mediaPlayer.seekTo(newPosition);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateProgress() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int current = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(current);
                    tvProgress.setText(formatTime(current) + " / " + formatTime(mediaPlayer.getDuration()));
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.post(updateSeekBar);
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (handler != null && updateSeekBar != null) {
            handler.removeCallbacks(updateSeekBar);
        }
    }
}