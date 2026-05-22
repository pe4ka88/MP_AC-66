package com.example.mediaezepchukac66;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CameraActivity extends AppCompatActivity {

    private ImageView imageView;
    private FrameLayout previewContainer;

    private final ActivityResultLauncher<Void> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(),
                    bitmap -> {
                        if (bitmap != null) {
                            // Показать контейнер превью и установить фото
                            previewContainer.setVisibility(FrameLayout.VISIBLE);
                            imageView.setImageBitmap(bitmap);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = findViewById(R.id.imageView);
        previewContainer = findViewById(R.id.previewContainer);
        ImageButton btnTakePhoto = findViewById(R.id.btnTakePhoto);

        // Сначала превью скрыто
        previewContainer.setVisibility(FrameLayout.GONE);

        // Проверка разрешений камеры
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }

        btnTakePhoto.setOnClickListener(v -> {
            // Запуск камеры
            takePictureLauncher.launch(null);
        });
    }
}