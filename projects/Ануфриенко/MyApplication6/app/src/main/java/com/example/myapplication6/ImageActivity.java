package com.example.myapplication6;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Отдельная активность для отображения изображения на весь экран.
 * Запускается из MainActivity при выборе файла типа image/*.
 */
public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView  tvImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView   = findViewById(R.id.imageView);
        tvImageName = findViewById(R.id.tvImageName);

        // Получаем URI и имя файла из Intent
        String uriString = getIntent().getStringExtra("imageUri");
        String imageName = getIntent().getStringExtra("imageName");

        if (uriString != null) {
            Uri imageUri = Uri.parse(uriString);
            imageView.setImageURI(imageUri);

            if (imageName != null) {
                tvImageName.setText(imageName);
            }
        } else {
            Toast.makeText(this, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** Обработчик кнопки «← Назад» */
    public void goBack(View view) {
        finish();
    }
}