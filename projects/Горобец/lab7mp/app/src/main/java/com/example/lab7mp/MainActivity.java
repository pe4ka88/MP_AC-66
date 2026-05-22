package com.example.lab7mp; // замени на свой пакет

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnAudio, btnVideo, btnPhoto, btnHistory, btnHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAudio = findViewById(R.id.btnAudio);
        btnVideo = findViewById(R.id.btnVideo);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnHistory = findViewById(R.id.btnHistory);
        btnHelp = findViewById(R.id.btnHelp);

        btnAudio.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AudioActivity.class)));

        btnVideo.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, VideoActivity.class)));

        btnPhoto.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PhotoActivity.class)));

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));

        btnHelp.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HelpActivity.class)));
    }
}
