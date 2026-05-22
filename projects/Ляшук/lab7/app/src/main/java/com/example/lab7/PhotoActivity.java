package com.example.lab7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PhotoActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ImageView imageView;
    private DatabaseHelper dbHelper;
    
    private Matrix matrix = new Matrix();
    private float currentScale = 1.0f;
    private float currentRotation = 0f;

    private final ActivityResultLauncher<Intent> photoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                    resetTransform();
                    dbHelper.addRecord("Сделано новое фото (Ляшук В.И.)");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        dbHelper = new DatabaseHelper(this);
        imageView = findViewById(R.id.imageView);
        
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        Button btnBack = findViewById(R.id.btnBackPhoto);
        Button btnZoomIn = findViewById(R.id.btnZoomIn);
        Button btnZoomOut = findViewById(R.id.btnZoomOut);
        Button btnRotate = findViewById(R.id.btnRotate);

        btnTakePhoto.setOnClickListener(v -> {
            if (checkPermission()) takePhoto();
            else requestPermission();
        });

        btnZoomIn.setOnClickListener(v -> {
            currentScale *= 1.2f;
            updateMatrix();
        });

        btnZoomOut.setOnClickListener(v -> {
            currentScale /= 1.2f;
            updateMatrix();
        });

        btnRotate.setOnClickListener(v -> {
            currentRotation += 90f;
            updateMatrix();
        });

        // Долгое нажатие на поворот сбросит всё (скрытая фича для бонуса)
        btnRotate.setOnLongClickListener(v -> {
            resetTransform();
            Toast.makeText(this, "Трансформация сброшена", Toast.LENGTH_SHORT).show();
            return true;
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void resetTransform() {
        currentScale = 1.0f;
        currentRotation = 0f;
        matrix.reset();
        imageView.setImageMatrix(matrix);
    }

    private void updateMatrix() {
        matrix.reset();
        matrix.postScale(currentScale, currentScale);
        matrix.postRotate(currentRotation, (imageView.getWidth() / 2f), (imageView.getHeight() / 2f));
        imageView.setImageMatrix(matrix);
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void takePhoto() {
        photoLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
    }
}
