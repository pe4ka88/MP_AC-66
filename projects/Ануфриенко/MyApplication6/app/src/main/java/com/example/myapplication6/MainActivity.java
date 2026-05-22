package com.example.myapplication6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements TextureView.SurfaceTextureListener {

    private static final int REQUEST_PERMISSION = 1001;
    private static final int PICKFILE_CODE      = 1;

    private boolean permissionGranted = false;
    private String  setType = "";

    // ── UI ───────────────────────────────────────────────
    private Button       startButton, pauseButton;
    private VideoView    videoView;
    private TextureView  textureView;
    private LinearLayout layoutAudioControls, layoutVideoControls,
            layoutTextureControls, layoutHint;
    private TextView     tvFileName, tvAudioStatus, tvVideoMode,
            tvVideoPosition, tvVideoDuration;
    private Button       btnSwitchImpl, btnVideoPlay, btnVideoPause;
    private SeekBar      seekBarVideo;

    // ── Аудио ────────────────────────────────────────────
    private MediaPlayer  mPlayer;

    // ── Видео: VideoView ──────────────────────────────────
    private MediaController mediaController;

    // ── Видео: TextureView + MediaPlayer ─────────────────
    private MediaPlayer  videoPlayer;
    private Surface      videoSurface;
    private Uri          lastVideoUri;

    // Обработчик для обновления SeekBar
    private final Handler seekHandler = new Handler(Looper.getMainLooper());
    private final Runnable seekUpdater = new Runnable() {
        @Override
        public void run() {
            if (videoPlayer != null) {
                try {
                    int pos = videoPlayer.getCurrentPosition();
                    int dur = videoPlayer.getDuration();
                    if (dur > 0) {
                        seekBarVideo.setMax(dur);
                        seekBarVideo.setProgress(pos);
                        tvVideoPosition.setText(formatTime(pos));
                        tvVideoDuration.setText(formatTime(dur));
                    }
                } catch (IllegalStateException ignored) {}
            }
            seekHandler.postDelayed(this, 500);
        }
    };

    // false = VideoView,  true = TextureView
    private boolean useTextureView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Отступ под вырез/статус-бар программно (поверх XML paddingTop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            View root = findViewById(android.R.id.content);
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                int topInset = insets.getInsets(WindowInsets.Type.statusBars()).top;
                v.setPadding(
                        v.getPaddingLeft(),
                        topInset + 8,
                        v.getPaddingRight(),
                        v.getPaddingBottom());
                return insets;
            });
        }

        startButton          = findViewById(R.id.start);
        pauseButton          = findViewById(R.id.pause);
        videoView            = findViewById(R.id.videoView);
        textureView          = findViewById(R.id.textureView);
        layoutAudioControls  = findViewById(R.id.layoutAudioControls);
        layoutVideoControls  = findViewById(R.id.layoutVideoControls);
        layoutTextureControls= findViewById(R.id.layoutTextureControls);
        layoutHint           = findViewById(R.id.layoutHint);
        tvFileName           = findViewById(R.id.tvFileName);
        tvAudioStatus        = findViewById(R.id.tvAudioStatus);
        tvVideoMode          = findViewById(R.id.tvVideoMode);
        tvVideoPosition      = findViewById(R.id.tvVideoPosition);
        tvVideoDuration      = findViewById(R.id.tvVideoDuration);
        btnSwitchImpl        = findViewById(R.id.btnSwitchImpl);
        btnVideoPlay         = findViewById(R.id.btnVideoPlay);
        btnVideoPause        = findViewById(R.id.btnVideoPause);
        seekBarVideo         = findViewById(R.id.seekBarVideo);

        // TextureView
        textureView.setSurfaceTextureListener(this);

        // VideoView MediaController
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // SeekBar — перемотка вручную
        seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser && videoPlayer != null) {
                    try { videoPlayer.seekTo(progress); } catch (IllegalStateException ignored) {}
                }
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        updateVideoModeLabel();

        if (!permissionGranted) checkPermissions();
    }

    // ─────────────────────────────────────────────────────
    //  Переключение реализации видео
    // ─────────────────────────────────────────────────────
    public void onSwitchImpl(View v) {
        stopVideoPlayback();
        useTextureView = !useTextureView;
        updateVideoModeLabel();
        if (lastVideoUri != null && "video/*".equals(setType)) {
            playVideo(lastVideoUri);
        }
    }

    private void updateVideoModeLabel() {
        if (useTextureView) {
            tvVideoMode.setText("Реализация: TextureView + MediaPlayer");
            btnSwitchImpl.setText("Переключить на VideoView");
        } else {
            tvVideoMode.setText("Реализация: VideoView");
            btnSwitchImpl.setText("Переключить на TextureView");
        }
    }

    // ─────────────────────────────────────────────────────
    //  Выбор файла
    // ─────────────────────────────────────────────────────
    public void onClFile(View viewButton) {
        if (!permissionGranted) { checkPermissions(); return; }

        releaseMediaPlayer();
        stopVideoPlayback();
        hideAllMedia();

        int id = viewButton.getId();
        if      (id == R.id.buttonImage) setType = "image/*";
        else if (id == R.id.buttonAudio) setType = "audio/*";
        else if (id == R.id.buttonVideo) setType = "video/*";

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(setType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,
                    Uri.parse("content://com.android.providers.downloads.documents/root/downloads"));
        }

        startActivityForResult(intent, PICKFILE_CODE);
    }

    // ─────────────────────────────────────────────────────
    //  Результат выбора файла
    // ─────────────────────────────────────────────────────
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICKFILE_CODE || resultCode != RESULT_OK || data == null) return;

        Uri fileUri = data.getData();
        if (fileUri == null) return;

        try {
            getContentResolver().takePersistableUriPermission(
                    fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {}

        tvFileName.setText(getFileNameFromUri(fileUri));
        layoutHint.setVisibility(View.GONE);

        if      ("audio/*".equals(setType)) playAudio(fileUri);
        else if ("video/*".equals(setType)) { lastVideoUri = fileUri; playVideo(fileUri); }
        else if ("image/*".equals(setType)) {
            Intent imageIntent = new Intent(this, ImageActivity.class);
            imageIntent.putExtra("imageUri", fileUri.toString());
            imageIntent.putExtra("imageName", getFileNameFromUri(fileUri));
            imageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(imageIntent);
        }
    }

    // ─────────────────────────────────────────────────────
    //  Аудио
    // ─────────────────────────────────────────────────────
    private void playAudio(Uri fileUri) {
        layoutAudioControls.setVisibility(View.VISIBLE);
        layoutVideoControls.setVisibility(View.GONE);
        layoutTextureControls.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        textureView.setVisibility(View.GONE);

        mPlayer = MediaPlayer.create(this, fileUri);
        if (mPlayer == null) {
            Toast.makeText(this, "Не удалось загрузить аудиофайл", Toast.LENGTH_SHORT).show();
            return;
        }
        mPlayer.start();
        tvAudioStatus.setText("Воспроизводится...");
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        mPlayer.setOnCompletionListener(mp -> {
            tvAudioStatus.setText("Готов к воспроизведению");
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
        });
    }

    public void play(View view) {
        if (mPlayer != null) {
            mPlayer.start();
            tvAudioStatus.setText("Воспроизводится...");
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
        }
    }

    public void pause(View view) {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            tvAudioStatus.setText("Пауза");
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
        }
    }

    // ─────────────────────────────────────────────────────
    //  Видео — выбор реализации
    // ─────────────────────────────────────────────────────
    private void playVideo(Uri fileUri) {
        layoutAudioControls.setVisibility(View.GONE);
        layoutVideoControls.setVisibility(View.VISIBLE);

        if (useTextureView) {
            playVideoTexture(fileUri);
        } else {
            playVideoView(fileUri);
        }
    }

    // ── Реализация 1: VideoView ───────────────────────────
    private void playVideoView(Uri fileUri) {
        textureView.setVisibility(View.GONE);
        layoutTextureControls.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        videoView.stopPlayback();

        videoView.setOnPreparedListener(mp -> {
            mp.setVolume(1f, 1f);
            videoView.start();
        });
        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "VideoView ошибка: " + what + "/" + extra,
                    Toast.LENGTH_LONG).show();
            return true;
        });

        videoView.setVideoURI(fileUri);
        videoView.requestFocus();
    }

    // ── Реализация 2: TextureView + MediaPlayer ───────────
    private void playVideoTexture(Uri fileUri) {
        videoView.setVisibility(View.GONE);
        textureView.setVisibility(View.VISIBLE);
        layoutTextureControls.setVisibility(View.VISIBLE);

        if (videoSurface != null) {
            startTexturePlayer(fileUri);
        }
        // иначе ждём onSurfaceTextureAvailable
    }

    private void startTexturePlayer(Uri uri) {
        stopVideoPlayback();

        videoPlayer = new MediaPlayer();
        try {
            videoPlayer.setDataSource(this, uri);
            videoPlayer.setSurface(videoSurface);
            videoPlayer.setVolume(1f, 1f);

            videoPlayer.setOnPreparedListener(mp -> {
                int dur = mp.getDuration();
                seekBarVideo.setMax(dur);
                tvVideoDuration.setText(formatTime(dur));
                btnVideoPlay.setEnabled(false);
                btnVideoPause.setEnabled(true);
                mp.start();
                seekHandler.post(seekUpdater); // запускаем обновление SeekBar
            });

            videoPlayer.setOnCompletionListener(mp -> {
                seekHandler.removeCallbacks(seekUpdater);
                seekBarVideo.setProgress(0);
                tvVideoPosition.setText("0:00");
                btnVideoPlay.setEnabled(true);
                btnVideoPause.setEnabled(false);
                Toast.makeText(this, "Воспроизведение завершено", Toast.LENGTH_SHORT).show();
            });

            videoPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "TextureView ошибка: " + what + "/" + extra,
                        Toast.LENGTH_LONG).show();
                return true;
            });

            videoPlayer.prepareAsync();

        } catch (IOException e) {
            Toast.makeText(this, "Ошибка загрузки: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Кнопки управления TextureView-плеером
    public void onVideoPlay(View v) {
        if (videoPlayer != null) {
            videoPlayer.start();
            btnVideoPlay.setEnabled(false);
            btnVideoPause.setEnabled(true);
            seekHandler.post(seekUpdater);
        }
    }

    public void onVideoPause(View v) {
        if (videoPlayer != null && videoPlayer.isPlaying()) {
            videoPlayer.pause();
            btnVideoPlay.setEnabled(true);
            btnVideoPause.setEnabled(false);
            seekHandler.removeCallbacks(seekUpdater);
        }
    }

    private void stopVideoPlayback() {
        seekHandler.removeCallbacks(seekUpdater);
        if (videoPlayer != null) {
            try { if (videoPlayer.isPlaying()) videoPlayer.stop(); } catch (Exception ignored) {}
            videoPlayer.release();
            videoPlayer = null;
        }
        if (videoView != null) {
            try { videoView.stopPlayback(); } catch (Exception ignored) {}
        }
    }

    // ── TextureView.SurfaceTextureListener ────────────────
    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture st, int w, int h) {
        videoSurface = new Surface(st);
        if (useTextureView && lastVideoUri != null && "video/*".equals(setType)) {
            startTexturePlayer(lastVideoUri);
        }
    }

    @Override public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture st, int w, int h) {}

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture st) {
        videoSurface = null;
        return true;
    }

    @Override public void onSurfaceTextureUpdated(@NonNull SurfaceTexture st) {}

    // ─────────────────────────────────────────────────────
    //  Разрешения
    // ─────────────────────────────────────────────────────
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean ok =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)  == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)  == PackageManager.PERMISSION_GRANTED;
            if (!ok) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_VIDEO
                }, REQUEST_PERMISSION);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
                return false;
            }
        }
        permissionGranted = true;
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            boolean all = grantResults.length > 0;
            for (int r : grantResults) if (r != PackageManager.PERMISSION_GRANTED) { all = false; break; }
            permissionGranted = all;
            Toast.makeText(this, all ? "Разрешения получены" : "Необходимо дать разрешения",
                    Toast.LENGTH_LONG).show();
        }
    }

    // ─────────────────────────────────────────────────────
    //  Вспомогательные
    // ─────────────────────────────────────────────────────
    private void hideAllMedia() {
        videoView.setVisibility(View.GONE);
        textureView.setVisibility(View.GONE);
        layoutAudioControls.setVisibility(View.GONE);
        layoutVideoControls.setVisibility(View.GONE);
        layoutTextureControls.setVisibility(View.GONE);
    }

    private void releaseMediaPlayer() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    /** Форматирует миллисекунды в М:СС */
    private String formatTime(int ms) {
        int totalSec = ms / 1000;
        int min = totalSec / 60;
        int sec = totalSec % 60;
        return String.format("%d:%02d", min, sec);
    }

    private String getFileNameFromUri(Uri uri) {
        if (uri == null) return "неизвестный файл";
        String path = uri.getLastPathSegment();
        if (path != null && path.contains("/"))
            return path.substring(path.lastIndexOf("/") + 1);
        return path != null ? path : uri.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        stopVideoPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            tvAudioStatus.setText("Пауза");
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
        }
        if (videoView != null && videoView.isPlaying()) videoView.pause();
        if (videoPlayer != null && videoPlayer.isPlaying()) {
            videoPlayer.pause();
            seekHandler.removeCallbacks(seekUpdater);
            btnVideoPlay.setEnabled(true);
            btnVideoPause.setEnabled(false);
        }
    }
}