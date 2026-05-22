package com.example.lab6;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button authorButton = findViewById(R.id.btnAboutAuthorAction);
        Button backButton = findViewById(R.id.btnBackToMain);

        authorButton.setOnClickListener(v ->
                Toast.makeText(this, R.string.author_action_toast, Toast.LENGTH_SHORT).show());
        backButton.setOnClickListener(v -> finish());
    }
}