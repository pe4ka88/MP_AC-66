package com.example.myapplication7;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    private VideoView videoView;
    private ImageView photoImageView;
    private SeekBar audioSeekBar;
    private View playbackControls, mediaControls;
    private Button btnAction;

    private MediaPlayer mediaPlayer;
    private Handler seekBarHandler = new Handler();

    private float currentZoom = 1.0f;
    private Uri selectedVideoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        videoView = findViewById(R.id.videoView);
        photoImageView = findViewById(R.id.photoImageView);
        audioSeekBar = findViewById(R.id.audioSeekBar);
        playbackControls = findViewById(R.id.playbackControls);
        mediaControls = findViewById(R.id.mediaControls);
        btnAction = findViewById(R.id.btnAction);

        setupNavigation();
        setupAudioPlayer();
        setupPlaybackButtons();
        setupVideoAndPhotoControls();


    }

    private void setupNavigation() {
        findViewById(R.id.navAudio).setOnClickListener(v -> showScreen("audio"));
        findViewById(R.id.navVideo).setOnClickListener(v -> showScreen("video"));
        findViewById(R.id.navCamera).setOnClickListener(v -> showScreen("camera"));
    }

    private void showScreen(String type) {
        // Скрываем всё
        videoView.setVisibility(View.GONE);
        photoImageView.setVisibility(View.GONE);
        playbackControls.setVisibility(View.GONE);
        mediaControls.setVisibility(View.GONE);

        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }

        switch (type) {
            case "audio":
                playbackControls.setVisibility(View.VISIBLE);
                audioSeekBar.setVisibility(View.VISIBLE);
                break;
            case "video":
                videoView.setVisibility(View.VISIBLE);
                playbackControls.setVisibility(View.VISIBLE);
                audioSeekBar.setVisibility(View.GONE);
                mediaControls.setVisibility(View.VISIBLE);
                btnAction.setText("Выбрать Видео");
                break;
            case "camera":
                photoImageView.setVisibility(View.VISIBLE);
                mediaControls.setVisibility(View.VISIBLE);
                btnAction.setText("Открыть Камеру");
                break;
        }
    }

    private void setupAudioPlayer() {
        mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_RINGTONE_URI);
        if (mediaPlayer != null) {
            audioSeekBar.setMax(mediaPlayer.getDuration());
        }

        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) mediaPlayer.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    private void setupPlaybackButtons() {

        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            if (videoView.getVisibility() == View.VISIBLE) {

                if (selectedVideoUri != null && !videoView.isPlaying()) {
                    videoView.start();
                } else if (selectedVideoUri == null) {
                    Toast.makeText(this, "Сначала выберите видео", Toast.LENGTH_SHORT).show();
                }
            } else if (mediaPlayer != null && !mediaPlayer.isPlaying()) {

                mediaPlayer.start();
                updateSeekBar();
            }
        });


        findViewById(R.id.btnPause).setOnClickListener(v -> {
            if (videoView.getVisibility() == View.VISIBLE) {

                if (videoView.isPlaying()) {
                    videoView.pause();
                }
            } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
            }
        });
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            audioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            seekBarHandler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private void setupVideoAndPhotoControls() {
        btnAction.setOnClickListener(v -> {
            if (videoView.getVisibility() == View.VISIBLE) {
                openVideoPicker();
            } else if (photoImageView.getVisibility() == View.VISIBLE) {
                openSystemCamera();
            }
        });

        findViewById(R.id.btnZoomIn).setOnClickListener(v -> adjustScale(0.2f));
        findViewById(R.id.btnZoomOut).setOnClickListener(v -> adjustScale(-0.2f));
    }

    private void openSystemCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Камера не найдена!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(resultCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                photoImageView.setImageBitmap(imageBitmap);
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                selectedVideoUri = data.getData();
                videoView.setVideoURI(selectedVideoUri);
                videoView.setOnPreparedListener(mp -> videoView.start());
            }
        }
    }

    private void adjustScale(float factor) {
        currentZoom += factor;
        if (currentZoom < 0.5f) currentZoom = 0.5f;
        if (currentZoom > 3.0f) currentZoom = 3.0f;

        View container = findViewById(R.id.displayContainer);
        container.setScaleX(currentZoom);
        container.setScaleY(currentZoom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        seekBarHandler.removeCallbacksAndMessages(null);
    }
}