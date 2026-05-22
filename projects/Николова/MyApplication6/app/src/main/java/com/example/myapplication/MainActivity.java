package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Insets;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private static final int PICKFILE_RESULT_CODE = 1;

    private boolean permissionGranted = false;
    private MediaPlayer mPlayer;
    private Button startButton, pauseButton;
    private VideoView videoView;
    private ImageView imageView;
    private TextView emptyStateText;
    private String setType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start);
        pauseButton = findViewById(R.id.pause);
        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);
        emptyStateText = findViewById(R.id.emptyStateText);

        View mainLayout = findViewById(R.id.main_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mainLayout.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return WindowInsets.CONSUMED;
            });
        }

        checkPermissions();
    }

    public void onClFile(View viewButton) {
        if (!permissionGranted && !checkPermissions()) {
            Toast.makeText(this, "Нужны разрешения для работы с файлами", Toast.LENGTH_SHORT).show();
            return;
        }

        if (viewButton.getId() == R.id.buttonAudio) setType = "audio/*";
        else if (viewButton.getId() == R.id.buttonVideo) setType = "video/*";
        else if (viewButton.getId() == R.id.buttonImage) setType = "image/*";

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            resetVisibility();
            emptyStateText.setVisibility(View.GONE);

            if (setType.equals("audio/*")) {
                setupAudio(fileUri);
            } else if (setType.equals("video/*")) {
                setupVideo(fileUri);
            } else if (setType.equals("image/*")) {
                setupImage(fileUri);
            }
        }
    }

    private void resetVisibility() {
        videoView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.VISIBLE);

        startButton.setEnabled(false);
        pauseButton.setEnabled(false);

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    private void setupImage(Uri uri) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageURI(uri);

        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
    }

    private void setupAudio(Uri uri) {
        try {
            mPlayer = MediaPlayer.create(this, uri);

            mPlayer.setOnCompletionListener(mp -> {
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
            });
            mPlayer.start();

            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки аудио", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupVideo(Uri uri) {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);
        videoView.start();

        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    public void play(View view) {
        if (mPlayer != null) mPlayer.start();
        if (videoView.getVisibility() == View.VISIBLE) videoView.start();

        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    public void pause(View view) {
        if (mPlayer != null && mPlayer.isPlaying()) mPlayer.pause();
        if (videoView.getVisibility() == View.VISIBLE && videoView.isPlaying()) videoView.pause();

        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    private boolean checkPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_WRITE);
                return false;
            }
        }
        permissionGranted = true;
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                permissionGranted = true;
                Toast.makeText(this, "Разрешения получены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Разрешения отклонены", Toast.LENGTH_SHORT).show();
            }
        }
    }
}