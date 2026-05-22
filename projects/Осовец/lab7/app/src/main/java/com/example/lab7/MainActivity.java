package com.example.lab7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindNavigation();
        bindAuthorAction();
    }

    private void bindNavigation() {
        findViewById(R.id.btnAudio).setOnClickListener(v -> open(AudioPlayerActivity.class));
        findViewById(R.id.btnVideo).setOnClickListener(v -> open(VideoPlayerActivity.class));
        findViewById(R.id.btnCamera).setOnClickListener(v -> open(CameraActivity.class));
        findViewById(R.id.btnHistory).setOnClickListener(v -> open(HistoryActivity.class));
        findViewById(R.id.btnHelp).setOnClickListener(v -> open(HelpActivity.class));
    }

    private void bindAuthorAction() {
        Button authorButton = findViewById(R.id.authorActionButton);
        authorButton.setOnClickListener(v -> Toast.makeText(
                this,
                getString(R.string.author_signature),
                Toast.LENGTH_SHORT
        ).show());
    }

    private void open(Class<?> destination) {
        startActivity(new Intent(this, destination));
    }
}