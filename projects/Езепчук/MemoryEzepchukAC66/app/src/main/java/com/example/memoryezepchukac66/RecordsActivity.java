package com.example.memoryezepchukac66;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class RecordsActivity extends AppCompatActivity {

    private TextView recordsText;
    private Button clearButton;
    private Button restartButton;

    private DatabaseReference database;

    // Поля для записи рекорда
    private String nickname;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        recordsText = findViewById(R.id.recordsText);
        restartButton = findViewById(R.id.restartGameButton);

        // Подключение к Firebase
        database = FirebaseDatabase.getInstance().getReference("records");

        // Получаем nickname и score из Intent
        nickname = getIntent().getStringExtra("nickname");
        score = getIntent().getIntExtra("score", 0);

        // Сохраняем рекорд (если есть)
        saveRecord();

        // Загружаем и отображаем рекорды
        fetchRecords();


        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void saveRecord() {
        if (nickname == null || nickname.isEmpty()) return;

        DatabaseReference userRef = database.child(nickname);

        userRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Ошибка чтения рекорда", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer oldScore = null;
            if (task.getResult().exists()) {
                Object value = task.getResult().getValue();
                if (value instanceof Long) {
                    oldScore = ((Long) value).intValue();
                } else if (value instanceof Integer) {
                    oldScore = (Integer) value;
                }
            }

            if (oldScore == null || score > oldScore) {
                userRef.setValue(score).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        fetchRecords(); // Обновляем UI после сохранения
                    } else {
                        Toast.makeText(this, "Ошибка сохранения рекорда", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void fetchRecords() {
        database.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Ошибка загрузки рекордов", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Record> list = new ArrayList<>();
            DataSnapshot snapshot = task.getResult();

            for (DataSnapshot child : snapshot.getChildren()) {
                String nickname = child.getKey();
                Long scoreLong = child.getValue(Long.class);
                if (nickname != null && scoreLong != null) {
                    list.add(new Record(nickname, scoreLong.intValue()));
                }
            }

            if (list.isEmpty()) {
                recordsText.setText("Рекордов пока нет");
                return;
            }

            Collections.sort(list, (a, b) -> b.score - a.score);

            StringBuilder sb = new StringBuilder();
            int place = 1;
            for (Record r : list) {
                sb.append(place++)
                        .append(". ")
                        .append(r.nickname)
                        .append(" — ")
                        .append(r.score)
                        .append(" очков\n");
            }

            recordsText.setText(sb.toString());
        });
    }

    // ------------------ Вспомогательный класс ------------------
    private static class Record {
        String nickname;
        int score;

        Record(String nickname, int score) {
            this.nickname = nickname;
            this.score = score;
        }
    }
}
