package com.example.mediamanager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    private TextView tvFileName, tvVideoProgress;
    private VideoView videoView;
    private SeekBar seekBar;
    private Button btnBack, btnVideoPlay, btnVideoPause, btnVideoStop, btnVideoRewind, btnVideoForward;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        initViews();
        videoUri = getIntent().getData();

        if (videoUri != null) {
            String name = getFileName(videoUri);
            tvFileName.setText("Файл: " + name);
            setupVideoPlayer();
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
        return fileName != null ? fileName : "video_file";
    }

    private void initViews() {
        tvFileName = findViewById(R.id.tvFileName);
        tvVideoProgress = findViewById(R.id.tvVideoProgress);
        videoView = findViewById(R.id.videoView);
        seekBar = findViewById(R.id.videoSeekBar);
        btnBack = findViewById(R.id.btnBack);
        btnVideoPlay = findViewById(R.id.btnVideoPlay);
        btnVideoPause = findViewById(R.id.btnVideoPause);
        btnVideoStop = findViewById(R.id.btnVideoStop);
        btnVideoRewind = findViewById(R.id.btnVideoRewind);
        btnVideoForward = findViewById(R.id.btnVideoForward);
    }

    private void setupVideoPlayer() {
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            seekBar.setMax(videoView.getDuration());
            updateVideoProgress();
            Toast.makeText(this, "Видео готово к воспроизведению", Toast.LENGTH_SHORT).show();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "Ошибка воспроизведения видео", Toast.LENGTH_SHORT).show();
            return true;
        });

        videoView.setOnCompletionListener(mp -> {
            seekBar.setProgress(0);
            tvVideoProgress.setText("00:00 / " + formatTime(videoView.getDuration()));
        });
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnVideoPlay.setOnClickListener(v -> {
            if (!videoView.isPlaying()) {
                videoView.start();
                updateVideoProgress();
            }
        });

        btnVideoPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
            }
        });

        btnVideoStop.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
                videoView.setVideoURI(videoUri);
                seekBar.setProgress(0);
                tvVideoProgress.setText("00:00 / " + formatTime(videoView.getDuration()));
            }
        });

        btnVideoRewind.setOnClickListener(v -> {
            int current = videoView.getCurrentPosition();
            int newPosition = current - 10000;
            if (newPosition < 0) newPosition = 0;
            videoView.seekTo(newPosition);
            seekBar.setProgress(newPosition);
        });

        btnVideoForward.setOnClickListener(v -> {
            int current = videoView.getCurrentPosition();
            int newPosition = current + 10000;
            if (newPosition > videoView.getDuration()) newPosition = videoView.getDuration();
            videoView.seekTo(newPosition);
            seekBar.setProgress(newPosition);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateVideoProgress() {
        if (updateSeekBar != null) {
            handler.removeCallbacks(updateSeekBar);
        }

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (videoView != null && videoView.isPlaying()) {
                    int current = videoView.getCurrentPosition();
                    int duration = videoView.getDuration();
                    seekBar.setProgress(current);
                    tvVideoProgress.setText(formatTime(current) + " / " + formatTime(duration));
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
        if (videoView != null) {
            videoView.stopPlayback();
        }
        if (handler != null && updateSeekBar != null) {
            handler.removeCallbacks(updateSeekBar);
        }
    }
}