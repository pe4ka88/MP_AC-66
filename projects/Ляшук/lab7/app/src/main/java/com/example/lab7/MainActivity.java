package com.example.lab7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

        // 1. Признак авторства: Текстовое поле вверху
        TextView authorText = findViewById(R.id.authorText);
        authorText.setText("Разработал: Ляшук В.И.\nГруппа: АС-66");

        // 2. Признак авторства: Всплывающее уведомление
        Toast.makeText(this, "Приложение подготовил Ляшук В.И. (АС-66)", Toast.LENGTH_LONG).show();

        // 3. Признак авторства: Подпись внизу (уже в XML макете)

        Button btnAudio = findViewById(R.id.btnAudio);
        Button btnVideo = findViewById(R.id.btnVideo);
        Button btnPhoto = findViewById(R.id.btnPhoto);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnAbout = findViewById(R.id.btnAbout);

        btnAudio.setOnClickListener(v -> startActivity(new Intent(this, AudioActivity.class)));
        btnVideo.setOnClickListener(v -> startActivity(new Intent(this, VideoActivity.class)));
        btnPhoto.setOnClickListener(v -> startActivity(new Intent(this, PhotoActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        btnAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));

        // Логирование
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.addRecord("Приложение запущено (Ляшук В.И.)");
    }
}
