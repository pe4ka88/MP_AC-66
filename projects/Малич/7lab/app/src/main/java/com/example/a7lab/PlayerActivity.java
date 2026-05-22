package com.example.a7lab;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {
    private ImageButton btnBack, btnPlayPause, btnNext, btnPrevious;
    private SeekBar seekBar;
    private TextView tvTrackTitle, tvCurrentTime, tvDuration;
    private ImageView ivAlbumArt;

    private List<Track> trackList;
    private int currentIndex;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnBack = findViewById(R.id.btnBack);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        seekBar = findViewById(R.id.seekBar);
        tvTrackTitle = findViewById(R.id.tvTrackTitle);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvDuration = findViewById(R.id.tvDuration);
        ivAlbumArt = findViewById(R.id.ivAlbumArt);

        Intent intent = getIntent();
        currentIndex = intent.getIntExtra("track_index", 0);
        String tracksJson = intent.getStringExtra("tracks");
        Type type = new TypeToken<List<Track>>(){}.getType();
        trackList = new Gson().fromJson(tracksJson, type);

        mediaPlayer = new MediaPlayer();
        loadTrack(currentIndex);

        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                pauseTrack();
            } else {
                playTrack();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < trackList.size() - 1) {
                currentIndex++;
                loadTrack(currentIndex);
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                if (currentPosition > 3000) {
                    mediaPlayer.seekTo(0);
                    seekBar.setProgress(0);
                    tvCurrentTime.setText(formatTime(0));
                } else {
                    if (currentIndex > 0) {
                        currentIndex--;
                        loadTrack(currentIndex);
                    } else {
                        mediaPlayer.seekTo(0);
                        seekBar.setProgress(0);
                        tvCurrentTime.setText(formatTime(0));
                    }
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    updateCurrentTime();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadTrack(int index) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
        }

        Track track = trackList.get(index);
        tvTrackTitle.setText(track.getName());

        try {
            Uri uri = Uri.parse(track.getUri());
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();

            seekBar.setMax(mediaPlayer.getDuration());
            tvDuration.setText(formatTime(mediaPlayer.getDuration()));
            tvCurrentTime.setText(formatTime(0));
            seekBar.setProgress(0);

            playTrack();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playTrack() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            updateSeekBar();
        }
    }

    private void pauseTrack() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && isPlaying) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            updateCurrentTime();

            if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()) {
                if (currentIndex < trackList.size() - 1) {
                    currentIndex++;
                    loadTrack(currentIndex);
                } else {
                    pauseTrack();
                }
            } else {
                handler.postDelayed(updateSeekBarRunnable, 1000);
            }
        }
    }

    private Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
        }
    };

    private void updateCurrentTime() {
        String currentTime = formatTime(mediaPlayer.getCurrentPosition());
        tvCurrentTime.setText(currentTime);
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
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBarRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTrack();
    }
}