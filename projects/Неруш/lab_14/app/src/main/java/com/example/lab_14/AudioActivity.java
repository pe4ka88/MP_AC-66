package com.example.lab_14;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.os.Handler;

public class AudioActivity extends AppCompatActivity {

    private static final int REQUEST_AUDIO = 1;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Button buttonPlay = findViewById(R.id.button_play_audio);
        Button buttonPause = findViewById(R.id.button_pause_audio);
        Button buttonBackToMain = findViewById(R.id.button_back_to_main);
        seekBar = findViewById(R.id.seekBar_audio);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_AUDIO);

        buttonPlay.setOnClickListener(view -> {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                updateSeekBar();
            }
        });

        buttonPause.setOnClickListener(view -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });

        buttonBackToMain.setOnClickListener(view -> {
            Intent intent_back = new Intent(AudioActivity.this, MainActivity.class);
            startActivity(intent_back);
            finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedAudio = data.getData();
            mediaPlayer = MediaPlayer.create(this, selectedAudio);
            if (mediaPlayer != null) {
                seekBar.setMax(mediaPlayer.getDuration());
                updateSeekBar();
            }
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this::updateSeekBar, 1000);
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