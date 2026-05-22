package com.example.lab7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageView imageViewPhoto;
    private VideoView videoView;
    private TextView tvStatus;

    private Button btnTakePhoto;
    private Button btnPlayAudio;
    private Button btnPauseAudio;
    private Button btnStopAudio;
    private Button btnShowVideo;

    private MediaPlayer mediaPlayer;
    private String currentPhotoPath;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            dispatchTakePictureIntent();
                        } else {
                            Toast.makeText(this, "Для съёмки необходимо разрешение камеры", Toast.LENGTH_LONG).show();
                            tvStatus.setText("Разрешение камеры не получено");
                        }
                    }
            );

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            showCapturedPhoto();
                            tvStatus.setText("Фотоснимок успешно создан и отображен");
                        } else if (result.getResultCode() == RESULT_CANCELED) {
                            // Пользователь закрыл камеру сам или она недоступна
                            if (currentPhotoPath == null || !new File(currentPhotoPath).exists()) {
                                tvStatus.setText("Камера недоступна на данном устройстве (эмулятор не поддерживает камеру)");
                                Toast.makeText(this, "Камера недоступна — работает только на реальном Android-устройстве", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        videoView = findViewById(R.id.videoView);
        tvStatus = findViewById(R.id.tvStatus);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        btnPauseAudio = findViewById(R.id.btnPauseAudio);
        btnStopAudio = findViewById(R.id.btnStopAudio);
        btnShowVideo = findViewById(R.id.btnShowVideo);

        btnTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());

        btnPlayAudio.setOnClickListener(v -> playAudio());

        btnPauseAudio.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                tvStatus.setText("Аудио поставлено на паузу");
            }
        });

        btnStopAudio.setOnClickListener(v -> stopAudio());

        btnShowVideo.setOnClickListener(v -> playVideo());
    }

    private void playAudio() {
        stopVideo();

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.tsevan_audio_lab7);
        }

        if (mediaPlayer != null) {
            mediaPlayer.start();
            tvStatus.setText("Воспроизведение аудио через MediaPlayer");

            mediaPlayer.setOnCompletionListener(mp ->
                    tvStatus.setText("Воспроизведение аудио завершено"));
        } else {
            tvStatus.setText("Аудиофайл не найден");
            Toast.makeText(this, "Файл lysiuk_audio_lab7.mp3 не найден в res/raw", Toast.LENGTH_LONG).show();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception ignored) {
            }

            mediaPlayer.release();
            mediaPlayer = null;
            tvStatus.setText("Аудио остановлено");
        }
    }

    private void playVideo() {
        stopAudio();

        imageViewPhoto.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.tsevan_video_lab7;
        Uri videoUri = Uri.parse(videoPath);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();

        videoView.setOnPreparedListener(mp -> {
            tvStatus.setText("Воспроизведение видео через VideoView");
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

    private void stopVideo() {
        if (videoView != null && videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    private void dispatchTakePictureIntent() {
        stopAudio();
        stopVideo();

        // Проверяем наличие камеры на устройстве
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            tvStatus.setText("Камера недоступна — устройство не имеет камеры");
            Toast.makeText(this, "Камера недоступна на данном устройстве", Toast.LENGTH_LONG).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            tvStatus.setText("Камера недоступна на данном устройстве (эмулятор не поддерживает камеру)");
            Toast.makeText(this, "Камера недоступна — работает только на реальном Android-устройстве", Toast.LENGTH_LONG).show();
            return;
        }

        File photoFile;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            tvStatus.setText("Ошибка создания файла изображения");
            Toast.makeText(this, "Не удалось создать файл для фото", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri photoURI = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".provider",
                photoFile
        );

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        cameraLauncher.launch(takePictureIntent);
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, "lysiuk_lab7_photo.jpg");
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showCapturedPhoto() {
        if (currentPhotoPath != null) {
            imageViewPhoto.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            imageViewPhoto.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAudio();
        stopVideo();
    }
}