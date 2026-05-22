package com.example.a6lab;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button btnPlayPause;
    private Button btnRewind;
    private Button btnForward;
    private View controlsLayout;
    private boolean isPlaying = false;
    private boolean controlsVisible = true;
    private android.os.Handler hideHandler = new android.os.Handler();
    private Runnable hideControlsRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnRewind = findViewById(R.id.btnRewind);
        btnForward = findViewById(R.id.btnForward);
        controlsLayout = findViewById(R.id.controlsLayout);

        btnRewind.setText("\u00AB\u00AB");
        btnForward.setText("\u00BB\u00BB");

        String uriString = getIntent().getStringExtra("uri");
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            videoView.setVideoURI(uri);
            videoView.setOnPreparedListener(mp -> {
                videoView.start();
                isPlaying = true;
                btnPlayPause.setText("\u23F8");
                startControlsAutoHide();
            });
            videoView.setOnCompletionListener(mp -> {
                isPlaying = false;
                btnPlayPause.setText("\u25B6");
                controlsLayout.setVisibility(View.VISIBLE);
                controlsVisible = true;
            });
        }

        videoView.setOnClickListener(v -> {
            if (controlsVisible) {
                controlsLayout.setVisibility(View.GONE);
                controlsVisible = false;
                stopControlsAutoHide();
            } else {
                controlsLayout.setVisibility(View.VISIBLE);
                controlsVisible = true;
                if (isPlaying) {
                    startControlsAutoHide();
                }
            }
        });

        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                videoView.pause();
                isPlaying = false;
                btnPlayPause.setText("\u25B6");
                controlsLayout.setVisibility(View.VISIBLE);
                controlsVisible = true;
                stopControlsAutoHide();
            } else {
                videoView.start();
                isPlaying = true;
                btnPlayPause.setText("\u23F8");
                startControlsAutoHide();
            }
        });

        btnRewind.setOnClickListener(v -> {
            int newPosition = videoView.getCurrentPosition() - 10000;
            if (newPosition < 0) newPosition = 0;
            videoView.seekTo(newPosition);
        });

        btnForward.setOnClickListener(v -> {
            int newPosition = videoView.getCurrentPosition() + 10000;
            videoView.seekTo(newPosition);
        });
    }

    private void startControlsAutoHide() {
        stopControlsAutoHide();
        hideControlsRunnable = () -> {
            controlsLayout.setVisibility(View.GONE);
            controlsVisible = false;
        };
        hideHandler.postDelayed(hideControlsRunnable, 3000);
    }

    private void stopControlsAutoHide() {
        if (hideControlsRunnable != null) {
            hideHandler.removeCallbacks(hideControlsRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopControlsAutoHide();
    }
}