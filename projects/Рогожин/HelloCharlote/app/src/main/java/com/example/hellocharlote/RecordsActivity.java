package com.example.hellocharlote;

import com.google.firebase.FirebaseApp;
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
        FirebaseApp.initializeApp(this);
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

        // Получаем данные из Intent
        int difficulty = getIntent().getIntExtra("difficulty", R.id.diffNormal);
        int initialTime = getIntent().getIntExtra("initialTime", 60);

        userRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Ошибка чтения рекорда", Toast.LENGTH_SHORT).show();
                return;
            }

            int oldScore = 0;
            if (task.getResult().exists()) {
                DataSnapshot snap = task.getResult();
                Long s = snap.child("score").getValue(Long.class);
                if (s != null) oldScore = s.intValue();
            }

            if (score > oldScore) {
                // Создаем объект с нужными полями
                RecordData recordData = new RecordData(score, difficulty, initialTime);
                userRef.setValue(recordData).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        fetchRecords();
                    } else {
                        Toast.makeText(this, "Ошибка сохранения рекорда", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Чтение рекордов
    private void fetchRecords() {
        database.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Ошибка загрузки рекордов", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<RecordData> list = new ArrayList<>();
            DataSnapshot snapshot = task.getResult();

            for (DataSnapshot child : snapshot.getChildren()) {
                String nickname = child.getKey();
                Long scoreLong = child.child("score").getValue(Long.class);
                Long diffLong = child.child("difficulty").getValue(Long.class);
                Long timeLong = child.child("initialTime").getValue(Long.class);

                if (nickname != null && scoreLong != null && diffLong != null && timeLong != null) {
                    list.add(new RecordData(nickname, scoreLong.intValue(), diffLong.intValue(), timeLong.intValue()));
                }
            }

            if (list.isEmpty()) {
                recordsText.setText("Рекордов пока нет");
                return;
            }

            Collections.sort(list, (a, b) -> b.score - a.score);

            StringBuilder sb = new StringBuilder();
            int place = 1;
            for (RecordData r : list) {
                String diffStr = (r.difficulty == R.id.diffEasy) ? "Легкий" :
                        (r.difficulty == R.id.diffHard) ? "Сложный" : "Средний";
                sb.append(place++)
                        .append(". ")
                        .append(r.nickname)
                        .append(" — ")
                        .append(r.score)
                        .append(" очков (")
                        .append(diffStr)
                        .append(", ")
                        .append(r.initialTime)
                        .append("с)\n");
            }

            recordsText.setText(sb.toString());
        });
    }

    // Вспомогательный класс для записи в Firebase
    private static class RecordData {
        public String nickname;
        public int score;
        public int difficulty;
        public int initialTime;

        // Для Firebase нужен пустой конструктор
        public RecordData() {}

        // Конструктор для записи
        public RecordData(int score, int difficulty, int initialTime) {
            this.score = score;
            this.difficulty = difficulty;
            this.initialTime = initialTime;
        }

        // Конструктор для чтения с ником
        public RecordData(String nickname, int score, int difficulty, int initialTime) {
            this.nickname = nickname;
            this.score = score;
            this.difficulty = difficulty;
            this.initialTime = initialTime;
        }
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
