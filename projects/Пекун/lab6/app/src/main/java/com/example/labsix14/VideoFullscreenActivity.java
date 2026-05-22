package com.example.labsix14;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class VideoFullscreenActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_URI = "extra_video_uri";
    public static final String EXTRA_VIDEO_POSITION_MS = "extra_video_position_ms";

    private static final String KEY_VIDEO_POSITION = "key_video_position";

    private VideoView videoView;
    private int startPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_fullscreen);

        videoView = findViewById(R.id.videoFullscreen);
        MaterialButton closeButton = findViewById(R.id.buttonCloseVideo);
        closeButton.setOnClickListener(v -> finish());

        String uriString = getIntent().getStringExtra(EXTRA_VIDEO_URI);
        if (TextUtils.isEmpty(uriString)) {
            finish();
            return;
        }

        if (savedInstanceState != null) {
            startPosition = savedInstanceState.getInt(KEY_VIDEO_POSITION, 0);
        } else {
            startPosition = getIntent().getIntExtra(EXTRA_VIDEO_POSITION_MS, 0);
        }

        Uri uri = Uri.parse(uriString);
        android.widget.MediaController controller = new android.widget.MediaController(this);
        controller.setAnchorView(videoView);

        videoView.setMediaController(controller);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            if (startPosition > 0) {
                videoView.seekTo(startPosition);
            }
            videoView.start();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_VIDEO_POSITION, videoView.getCurrentPosition());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }
}
