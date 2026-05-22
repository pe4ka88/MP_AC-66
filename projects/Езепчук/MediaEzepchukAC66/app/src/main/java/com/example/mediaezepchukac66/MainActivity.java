package com.example.mediaezepchukac66;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton fabCamera = findViewById(R.id.fabCamera);
        ImageButton btnPlayer = findViewById(R.id.btnPlayer);
        ImageButton btnGallery = findViewById(R.id.btnGallery);

        fabCamera.setOnClickListener(v ->
                startActivity(new Intent(this, CameraActivity.class)));

        btnPlayer.setOnClickListener(v ->
                startActivity(new Intent(this, PlayerActivity.class)));

        btnGallery.setOnClickListener(v ->
                startActivity(new Intent(this, GalleryActivity.class)));
    }
}