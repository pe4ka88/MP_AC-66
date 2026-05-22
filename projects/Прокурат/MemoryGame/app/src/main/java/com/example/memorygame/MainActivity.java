package com.example.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button aboutButton = findViewById(R.id.authorButton);
        aboutButton.setText(R.string.about_button);
        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        Button easyButton = findViewById(R.id.easyButton);
        easyButton.setText(R.string.easy_mode);
        easyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameEasyActivity.class);
            startActivity(intent);
        });

        Button mediumButton = findViewById(R.id.mediumButton);
        mediumButton.setText(R.string.medium_mode);
        mediumButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameMediumActivity.class);
            startActivity(intent);
        });

        Button hardButton = findViewById(R.id.hardButton);
        hardButton.setText(R.string.hard_mode);
        hardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameHardActivity.class);
            startActivity(intent);
        });
    }
}