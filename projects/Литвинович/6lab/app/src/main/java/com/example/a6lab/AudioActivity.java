package com.example.a6lab;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class AudioActivity extends AppCompatActivity {

    private TextView tvAudioName;
    private TextView tvTime;
    private Button btnPlayPause;
    private Button btnStop;
    private Button btnRewind;
    private Button btnForward;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        tvAudioName = findViewById(R.id.tvAudioName);
        tvTime = findViewById(R.id.tvTime);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnStop = findViewById(R.id.btnStop);
        btnRewind = findViewById(R.id.btnRewind);
        btnForward = findViewById(R.id.btnForward);

        btnRewind.setText("\u00AB\u00AB");
        btnForward.setText("\u00BB\u00BB");

        String name = getIntent().getStringExtra("name");
        String uriString = getIntent().getStringExtra("uri");

        tvAudioName.setText(name);
        btnPlayPause.setText("\u25B6");

        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, uri);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> {
                    btnPlayPause.setEnabled(true);
                    btnStop.setEnabled(true);
                    btnRewind.setEnabled(true);
                    btnForward.setEnabled(true);
                    updateTimeDisplay();
                });
                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    btnPlayPause.setText("\u25B6");
                    updateTimeDisplay();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer == null) return;
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
                btnPlayPause.setText("\u25B6");
                stopTimeUpdater();
            } else {
                mediaPlayer.start();
                isPlaying = true;
                btnPlayPause.setText("\u23F8");
                startTimeUpdater();
            }
        });

        btnStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
                if (isPlaying) {
                    mediaPlayer.pause();
                    isPlaying = false;
                }
                btnPlayPause.setText("\u25B6");
                stopTimeUpdater();
                updateTimeDisplay();
            }
        });

        btnRewind.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() - 10000;
                if (newPosition < 0) newPosition = 0;
                mediaPlayer.seekTo(newPosition);
                updateTimeDisplay();
            }
        });

        btnForward.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() + 10000;
                if (newPosition > mediaPlayer.getDuration()) {
                    newPosition = mediaPlayer.getDuration();
                }
                mediaPlayer.seekTo(newPosition);
                updateTimeDisplay();
            }
        });
    }

    private void startTimeUpdater() {
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimeDisplay();
                handler.postDelayed(this, 500);
            }
        };
        handler.post(updateTimeRunnable);
    }

    private void stopTimeUpdater() {
        if (updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }

    private void updateTimeDisplay() {
        if (mediaPlayer != null && mediaPlayer.getDuration() > 0) {
            String current = formatTime(mediaPlayer.getCurrentPosition());
            String total = formatTime(mediaPlayer.getDuration());
            tvTime.setText(current + " / " + total);
        } else {
            tvTime.setText("00:00 / 00:00");
        }
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimeUpdater();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}