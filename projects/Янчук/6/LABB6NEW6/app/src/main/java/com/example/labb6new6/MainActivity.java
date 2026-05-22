package com.example.labb6new6;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_PICK_CODE = 100;

    private ImageView imageView;
    private VideoView videoView;
    private MediaPlayer mediaPlayer;

    private LinearLayout audioControls;
    private Button btnPlay, btnPause, btnStop;

    private Uri currentAudioUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);

        audioControls = findViewById(R.id.audioControls);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);

        Button btnSelect = findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(v -> openFilePicker());

        btnPlay.setOnClickListener(v -> playAudio());
        btnPause.setOnClickListener(v -> pauseAudio());
        btnStop.setOnClickListener(v -> stopAudio());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                handleFile(uri);
            }
        }
    }

    private void handleFile(Uri uri) {
        String extension = getFileExtension(uri);
        if (extension != null) extension = extension.toLowerCase();

        imageView.setVisibility(ImageView.GONE);
        videoView.setVisibility(VideoView.GONE);
        audioControls.setVisibility(LinearLayout.GONE);

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (extension == null) return;

        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
                imageView.setImageURI(uri);
                imageView.setVisibility(ImageView.VISIBLE);
                break;

            case "mp3":
            case "wav":
            case "ogg":
                currentAudioUri = uri;
                audioControls.setVisibility(LinearLayout.VISIBLE);
                mediaPlayer = MediaPlayer.create(this, uri);
                mediaPlayer.start();
                break;

            case "mp4":
            case "3gp":
            case "mkv":
                videoView.setVideoURI(uri);
                videoView.start();
                videoView.setVisibility(VideoView.VISIBLE);
                break;
        }
    }

    private void playAudio() {
        if (mediaPlayer == null && currentAudioUri != null) {
            mediaPlayer = MediaPlayer.create(this, currentAudioUri);
        }
        if (mediaPlayer != null) mediaPlayer.start();
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private String getFileExtension(Uri uri) {
        String mime = getContentResolver().getType(uri);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
    }
}

