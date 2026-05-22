package com.example.lab7;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AudioActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        dbHelper = new DatabaseHelper(this);
        dbHelper.addRecord("Открыта активность Аудио (Ляшук В.И.)");

        try {
            int resId = getResources().getIdentifier("audio_sample", "raw", getPackageName());
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId);
            } else {
                Toast.makeText(this, "Файл audio_sample не найден в res/raw!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        seekBar = findViewById(R.id.audioSeekBar);
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnPause = findViewById(R.id.btnPause);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnBack = findViewById(R.id.btnBackAudio);

        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                updateSeekBar();
                Toast.makeText(this, "Ляшук В.И.: Воспроизведение", Toast.LENGTH_SHORT).show();
                dbHelper.addRecord("Воспроизведение аудио начато (Ляшук В.И.)");
            }
        });

        btnPause.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                dbHelper.addRecord("Аудио на паузе");
            }
        });

        btnStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(0);
                } catch (Exception e) { e.printStackTrace(); }
                dbHelper.addRecord("Аудио остановлено");
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBar.setMax(mediaPlayer.getDuration());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        handler.postDelayed(this, 1000);
                    }
                }
            }, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
