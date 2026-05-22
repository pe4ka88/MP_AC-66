package com.example.laba2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TaskActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Button btnPress = findViewById(R.id.btnPress);
        Button btnAbout = findViewById(R.id.btnAbout);
        Button btnBack  = findViewById(R.id.btnBack);
        TextView tvTask = findViewById(R.id.tvTaskText);

        btnPress.setOnClickListener(v ->
                Toast.makeText(this, "Нажимает Занько Я.С.", Toast.LENGTH_SHORT).show()
        );

        btnAbout.setOnClickListener(v ->
                startActivity(new Intent(this, AuthorActivity.class))
        );

        btnBack.setOnClickListener(v -> finish());

        String fullTask =
                "Полная формулировка задачи ЛР2:\n" +
                "(вставь сюда текст задания от преподавателя полностью)\n\n" +
                "Выполнил: Занько  Я.С.\n" +
                "Группа: АС-65\n";

        tvTask.setText(fullTask);
    }
}
