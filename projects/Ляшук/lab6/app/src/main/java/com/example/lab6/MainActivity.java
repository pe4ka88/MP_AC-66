package com.example.lab6;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btnPickFile, btnPlayAudio, btnStopAudio, btnShowTask;
    private TextView tvFileName, tvTask;
    private ImageView imageView;
    private VideoView videoView;
    private LinearLayout audioLayout;

    private MediaPlayer mediaPlayer;
    private Uri currentFileUri;

    // Лаунчер для выбора файла через системный проводник
    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    currentFileUri = uri;
                    handleFile(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Настройка отступов для Edge-to-Edge (системные бары)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        checkPermissions();

        // Кнопка выбора любого файла
        btnPickFile.setOnClickListener(v -> filePickerLauncher.launch("*/*"));

        // Кнопка показа/скрытия задания
        btnShowTask.setOnClickListener(v -> {
            if (tvTask.getVisibility() == View.GONE) {
                tvTask.setVisibility(View.VISIBLE);
            } else {
                tvTask.setVisibility(View.GONE);
            }
        });

        // Кнопки управления аудио
        btnPlayAudio.setOnClickListener(v -> playAudio());
        btnStopAudio.setOnClickListener(v -> stopAudio());
    }

    private void initViews() {
        btnPickFile = findViewById(R.id.btnPickFile);
        btnShowTask = findViewById(R.id.btnShowTask);
        tvTask = findViewById(R.id.tvTask);
        tvFileName = findViewById(R.id.tvFileName);
        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);
        audioLayout = findViewById(R.id.audioLayout);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        btnStopAudio = findViewById(R.id.btnStopAudio);

        // Добавляем стандартные элементы управления для видео (Play/Pause/Seek)
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
    }

    private void handleFile(Uri uri) {
        resetViews();
        String mimeType = getContentResolver().getType(uri);
        tvFileName.setText("Выбран: " + uri.getLastPathSegment());

        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                showImage(uri);
            } else if (mimeType.startsWith("video/")) {
                showVideo(uri);
            } else if (mimeType.startsWith("audio/")) {
                showAudio(uri);
            } else {
                Toast.makeText(this, "Тип файла не поддерживается: " + mimeType, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Не удалось определить тип файла", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetViews() {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        audioLayout.setVisibility(View.GONE);
        
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        stopAudio();
    }

    private void showImage(Uri uri) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageURI(uri);
    }

    private void showVideo(Uri uri) {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    private void showAudio(Uri uri) {
        audioLayout.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Аудиофайл готов к воспроизведению", Toast.LENGTH_SHORT).show();
    }

    private void playAudio() {
        if (currentFileUri == null) return;
        
        stopAudio();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, currentFileUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Воспроизведение...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void checkPermissions() {
        // Начиная с Android 13 (Tiramisu), нужны специфичные разрешения
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
            if (!hasPermissions(permissions)) {
                ActivityCompat.requestPermissions(this, permissions, 100);
            }
        } else {
            // Для старых версий достаточно общего разрешения
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAudio();
    }
}