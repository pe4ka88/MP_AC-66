package com.example.lab7mp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private Uri videoUri;

    private ActivityResultLauncher<String> videoPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);

        Button btnChoose = findViewById(R.id.btnChooseVideo);
        Button btnPlay = findViewById(R.id.btnPlayVideo);
        Button btnPause = findViewById(R.id.btnPauseVideo);
        Button btnStop = findViewById(R.id.btnStopVideo);

        videoPicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        videoUri = uri;
                        videoView.setVideoURI(videoUri);
                    }
                }
        );

        btnChoose.setOnClickListener(v -> videoPicker.launch("video/*"));
        btnPlay.setOnClickListener(v -> videoView.start());
        btnPause.setOnClickListener(v -> videoView.pause());
        btnStop.setOnClickListener(v -> {
            videoView.stopPlayback();
            if (videoUri != null) videoView.setVideoURI(videoUri);
        });
    }
}
