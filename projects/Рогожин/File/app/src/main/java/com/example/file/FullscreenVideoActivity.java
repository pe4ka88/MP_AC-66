package com.example.file;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.ui.PlayerView;

public class FullscreenVideoActivity extends AppCompatActivity {

    public static final String EXTRA_URI = "extra_uri";
    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_SPEED = "extra_speed";
    public static final String EXTRA_VOLUME = "extra_volume";
    private PlayerView fullscreenPlayerView;
    private ExoPlayer player;
    private long startPosition = 0;
    private Uri videoUri;
    private float startSpeed = 1f;
    private float startVolume = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video);

        // Fullscreen флаги
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fullscreenPlayerView = findViewById(R.id.fullscreenPlayerView);

        videoUri = getIntent().getParcelableExtra(EXTRA_URI);
        startPosition = getIntent().getLongExtra(EXTRA_POSITION, 0);

        // Инициализация ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        fullscreenPlayerView.setPlayer(player);
        videoUri = getIntent().getParcelableExtra(EXTRA_URI);
        startPosition = getIntent().getLongExtra(EXTRA_POSITION, 0);
        startSpeed = getIntent().getFloatExtra(EXTRA_SPEED, 1f);
        startVolume = getIntent().getFloatExtra(EXTRA_VOLUME, 1f);
        MediaItem item = MediaItem.fromUri(videoUri);
        player.setMediaItem(item);
        player.prepare();
        player.seekTo(startPosition);
        player.setPlaybackParameters(new PlaybackParameters(startSpeed));
        player.setVolume(startVolume);
        player.play();
        // ===== Новый способ обработки Back =====
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Передаем позицию обратно
                Intent intent = new Intent();
                intent.putExtra(EXTRA_POSITION, player.getCurrentPosition());
                setResult(RESULT_OK, intent);

                // Анимация выхода
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        fullscreenPlayerView.findViewById(R.id.btnFullscreen).setOnClickListener(v -> {
            exitFullscreen();
        });
        // Настраиваем кастомный контроллер
        setupController();
    }

    private void setupController() {
        // Play/Pause
        fullscreenPlayerView.findViewById(R.id.exo_play_pause).setOnClickListener(v -> {
            if (player.isPlaying()) player.pause();
            else player.play();
        });

        // Rewind 10 сек
        fullscreenPlayerView.findViewById(R.id.btnRewind).setOnClickListener(v ->
                player.seekTo(Math.max(player.getCurrentPosition() - 10000, 0)));

        // Forward 10 сек
        fullscreenPlayerView.findViewById(R.id.btnForward).setOnClickListener(v ->
                player.seekTo(player.getCurrentPosition() + 10000));

        // Скорость
        Spinner spinnerSpeed = fullscreenPlayerView.findViewById(R.id.spinnerSpeed);
        if (spinnerSpeed != null) {
            spinnerSpeed.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                    String speedText = parent.getItemAtPosition(position).toString().replace("x", "").trim();
                    try {
                        float speed = Float.parseFloat(speedText);
                        player.setPlaybackParameters(new com.google.android.exoplayer2.PlaybackParameters(speed));
                    } catch (Exception ignored) {}
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
        }

        // Volume
        SeekBar seekVolume = fullscreenPlayerView.findViewById(R.id.seekVolume);
        if (seekVolume != null) {
            int max = 100;
            seekVolume.setMax(max);
            seekVolume.setProgress(max / 2);
            seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float volume = progress / (float) max;
                    player.setVolume(volume);
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) startPosition = player.getCurrentPosition();
        if (player != null) player.pause();
    }
    private void exitFullscreen() {
        if (player != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_POSITION, player.getCurrentPosition());
            setResult(RESULT_OK, intent);
        }

        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}