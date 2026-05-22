package com.example.myapplication7;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MediaActivity extends AppCompatActivity
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private MediaPlayer   mediaPlayer;
    private CheckBox      chbLoop;
    private SeekBar       seekVolume;
    private TextView      tvStatus;
    private TextView      tvLocation;
    private EditText      etPath;
    private AudioManager  audioManager;

    private DatabaseHelper db;
    private LocationHelper locationHelper;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_media);

        db             = new DatabaseHelper(this);
        locationHelper = new LocationHelper(this);
        audioManager   = (AudioManager) getSystemService(AUDIO_SERVICE);

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            etPath.setText(uri.toString());
                            startPlayback(uri);
                        }
                    }
                });

        initViews();
        setupVolumeSeekBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMP();
    }

    // ──────────────────────── Init ────────────────────────

    private void initViews() {
        chbLoop    = findViewById(R.id.chb_loop);
        seekVolume = findViewById(R.id.seek_volume);
        tvStatus   = findViewById(R.id.tv_status);
        tvLocation = findViewById(R.id.tv_media_location);
        etPath     = findViewById(R.id.et_media_path);

        tvLocation.setText(locationHelper.getLocationString());

        chbLoop.setOnCheckedChangeListener((btn, checked) -> {
            if (mediaPlayer != null) mediaPlayer.setLooping(checked);
        });

        findViewById(R.id.btn_pick_file) .setOnClickListener(v -> openFilePicker());
        findViewById(R.id.btn_play)      .setOnClickListener(v -> onClickPlay());
        findViewById(R.id.btn_pause)     .setOnClickListener(v -> onClickPause());
        findViewById(R.id.btn_resume)    .setOnClickListener(v -> onClickResume());
        findViewById(R.id.btn_stop)      .setOnClickListener(v -> onClickStop());
        findViewById(R.id.btn_media_back).setOnClickListener(v -> finish());
    }

    private void setupVolumeSeekBar() {
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekVolume.setMax(maxVol);
        seekVolume.setProgress(curVol);
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int p, boolean u) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, p, 0);
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });
    }

    // ──────────────────────── File picker ────────────────────────

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"video/*", "audio/*"});
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "Выберите файл"));
    }

    // ──────────────────────── Playback ────────────────────────

    private void onClickPlay() {
        String path = etPath.getText().toString().trim();
        if (path.isEmpty()) {
            showMessage("Укажите путь или выберите файл 📁");
            return;
        }
        // content:// URI (из picker-а или поля ввода)
        if (path.startsWith("content://")) {
            startPlayback(Uri.parse(path));
        } else {
            // http://, https://, file:// или голый путь вроде /sdcard/...
            String uriStr = path.startsWith("/") ? "file://" + path : path;
            startPlayback(Uri.parse(uriStr));
        }
    }

    private void startPlayback(Uri uri) {
        releaseMP();
        setStatus("Подготовка...");
        try {
            mediaPlayer = new MediaPlayer();

            // ✅ AudioAttributes вместо устаревшего setAudioStreamType()
            //    и ОБЯЗАТЕЛЬНО до setDataSource()
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build();
            mediaPlayer.setAudioAttributes(attrs);

            // ✅ setDataSource() — после setAudioAttributes()
            mediaPlayer.setDataSource(getApplicationContext(), uri);

            // ✅ SurfaceView — после setDataSource()
            SurfaceView sv = findViewById(R.id.surface_media);
            mediaPlayer.setDisplay(sv.getHolder());

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setLooping(chbLoop.isChecked());
            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            setStatus("Ошибка");
            showMessage("Ошибка: " + e.getMessage());
        }
    }

    private void onClickPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            setStatus("Пауза");
        }
    }

    private void onClickResume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            setStatus("Воспроизведение");
        }
    }

    private void onClickStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            setStatus("Остановлено");
        }
    }

    // ──────────────────────── Callbacks ────────────────────────

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        setStatus("Воспроизведение ▶");
        db.addRecord("Медиа",
                etPath.getText().toString(),
                locationHelper.getLatitude(),
                locationHelper.getLongitude());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setStatus("Завершено ✓");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setStatus("Ошибка плеера (" + what + "/" + extra + ")");
        showMessage("Не удалось воспроизвести файл");
        releaseMP();
        return true;
    }

    // ──────────────────────── Util ────────────────────────

    private void releaseMP() {
        if (mediaPlayer != null) {
            try { mediaPlayer.release(); } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }

    private void setStatus(String text) {
        runOnUiThread(() -> tvStatus.setText(text));
    }

    // ✅ Простой Toast без setGravity() — он не работает на Android 11+
    private void showMessage(String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }
}