package com.example.labseven17.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.labseven17.R;
import com.example.labseven17.databinding.ActivityAudioPlayerBinding;

import java.io.IOException;
import java.util.Locale;

public class AudioPlayerActivity extends AppCompatActivity {

    private static final int SKIP_MS = 10_000;

    private ActivityAudioPlayerBinding binding;
    private MediaPlayer mediaPlayer;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private Uri audioUri;
    private boolean isUserSeeking = false;
    private float selectedSpeed = 1.0f;

    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                int current = mediaPlayer.getCurrentPosition();
                if (!isUserSeeking) {
                    binding.seekAudio.setProgress(current);
                }
                binding.tvCurrentAudioTime.setText(formatTime(current));
                progressHandler.postDelayed(this, 250);
            }
        }
    };

    private final ActivityResultLauncher<String[]> pickAudioLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) {
                    return;
                }

                audioUri = uri;
                try {
                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                } catch (SecurityException ignored) {
                    // Some providers do not grant persistable permission.
                }
                preparePlayer();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAudioPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnPickAudio.setOnClickListener(v -> pickAudioLauncher.launch(new String[]{"audio/*"}));
        binding.btnPlayPause.setOnClickListener(v -> togglePlayback());
        binding.btnRewind.setOnClickListener(v -> seekBy(-SKIP_MS));
        binding.btnForward.setOnClickListener(v -> seekBy(SKIP_MS));

        setupSpeedSelector();
        setupSeekBar();
    }

    private void setupSpeedSelector() {
        String[] speeds = getResources().getStringArray(R.array.speed_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, speeds);
        binding.spinnerSpeed.setAdapter(adapter);
        binding.spinnerSpeed.setSelection(1);
        binding.spinnerSpeed.setOnItemSelectedListener(new SimpleItemSelectedListener(position -> {
            selectedSpeed = parseSpeed(speeds[position]);
            applySpeed();
        }));
    }

    private void setupSeekBar() {
        binding.seekAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.tvCurrentAudioTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
                isUserSeeking = false;
            }
        });
    }

    private void preparePlayer() {
        releasePlayer();
        if (audioUri == null) {
            return;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, audioUri);
            mediaPlayer.setOnPreparedListener(mp -> {
                binding.seekAudio.setMax(mp.getDuration());
                binding.tvAudioDuration.setText(formatTime(mp.getDuration()));
                binding.tvCurrentAudioTime.setText(formatTime(0));
                applySpeed();
                binding.btnPlayPause.setEnabled(true);
                binding.btnRewind.setEnabled(true);
                binding.btnForward.setEnabled(true);
                Toast.makeText(this, R.string.audio_loaded, Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                binding.btnPlayPause.setText(R.string.play);
                binding.seekAudio.setProgress(0);
                binding.tvCurrentAudioTime.setText(formatTime(0));
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Toast.makeText(this, R.string.audio_open_error, Toast.LENGTH_LONG).show();
            releasePlayer();
        }
    }

    private void togglePlayback() {
        if (mediaPlayer == null) {
            Toast.makeText(this, R.string.pick_audio_first, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            binding.btnPlayPause.setText(R.string.play);
        } else {
            mediaPlayer.start();
            binding.btnPlayPause.setText(R.string.pause);
            progressHandler.removeCallbacks(updateProgressTask);
            progressHandler.post(updateProgressTask);
        }
    }

    private void seekBy(int shiftMs) {
        if (mediaPlayer == null) {
            return;
        }
        int newPosition = mediaPlayer.getCurrentPosition() + shiftMs;
        newPosition = Math.max(0, Math.min(newPosition, mediaPlayer.getDuration()));
        mediaPlayer.seekTo(newPosition);
    }

    private void applySpeed() {
        if (mediaPlayer == null) {
            return;
        }
        try {
            PlaybackParams params = mediaPlayer.getPlaybackParams();
            params.setSpeed(selectedSpeed);
            mediaPlayer.setPlaybackParams(params);
        } catch (Exception ignored) {
            Toast.makeText(this, R.string.speed_not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    private float parseSpeed(String text) {
        String normalized = text.replace("x", "").trim();
        try {
            return Float.parseFloat(normalized);
        } catch (NumberFormatException e) {
            return 1.0f;
        }
    }

    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            binding.btnPlayPause.setText(R.string.play);
        }
    }

    @Override
    protected void onDestroy() {
        progressHandler.removeCallbacks(updateProgressTask);
        releasePlayer();
        super.onDestroy();
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException ignored) {
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
