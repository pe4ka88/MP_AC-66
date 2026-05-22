package com.example.a6lab;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);

        String uriString = getIntent().getStringExtra("uri");
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            imageView.setImageURI(uri);
        }
    }
}