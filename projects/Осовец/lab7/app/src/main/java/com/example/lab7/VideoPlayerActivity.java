package com.example.lab7;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab7.data.HistoryLogger;
import com.example.lab7.ui.UiHelpers;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private SeekBar seekBar;
    private TextView statusText;
    private float currentScale = 1.0f;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (videoView != null && videoView.isPlaying()) {
                seekBar.setProgress(videoView.getCurrentPosition());
                progressHandler.postDelayed(this, 350);
            }
        }
    };

    private final ActivityResultLauncher<String> videoPicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::onVideoPicked
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        videoView = findViewById(R.id.videoView);
        seekBar = findViewById(R.id.seekVideo);
        statusText = findViewById(R.id.textVideoStatus);

        UiHelpers.bindBack(this);
        UiHelpers.bindAuthorButton(this);
        bindActions();
    }

    private void bindActions() {
        findViewById(R.id.btnPickVideo).setOnClickListener(v -> videoPicker.launch("video/*"));

        findViewById(R.id.btnPlayVideo).setOnClickListener(v -> {
            if (videoView.getDuration() <= 0) {
                toast("Сначала выберите видео");
                return;
            }
            videoView.start();
            statusText.setText("Статус: воспроизведение");
            HistoryLogger.log(this, "VIDEO", "PLAY");
            progressHandler.post(progressRunnable);
        });

        findViewById(R.id.btnPauseVideo).setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                statusText.setText("Статус: пауза");
                HistoryLogger.log(this, "VIDEO", "PAUSE");
            }
        });

        findViewById(R.id.btnVideoPrev).setOnClickListener(v -> seekBy(-10_000));
        findViewById(R.id.btnVideoNext).setOnClickListener(v -> seekBy(10_000));

        findViewById(R.id.btnVideoZoomIn).setOnClickListener(v -> updateScale(1.15f));
        findViewById(R.id.btnVideoZoomOut).setOnClickListener(v -> updateScale(0.87f));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && videoView.getDuration() > 0) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        videoView.setOnCompletionListener(mp -> {
            seekBar.setProgress(0);
            statusText.setText("Статус: завершено");
            HistoryLogger.log(this, "VIDEO", "COMPLETE");
        });
    }

    private void onVideoPicked(Uri uri) {
        if (uri == null) {
            return;
        }
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            seekBar.setMax(videoView.getDuration());
            seekBar.setProgress(0);
            statusText.setText("Статус: файл выбран");
            HistoryLogger.log(this, "VIDEO", "PICK");
        });
    }

    private void seekBy(int ms) {
        if (videoView.getDuration() <= 0) {
            return;
        }
        int next = videoView.getCurrentPosition() + ms;
        if (next < 0) {
            next = 0;
        }
        if (next > videoView.getDuration()) {
            next = videoView.getDuration();
        }
        videoView.seekTo(next);
        seekBar.setProgress(next);
    }

    private void updateScale(float factor) {
        currentScale *= factor;
        if (currentScale < 0.75f) {
            currentScale = 0.75f;
        }
        if (currentScale > 1.8f) {
            currentScale = 1.8f;
        }
        videoView.setScaleX(currentScale);
        videoView.setScaleY(currentScale);
        statusText.setText("Масштаб: " + String.format("%.2f", currentScale));
        HistoryLogger.log(this, "VIDEO", "ZOOM_" + currentScale);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressHandler.removeCallbacks(progressRunnable);
    }
}
