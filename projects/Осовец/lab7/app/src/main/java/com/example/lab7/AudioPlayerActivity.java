package com.example.lab7;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;

public class AudioPlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private SeekBar seekBar;
    private TextView statusText;
    private float currentSpeed = 1.0f;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (player != null) {
                long duration = player.getDuration();
                long position = player.getCurrentPosition();

                if (duration > 0) {
                    seekBar.setMax((int) duration);
                    seekBar.setProgress((int) Math.min(position, duration));
                }

                if (player.isPlaying()) {
                    progressHandler.postDelayed(this, 300);
                }
            }
        }
    };

    private final ActivityResultLauncher<String[]> audioPicker = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            this::onAudioPicked
    );

    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_READY && player != null) {
                long duration = player.getDuration();
                if (duration > 0) {
                    seekBar.setMax((int) duration);
                    seekBar.setProgress(0);
                }
                statusText.setText("Статус: файл выбран");
            } else if (playbackState == Player.STATE_ENDED) {
                seekBar.setProgress(0);
                statusText.setText("Статус: завершено");
                HistoryLogger.log(AudioPlayerActivity.this, "AUDIO", "COMPLETE");
            }
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            statusText.setText("Статус: ошибка воспроизведения");
            toast("Не удалось воспроизвести файл: " + PlaybackException.getErrorCodeName(error.errorCode));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_audio_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        seekBar = findViewById(R.id.seekAudio);
        statusText = findViewById(R.id.textAudioStatus);

        UiHelpers.bindBack(this);
        UiHelpers.bindAuthorButton(this);

        bindActions();
    }

    private void bindActions() {
        findViewById(R.id.btnPickAudio).setOnClickListener(v -> audioPicker.launch(new String[]{"audio/*"}));

        findViewById(R.id.btnPlayAudio).setOnClickListener(v -> {
            if (player == null) {
                toast("Сначала выберите файл");
                return;
            }
            if (player.getMediaItemCount() == 0) {
                toast("Сначала выберите аудиофайл");
                return;
            }

            if (isSystemVolumeMuted()) {
                toast("Громкость эмулятора/устройства = 0. Увеличьте MEDIA volume");
            }

            player.play();
            statusText.setText("Статус: воспроизведение");
            HistoryLogger.log(this, "AUDIO", "PLAY");
            progressHandler.post(progressRunnable);
        });

        findViewById(R.id.btnPauseAudio).setOnClickListener(v -> {
            if (player != null && player.isPlaying()) {
                player.pause();
                statusText.setText("Статус: пауза");
                HistoryLogger.log(this, "AUDIO", "PAUSE");
            }
        });

        findViewById(R.id.btnStopAudio).setOnClickListener(v -> {
            if (player != null) {
                player.pause();
                player.seekTo(0);
                seekBar.setProgress(0);
                statusText.setText("Статус: стоп");
                HistoryLogger.log(this, "AUDIO", "STOP");
            }
        });

        findViewById(R.id.btnAudioPrev).setOnClickListener(v -> skipBy(-10_000));
        findViewById(R.id.btnAudioNext).setOnClickListener(v -> skipBy(10_000));

        findViewById(R.id.btnAudioZoomIn).setOnClickListener(v -> setPlaybackSpeed(1.25f));
        findViewById(R.id.btnAudioZoomOut).setOnClickListener(v -> setPlaybackSpeed(0.85f));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && player != null) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void onAudioPicked(Uri uri) {
        if (uri == null) {
            return;
        }
        try {
            getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (SecurityException ignored) {
            // Some providers do not support persistable permissions.
        }

        if (!canOpenUri(uri)) {
            toast("Нет доступа к файлу или формат не поддерживается");
            statusText.setText("Статус: ошибка доступа к файлу");
            return;
        }

        ensurePlayer();
        if (player == null) {
            toast("Не удалось открыть файл");
            return;
        }

        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.pause();
        player.seekTo(0);
        player.setPlaybackParameters(new PlaybackParameters(currentSpeed));

        seekBar.setProgress(0);
        statusText.setText("Статус: загрузка файла...");
        HistoryLogger.log(this, "AUDIO", "PICK");
    }

    private void skipBy(int ms) {
        if (player == null) {
            return;
        }
        long duration = player.getDuration();
        long next = player.getCurrentPosition() + ms;
        if (next < 0) {
            next = 0;
        }
        if (duration > 0 && next > duration) {
            next = duration;
        }
        player.seekTo(next);
        seekBar.setProgress((int) next);
    }

    private void setPlaybackSpeed(float speed) {
        currentSpeed = speed;
        if (player == null) {
            return;
        }
        player.setPlaybackParameters(new PlaybackParameters(speed));
        statusText.setText("Скорость: " + speed + "x");
        HistoryLogger.log(this, "AUDIO", "SPEED_" + speed);
    }

    private boolean canOpenUri(Uri uri) {
        try (AssetFileDescriptor ignored = getContentResolver().openAssetFileDescriptor(uri, "r")) {
            return ignored != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void ensurePlayer() {
        releasePlayer();
        player = new ExoPlayer.Builder(this).build();
        player.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                true
        );
        player.setVolume(1.0f);
        player.addListener(playerListener);
    }

    private boolean isSystemVolumeMuted() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            return false;
        }
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0;
    }

    private void releasePlayer() {
        progressHandler.removeCallbacks(progressRunnable);
        if (player != null) {
            player.removeListener(playerListener);
            player.release();
            player = null;
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
