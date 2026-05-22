package com.example.memorygame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView infoText = findViewById(R.id.infoText);
        infoText.setText(R.string.about_info);

        Button backButton = findViewById(R.id.backButton);
        backButton.setText(R.string.back_button);
        backButton.setOnClickListener(v -> finish());
    }
}