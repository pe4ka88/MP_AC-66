package com.example.labb7new7;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAudio = findViewById(R.id.btnAudio);
        Button btnVideo = findViewById(R.id.btnVideo);
        Button btnPhoto = findViewById(R.id.btnPhoto);
        Button btnHelp = findViewById(R.id.btnHelp);

        btnAudio.setOnClickListener(v ->
                startActivity(new Intent(this, AudioActivity.class)));

        btnVideo.setOnClickListener(v ->
                startActivity(new Intent(this, VideoActivity.class)));

        btnPhoto.setOnClickListener(v ->
                startActivity(new Intent(this, PhotoActivity.class)));

        Button btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        btnHelp.setOnClickListener(v ->
                startActivity(new Intent(this, HelpActivity.class)));
    }
}
