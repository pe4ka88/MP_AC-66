package com.example.mediamanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoViewerActivity extends AppCompatActivity {

    private TextView tvPhotoInfo;
    private ImageView photoView;
    private Button btnBack, btnZoomOut, btnZoomIn, btnReset;
    private String photoPath;
    private float currentScale = 1.0f;
    private Matrix matrix = new Matrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        initViews();
        photoPath = getIntent().getStringExtra("photo_path");

        if (photoPath != null) {
            displayPhoto();
            setPhotoInfo();
        } else {
            Toast.makeText(this, "Ошибка: фото не найдено", Toast.LENGTH_SHORT).show();
            finish();
        }

        setListeners();
    }

    private void initViews() {
        tvPhotoInfo = findViewById(R.id.tvPhotoInfo);
        photoView = findViewById(R.id.photoView);
        btnBack = findViewById(R.id.btnBack);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnReset = findViewById(R.id.btnReset);
    }

    private void displayPhoto() {
        File imgFile = new File(photoPath);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            photoView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Файл не существует", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPhotoInfo() {
        File imgFile = new File(photoPath);
        long lastModified = imgFile.lastModified();
        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(new Date(lastModified));
        tvPhotoInfo.setText("Название: " + imgFile.getName() + "\nДата: " + date + "\nРазмер: " + imgFile.length() / 1024 + " KB");
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnZoomIn.setOnClickListener(v -> {
            currentScale += 0.2f;
            if (currentScale > 3.0f) currentScale = 3.0f;
            applyZoom();
        });

        btnZoomOut.setOnClickListener(v -> {
            currentScale -= 0.2f;
            if (currentScale < 0.5f) currentScale = 0.5f;
            applyZoom();
        });

        btnReset.setOnClickListener(v -> {
            currentScale = 1.0f;
            applyZoom();
        });
    }

    private void applyZoom() {
        matrix.setScale(currentScale, currentScale);
        photoView.setScaleType(ImageView.ScaleType.MATRIX);
        photoView.setImageMatrix(matrix);
    }
}