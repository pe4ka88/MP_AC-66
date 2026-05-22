package com.example.lab6mp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;

    private ImageView imageView;
    private VideoView videoView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);

        Button btnSelect = findViewById(R.id.btnSelectFile);
        Button btnClose = findViewById(R.id.btnClose);
        Button btnGorobec = findViewById(R.id.btnGorobec);

        btnSelect.setOnClickListener(v -> openFileChooser());
        btnClose.setOnClickListener(v -> closeAll());

        // 🔥 Кнопка "Нажимает Горобец" — открывает окно с заданием
        btnGorobec.setOnClickListener(v -> showTaskDialog());
    }

    private void showTaskDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Текст задания")
                .setMessage(
                        "Задание:\n" +
                                "Создать приложение, обеспечивающее выбор файла во внешнем хранилище с возможностью дальнейшей его обработки в зависимости от расширения:\n" +
                                "- графический файл отобразить с использованием элемента ImageView;\n" +
                                "- аудиофайл воспроизвести с использованием элемента MediaPlayer;\n" +
                                "- видеофайл воспроизвести с использованием элемента VideoView.\n" +
                                "2. Загрузить заранее набор медиафайлов.\n" +
                                "3. Создать новый проект.\n" +
                                "4. Добавить необходимые элементы интерфейса."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            handleFile(uri);
        }
    }

    private void closeAll() {
        imageView.setVisibility(View.GONE);

        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        videoView.setVisibility(View.GONE);

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void handleFile(Uri uri) {
        closeAll();

        String type = getContentResolver().getType(uri);

        if (type == null) {
            Toast.makeText(this, "Неизвестный формат файла", Toast.LENGTH_SHORT).show();
            return;
        }

        if (type.startsWith("image/")) {
            showImage(uri);
        } else if (type.startsWith("audio/")) {
            playAudio(uri);
        } else if (type.startsWith("video/")) {
            playVideo(uri);
        } else {
            Toast.makeText(this, "Неизвестный формат файла", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImage(Uri uri) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageURI(uri);
    }

    private void playAudio(Uri uri) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();

        Toast.makeText(this, "Воспроизведение аудио...", Toast.LENGTH_SHORT).show();
    }

    private void playVideo(Uri uri) {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);
        videoView.start();
    }
}
