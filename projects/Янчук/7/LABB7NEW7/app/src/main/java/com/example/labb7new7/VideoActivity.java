package com.example.labb7new7;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO = 2;

    private VideoView videoView;
    private Uri currentVideoUri;

    private LinearLayout videoControls;
    private TextView txtVideoName;
    private SeekBar videoSeekBar;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        new HistoryManager(this).addAction("Открыто видео");

        Button btnSelectVideo = findViewById(R.id.btnSelectVideo);
        Button btnPlay = findViewById(R.id.btnPlayVideo);
        Button btnPause = findViewById(R.id.btnPauseVideo);
        Button btnStop = findViewById(R.id.btnStopVideo);
        Button btnBack10 = findViewById(R.id.btnBack10);
        Button btnForward10 = findViewById(R.id.btnForward10);

        txtVideoName = findViewById(R.id.txtVideoName);
        videoView = findViewById(R.id.videoView);
        videoControls = findViewById(R.id.videoControls);
        videoSeekBar = findViewById(R.id.videoSeekBar);

        btnSelectVideo.setOnClickListener(v -> selectVideoFile());

        btnPlay.setOnClickListener(v -> videoView.start());
        btnPause.setOnClickListener(v -> videoView.pause());
        btnStop.setOnClickListener(v -> stopVideo());

        btnBack10.setOnClickListener(v -> videoView.seekTo(videoView.getCurrentPosition() - 10000));
        btnForward10.setOnClickListener(v -> videoView.seekTo(videoView.getCurrentPosition() + 10000));

        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) videoView.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        videoView.setOnClickListener(v -> toggleControls());
    }

    private void selectVideoFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO && resultCode == Activity.RESULT_OK) {
            currentVideoUri = data.getData();

            if (currentVideoUri != null) {
                txtVideoName.setText("Выбран файл: " + currentVideoUri.getLastPathSegment());
                videoControls.setVisibility(View.VISIBLE);

                videoView.setVideoURI(currentVideoUri);
                videoView.setOnPreparedListener(mp -> {
                    videoSeekBar.setMax(videoView.getDuration());
                    updateSeekBar();
                });
            }
        }
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoView != null && videoView.isPlaying()) {
                    videoSeekBar.setProgress(videoView.getCurrentPosition());
                }
                updateSeekBar();
            }
        }, 500);
    }

    private void stopVideo() {
        videoView.stopPlayback();
        videoView.setVideoURI(currentVideoUri);
    }

    private void toggleControls() {
        if (videoControls.getVisibility() == View.VISIBLE) {
            videoControls.setVisibility(View.GONE);
        } else {
            videoControls.setVisibility(View.VISIBLE);
        }
    }
}
