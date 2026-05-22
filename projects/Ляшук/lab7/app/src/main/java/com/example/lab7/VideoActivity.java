package com.example.lab7;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {
    private VideoView videoView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        dbHelper = new DatabaseHelper(this);
        dbHelper.addRecord("Открыта активность Видео (Ляшук В.И.)");

        videoView = findViewById(R.id.videoView);
        Button btnPlay = findViewById(R.id.btnPlayVideo);
        Button btnPause = findViewById(R.id.btnPauseVideo);
        Button btnBack = findViewById(R.id.btnBackVideo);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Инициализация видео. Файл должен быть в res/raw/video_sample.mp4
        int resId = getResources().getIdentifier("video_sample", "raw", getPackageName());
        if (resId != 0) {
            String videoPath = "android.resource://" + getPackageName() + "/" + resId;
            videoView.setVideoURI(Uri.parse(videoPath));
        } else {
            Toast.makeText(this, "Файл video_sample не найден в res/raw!", Toast.LENGTH_LONG).show();
        }

        btnPlay.setOnClickListener(v -> {
            videoView.start();
            Toast.makeText(this, "Ляшук: Видео запущено", Toast.LENGTH_SHORT).show();
            dbHelper.addRecord("Воспроизведение видео запущено (Ляшук В.И.)");
        });

        btnPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                dbHelper.addRecord("Видео на паузе");
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
