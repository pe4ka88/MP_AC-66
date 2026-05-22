package com.example.lab6;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lab6.R;

public class MainActivity extends AppCompatActivity {

    // UI элементы
    private ImageView imageView;
    private VideoView videoView;
    private LinearLayout audioPanel;
    private Button btnPlay, btnPause, btnStop;
    private TextView tvFileName, tvStatus, tvAudioFile;

    // MediaPlayer для аудио
    private MediaPlayer mediaPlayer;

    // Код запроса разрешений
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Лаунчер для выбора файла (современный способ вместо startActivityForResult)
    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                handleFile(uri);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        imageView   = findViewById(R.id.imageView);
        videoView   = findViewById(R.id.videoView);
        audioPanel  = findViewById(R.id.audioPanel);
        btnPlay     = findViewById(R.id.btnPlay);
        btnPause    = findViewById(R.id.btnPause);
        btnStop     = findViewById(R.id.btnStop);
        tvFileName  = findViewById(R.id.tvFileName);
        tvStatus    = findViewById(R.id.tvStatus);
        tvAudioFile = findViewById(R.id.tvAudioFile);

        // Кнопка выбора файла
        Button btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSelectFile.setOnClickListener(v -> checkPermissionsAndPick());

        // Кнопки тестовых файлов
        Button btnTestImage = findViewById(R.id.btnTestImage);
        Button btnTestAudio = findViewById(R.id.btnTestAudio);
        Button btnTestVideo = findViewById(R.id.btnTestVideo);

        if (btnTestImage != null) {
            btnTestImage.setOnClickListener(v -> {
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                audioPanel.setVisibility(View.GONE);
                stopMediaPlayer();
                imageView.setImageResource(R.drawable.test_lab6_photo);
                tvFileName.setText("Тестовый файл: test_lab6_photo.png");
                tvStatus.setText("Изображение загружено через ImageView");
            });
        }
        if (btnTestAudio != null) {
            btnTestAudio.setOnClickListener(v -> loadTestAudio());
        }
        if (btnTestVideo != null) {
            btnTestVideo.setOnClickListener(v -> loadTestVideo());
        }

        // Кнопки управления аудио
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                tvStatus.setText("Воспроизведение...");
            }
        });

        btnPause.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                tvStatus.setText("Пауза");
            }
        });

        btnStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                try {
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvStatus.setText("Остановлено");
            }
        });
    }

    // ─── ЗАГРУЗКА ТЕСТОВЫХ ФАЙЛОВ ИЗ res/raw ─────────────────────────────────


    private void loadTestAudio() {
        audioPanel.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        stopMediaPlayer();
        mediaPlayer = MediaPlayer.create(this, R.raw.test_audio);
        if (mediaPlayer != null) {
            tvAudioFile.setText("Тестовый: audio_test.mp3");
            tvStatus.setText("Аудио загружено — нажмите Играть");
            mediaPlayer.setOnCompletionListener(mp ->
                    tvStatus.setText("Воспроизведение завершено"));
            mediaPlayer.start();
            tvStatus.setText("Воспроизведение аудио через MediaPlayer...");
        } else {
            tvStatus.setText("Тестовый аудиофайл не найден. Добавьте audio_test.mp3 в res/raw/");
            Toast.makeText(this, "Добавьте audio_test.mp3 в res/raw/", Toast.LENGTH_LONG).show();
        }
    }

    private void loadTestVideo() {
        videoView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        audioPanel.setVisibility(View.GONE);
        stopMediaPlayer();
        tvStatus.setText("Загрузка видео...");

        try {
            // Попытка загрузить тестовое видео из res/raw
            String packageName = getPackageName();
            int videoResId = getResources().getIdentifier("test_video", "raw", packageName);
            if (videoResId != 0) {
                String path = "android.resource://" + packageName + "/" + videoResId;
                videoView.setVideoURI(Uri.parse(path));
            } else {
                tvStatus.setText("Тестовый видеофайл не найден. Добавьте video_test.mp4 в res/raw/");
                Toast.makeText(this, "Добавьте video_test.mp4 в res/raw/", Toast.LENGTH_LONG).show();
                videoView.setVisibility(View.GONE);
                return;
            }

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.requestFocus();

            videoView.setOnPreparedListener(mp -> {
                tvStatus.setText("Воспроизведение видео через VideoView...");
                videoView.start();
            });

            videoView.setOnCompletionListener(mp ->
                    tvStatus.setText("Воспроизведение видео завершено"));

            videoView.setOnErrorListener((mp, what, extra) -> {
                tvStatus.setText("Ошибка воспроизведения видео");
                Toast.makeText(this, "Ошибка видео", Toast.LENGTH_SHORT).show();
                return true;
            });
        } catch (Exception e) {
            tvStatus.setText("Ошибка загрузки видео");
            videoView.setVisibility(View.GONE);
        }
    }

    // ─── ПРОВЕРКА РАЗРЕШЕНИЙ ──────────────────────────────────────────────────

    private void checkPermissionsAndPick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: отдельные разрешения для каждого типа медиа
            String[] perms = {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
            if (allGranted(perms)) {
                openFilePicker();
            } else {
                ActivityCompat.requestPermissions(this, perms, PERMISSION_REQUEST_CODE);
            }
        } else {
            // Android 12 и ниже: одно разрешение READ_EXTERNAL_STORAGE
            String perm = Manifest.permission.READ_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{perm}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private boolean allGranted(String[] permissions) {
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allOk = true;
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    allOk = false;
                    break;
                }
            }
            if (allOk) {
                openFilePicker();
            } else {
                Toast.makeText(this,
                        "Разрешение не выдано. Доступ к файлам невозможен.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // ─── ВЫБОР ФАЙЛА ─────────────────────────────────────────────────────────

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // Фильтр только по нужным типам файлов
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                "image/*", "audio/*", "video/*"
        });
        filePickerLauncher.launch(intent);
    }

    // ─── ОБРАБОТКА ФАЙЛА ─────────────────────────────────────────────────────

    private void handleFile(Uri uri) {
        // Скрываем все панели
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        audioPanel.setVisibility(View.GONE);
        stopMediaPlayer();

        // Получаем имя файла и MIME-тип
        String fileName = getFileName(uri);
        String mimeType = getContentResolver().getType(uri);

        tvFileName.setText("Файл: " + fileName);

        // Определяем тип файла: сначала по MIME, затем по расширению
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                showImage(uri);
            } else if (mimeType.startsWith("audio/")) {
                showAudio(uri, fileName);
            } else if (mimeType.startsWith("video/")) {
                showVideo(uri);
            } else {
                tvStatus.setText("Неподдерживаемый тип: " + mimeType);
            }
        } else {
            // Определяем по расширению если MIME не определён
            String ext = getExtension(fileName);
            switch (ext) {
                case "jpg": case "jpeg": case "png": case "gif": case "bmp": case "webp":
                    showImage(uri);
                    break;
                case "mp3": case "wav": case "ogg": case "aac": case "m4a": case "flac":
                    showAudio(uri, fileName);
                    break;
                case "mp4": case "3gp": case "mkv": case "avi": case "mov":
                    showVideo(uri);
                    break;
                default:
                    tvStatus.setText("Формат файла не поддерживается");
                    Toast.makeText(this, "Неизвестный формат", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ─── ОТОБРАЖЕНИЕ ИЗОБРАЖЕНИЯ ──────────────────────────────────────────────

    private void showImage(Uri uri) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageURI(uri);
        tvStatus.setText("Изображение отображено через ImageView");
    }

    // ─── ВОСПРОИЗВЕДЕНИЕ АУДИО ────────────────────────────────────────────────

    private void showAudio(Uri uri, String fileName) {
        audioPanel.setVisibility(View.VISIBLE);
        tvAudioFile.setText(fileName);
        tvStatus.setText("Аудиофайл загружен — нажмите Играть");

        stopMediaPlayer();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            tvStatus.setText("Воспроизведение аудио через MediaPlayer...");

            mediaPlayer.setOnCompletionListener(mp ->
                    tvStatus.setText("Воспроизведение завершено"));

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                tvStatus.setText("Ошибка воспроизведения аудио");
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
            tvStatus.setText("Ошибка: " + e.getMessage());
            Toast.makeText(this, "Не удалось воспроизвести аудио", Toast.LENGTH_SHORT).show();
        }
    }

    // ─── ВОСПРОИЗВЕДЕНИЕ ВИДЕО ────────────────────────────────────────────────

    private void showVideo(Uri uri) {
        videoView.setVisibility(View.VISIBLE);
        tvStatus.setText("Видео загружается...");

        // Добавляем стандартные элементы управления видео (пауза, перемотка)
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        videoView.setOnPreparedListener(mp -> {
            tvStatus.setText("Воспроизведение видео через VideoView...");
            videoView.start();
        });

        videoView.setOnCompletionListener(mp ->
                tvStatus.setText("Воспроизведение видео завершено"));

        videoView.setOnErrorListener((mp, what, extra) -> {
            tvStatus.setText("Ошибка воспроизведения видео");
            Toast.makeText(this, "Не удалось воспроизвести видео", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    // ─── ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ───────────────────────────────────────────────

    /**
     * Получаем настоящее имя файла через ContentResolver
     * (URI вида content:// не содержит имя файла напрямую)
     */
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "неизвестный файл";
    }

    /**
     * Получаем расширение файла в нижнем регистре
     */
    private String getExtension(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Освобождаем ресурсы MediaPlayer
     */
    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            } catch (Exception ignored) {}
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMediaPlayer(); // Обязательно освобождаем ресурсы при закрытии
    }
}