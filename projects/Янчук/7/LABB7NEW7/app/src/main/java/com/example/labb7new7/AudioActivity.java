package com.example.labb7new7;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class AudioActivity extends AppCompatActivity {

    private static final int PICK_AUDIO = 1;

    private MediaPlayer mediaPlayer;
    private Uri currentAudioUri;

    private LinearLayout audioControls;
    private TextView txtFileName, txtTime;
    private SeekBar seekBar, volumeBar;

    private Handler handler = new Handler();
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        new HistoryManager(this).addAction("Открыто аудио");

        Button btnSelectAudio = findViewById(R.id.btnSelectAudio);
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnPause = findViewById(R.id.btnPause);
        Button btnStop = findViewById(R.id.btnStop);

        txtFileName = findViewById(R.id.txtFileName);
        txtTime = findViewById(R.id.txtTime);
        audioControls = findViewById(R.id.audioControls);
        seekBar = findViewById(R.id.seekBar);
        volumeBar = findViewById(R.id.volumeBar);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Настройка громкости
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSelectAudio.setOnClickListener(v -> selectAudioFile());
        btnPlay.setOnClickListener(v -> playAudio());
        btnPause.setOnClickListener(v -> pauseAudio());
        btnStop.setOnClickListener(v -> stopAudio());
    }

    private void selectAudioFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO && resultCode == Activity.RESULT_OK) {
            currentAudioUri = data.getData();
            if (currentAudioUri != null) {

                txtFileName.setText("Выбран файл: " + currentAudioUri.getLastPathSegment());
                audioControls.setVisibility(LinearLayout.VISIBLE);

                if (mediaPlayer != null) mediaPlayer.release();

                mediaPlayer = MediaPlayer.create(this, currentAudioUri);

                // Настройка SeekBar
                seekBar.setMax(mediaPlayer.getDuration());

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) mediaPlayer.seekTo(progress);
                    }
                    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                updateSeekBar();
            }
        }
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());

                    txtTime.setText(
                            formatTime(mediaPlayer.getCurrentPosition()) +
                                    " / " +
                                    formatTime(mediaPlayer.getDuration())
                    );

                    updateSeekBar();
                }
            }
        }, 500);
    }

    private String formatTime(int ms) {
        int sec = ms / 1000;
        int min = sec / 60;
        sec = sec % 60;
        return String.format("%02d:%02d", min, sec);
    }

    private void playAudio() {
        if (mediaPlayer != null) mediaPlayer.start();
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, currentAudioUri);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
    }
}

