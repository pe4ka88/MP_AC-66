package com.example.lr6;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private ImageView imageView;
    private VideoView videoView;
    private LinearLayout audioLayout;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);
        audioLayout = findViewById(R.id.audioLayout);

        // Контроллер для видео
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Кнопка показа задания
        Button btnTask = findViewById(R.id.btnTask);
        btnTask.setOnClickListener(v -> showTaskDialog());

        // Кнопка выбора файла
        Button btnPickFile = findViewById(R.id.btnPickFile);
        btnPickFile.setOnClickListener(v -> pickFile());

        // Кнопка остановки аудио
        Button btnStopAudio = findViewById(R.id.btnStopAudio);
        btnStopAudio.setOnClickListener(v -> stopAudio());
    }

    private void showTaskDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Постановка задачи")
            .setMessage(getString(R.string.task_text))
            .setPositiveButton("Понятно", null)
            .show();
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // Фильтр по медиа-файлам
        String[] mimetypes = {"image/*", "audio/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                processFile(uri);
            }
        }
    }

    private void processFile(Uri uri) {
        // Скрываем все элементы перед новым выбором
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        audioLayout.setVisibility(View.GONE);
        stopAudio();

        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageURI(uri);
            } else if (mimeType.startsWith("video/")) {
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(uri);
                videoView.start();
            } else if (mimeType.startsWith("audio/")) {
                audioLayout.setVisibility(View.VISIBLE);
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(this, uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAudio(); // Освобождаем ресурсы при закрытии
    }
}
