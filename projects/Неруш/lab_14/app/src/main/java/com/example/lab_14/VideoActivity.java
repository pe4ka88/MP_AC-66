package com.example.lab_14;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;
import android.os.Handler;

public class VideoActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO = 2;
    private VideoView videoView;
    private SeekBar seekBar;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Button buttonPlay = findViewById(R.id.button_play_video);
        Button buttonPause = findViewById(R.id.button_pause_video);
        Button buttonBackToMain = findViewById(R.id.button_back_to_main);
        videoView = findViewById(R.id.videoView);
        seekBar = findViewById(R.id.seekBar_video);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_VIDEO);

        buttonPlay.setOnClickListener(view -> {
            if (videoView != null) {
                videoView.start();
                updateSeekBar();
            }
        });

        buttonPause.setOnClickListener(view -> {
            if (videoView != null) {
                videoView.pause();
            }
        });

        buttonBackToMain.setOnClickListener(view -> {
            Intent intent_back = new Intent(VideoActivity.this, MainActivity.class);
            startActivity(intent_back);
            finish();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && videoView != null) {
                    videoView.seekTo(progress);
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
            Uri selectedVideo = data.getData();
            videoView.setVideoURI(selectedVideo);
            videoView.setOnPreparedListener(mp -> {
                seekBar.setMax(mp.getDuration());
                updateSeekBar();
            });
        }
    }

    private void updateSeekBar() {
        if (videoView != null && videoView.isPlaying()) {
            seekBar.setProgress(videoView.getCurrentPosition());
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }
}