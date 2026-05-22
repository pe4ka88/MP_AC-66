package com.example.fileexplorer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;
import android.widget.MediaController;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MediaDisplayActivity extends AppCompatActivity {

    private TextView tvMediaInfo, tvAudioProgress;
    private ImageView imageView;
    private VideoView videoView;
    private Button btnBack, btnPlay, btnPause, btnStop;
    private SeekBar seekBar;
    private LinearLayout audioControls;

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private Uri mediaUri;
    private String fileName;
    private String fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_display);

        initViews();
        getDataFromIntent();
        setupBackButton();
        processMediaFile();
    }

    private void initViews() {
        tvMediaInfo = findViewById(R.id.tvMediaInfo);
        tvAudioProgress = findViewById(R.id.tvAudioProgress);
        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);
        btnBack = findViewById(R.id.btnBack);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        seekBar = findViewById(R.id.seekBar);
        audioControls = findViewById(R.id.audioControls);
    }

    private void getDataFromIntent() {
        mediaUri = getIntent().getData();
        fileName = getIntent().getStringExtra("file_name");
        fileType = getFileType(fileName);

        tvMediaInfo.setText("Файл: " + fileName + "\nТип: " + fileType);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void processMediaFile() {
        if (mediaUri == null) {
            Toast.makeText(this, "Ошибка: файл не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (fileType.equals("графический файл")) {
            displayImage();
        } else if (fileType.equals("аудиофайл")) {
            setupAudioPlayer();
        } else if (fileType.equals("видеофайл")) {
            setupVideoPlayer();
        } else {
            Toast.makeText(this, "Неподдерживаемый тип файла", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayImage() {
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        audioControls.setVisibility(View.GONE);

        imageView.setImageURI(mediaUri);
    }

    private void setupVideoPlayer() {
        imageView.setVisibility(View.GONE);
        audioControls.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(mediaUri);
        videoView.requestFocus();

        videoView.setOnPreparedListener(mp -> {
            Toast.makeText(this, "Видео готово к воспроизведению", Toast.LENGTH_SHORT).show();
            videoView.start();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "Ошибка воспроизведения видео", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void setupAudioPlayer() {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        audioControls.setVisibility(View.VISIBLE);

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(this, mediaUri);
            mediaPlayer.prepare();

            seekBar.setMax(mediaPlayer.getDuration());
            updateAudioProgress();

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

            btnPlay.setOnClickListener(v -> {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    updateAudioProgress();
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
                        tvAudioProgress.setText("00:00 / " + formatTime(mediaPlayer.getDuration()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                seekBar.setProgress(0);
                tvAudioProgress.setText("00:00 / " + formatTime(mediaPlayer.getDuration()));
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка загрузки аудиофайла", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAudioProgress() {
        if (mediaPlayer == null) return;

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    tvAudioProgress.setText(formatTime(currentPosition) + " / " +
                            formatTime(mediaPlayer.getDuration()));
                    handler.postDelayed(this, 1000);
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

    private String getFileType(String fileName) {
        if (fileName == null) return "неизвестен";

        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png") || lowerName.endsWith(".gif") ||
                lowerName.endsWith(".bmp")) {
            return "графический файл";
        } else if (lowerName.endsWith(".mp3") || lowerName.endsWith(".wav") ||
                lowerName.endsWith(".ogg") || lowerName.endsWith(".aac")) {
            return "аудиофайл";
        } else if (lowerName.endsWith(".mp4") || lowerName.endsWith(".avi") ||
                lowerName.endsWith(".mkv") || lowerName.endsWith(".3gp")) {
            return "видеофайл";
        } else {
            return "неподдерживаемый тип";
        }
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
        if (handler != null && updateSeekBar != null) {
            handler.removeCallbacks(updateSeekBar);
        }
    }
}