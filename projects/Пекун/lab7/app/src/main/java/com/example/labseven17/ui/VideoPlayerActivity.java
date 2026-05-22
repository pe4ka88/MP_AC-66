package com.example.labseven17.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.labseven17.R;
import com.example.labseven17.databinding.ActivityVideoPlayerBinding;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final int VIDEO_SKIP_MS = 10_000;

    private ActivityVideoPlayerBinding binding;
    private Uri videoUri;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private MediaPlayer preparedMediaPlayer;
    private float selectedSpeed = 1.0f;
    private boolean isFullscreen = false;
    private boolean isUserSeeking = false;

    private final Runnable updateSeekRunnable = new Runnable() {
        @Override
        public void run() {
            VideoView videoView = binding.videoView;
            if (videoView.isPlaying() && !isUserSeeking) {
                binding.seekVideo.setProgress(videoView.getCurrentPosition());
            }
            progressHandler.postDelayed(this, 300);
        }
    };

    private final ActivityResultLauncher<String[]> pickVideoLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) {
                    return;
                }
                videoUri = uri;
                try {
                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                } catch (SecurityException ignored) {
                    // Some providers do not grant persistable permission.
                }
                prepareVideo();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnPickVideo.setOnClickListener(v -> pickVideoLauncher.launch(new String[]{"video/*"}));
        binding.btnPlayPauseVideo.setOnClickListener(v -> toggleVideo());
        binding.btnVideoRewind.setOnClickListener(v -> seekBy(-VIDEO_SKIP_MS));
        binding.btnVideoForward.setOnClickListener(v -> seekBy(VIDEO_SKIP_MS));
        binding.btnFullscreen.setOnClickListener(v -> toggleFullscreen());

        setupSpeedSelector();
        setupQualitySelector();
        setupSeekBar();
    }

    private void setupSpeedSelector() {
        String[] speeds = getResources().getStringArray(R.array.speed_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, speeds);
        binding.spinnerVideoSpeed.setAdapter(adapter);
        binding.spinnerVideoSpeed.setSelection(1);
        binding.spinnerVideoSpeed.setOnItemSelectedListener(new SimpleItemSelectedListener(position -> {
            selectedSpeed = parseSpeed(speeds[position]);
            applyVideoSpeed();
        }));
    }

    private void setupQualitySelector() {
        String[] qualities = getResources().getStringArray(R.array.video_quality_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, qualities);
        binding.spinnerQuality.setAdapter(adapter);
        binding.spinnerQuality.setSelection(1);
        binding.spinnerQuality.setOnItemSelectedListener(new SimpleItemSelectedListener(position -> {
            String value = qualities[position];
            binding.tvSelectedQuality.setText(getString(R.string.current_quality, value));
            Toast.makeText(this, getString(R.string.quality_emulation, value), Toast.LENGTH_SHORT).show();
        }));
    }

    private void setupSeekBar() {
        binding.seekVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.tvVideoCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binding.videoView.seekTo(seekBar.getProgress());
                isUserSeeking = false;
            }
        });
    }

    private void prepareVideo() {
        if (videoUri == null) {
            return;
        }

        binding.videoView.setVideoURI(videoUri);
        binding.videoView.setOnPreparedListener(mp -> {
            preparedMediaPlayer = mp;
            binding.seekVideo.setMax(mp.getDuration());
            binding.tvVideoDuration.setText(formatTime(mp.getDuration()));
            binding.tvVideoCurrentTime.setText(formatTime(0));
            applyVideoSpeed();
            Toast.makeText(this, R.string.video_loaded, Toast.LENGTH_SHORT).show();
        });

        binding.videoView.setOnCompletionListener(mp -> {
            binding.btnPlayPauseVideo.setText(R.string.play);
            binding.seekVideo.setProgress(0);
            binding.tvVideoCurrentTime.setText(formatTime(0));
        });

        binding.videoView.requestFocus();
    }

    private void toggleVideo() {
        if (videoUri == null) {
            Toast.makeText(this, R.string.pick_video_first, Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
            binding.btnPlayPauseVideo.setText(R.string.play);
        } else {
            binding.videoView.start();
            applyVideoSpeed();
            binding.btnPlayPauseVideo.setText(R.string.pause);
            progressHandler.removeCallbacks(updateSeekRunnable);
            progressHandler.post(updateSeekRunnable);
        }
    }

    private void seekBy(int amount) {
        if (videoUri == null) {
            return;
        }
        int duration = binding.videoView.getDuration();
        if (duration <= 0) {
            return;
        }
        int target = Math.max(0, Math.min(binding.videoView.getCurrentPosition() + amount, duration));
        binding.videoView.seekTo(target);
    }

    private void applyVideoSpeed() {
        if (preparedMediaPlayer == null) {
            return;
        }
        try {
            PlaybackParams params = preparedMediaPlayer.getPlaybackParams();
            params.setSpeed(selectedSpeed);
            preparedMediaPlayer.setPlaybackParams(params);
        } catch (Exception ignored) {
            Toast.makeText(this, R.string.speed_not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    private float parseSpeed(String text) {
        try {
            return Float.parseFloat(text.replace("x", "").trim());
        } catch (NumberFormatException e) {
            return 1.0f;
        }
    }

    private String formatTime(int millis) {
        int total = millis / 1000;
        int min = total / 60;
        int sec = total % 60;
        return String.format(java.util.Locale.getDefault(), "%02d:%02d", min, sec);
    }

    private void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        setRequestedOrientation(isFullscreen
                ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        if (isFullscreen) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            binding.btnFullscreen.setText(R.string.exit_fullscreen);
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars());
            binding.btnFullscreen.setText(R.string.fullscreen);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
            binding.btnPlayPauseVideo.setText(R.string.play);
        }
    }

    @Override
    protected void onDestroy() {
        progressHandler.removeCallbacks(updateSeekRunnable);
        preparedMediaPlayer = null;
        super.onDestroy();
    }
}
