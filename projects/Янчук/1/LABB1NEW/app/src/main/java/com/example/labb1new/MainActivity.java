package com.example.labb1new;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labb1new.models.GameConfig;
import com.example.labb1new.utils.TaskInfoActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerSize, spinnerMode;
    private Button btnStart, btnRecords, btnTaskInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        spinnerSize = findViewById(R.id.spinnerSize);
        spinnerMode = findViewById(R.id.spinnerMode);
        btnStart = findViewById(R.id.btnStart);
        btnRecords = findViewById(R.id.btnRecords);
        btnTaskInfo = findViewById(R.id.btnTaskInfo);

        spinnerSize.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"4x4", "6x6", "8x8"}));

        spinnerMode.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Пары", "Тройки"}));

        btnStart.setOnClickListener(v -> startGame());
        btnRecords.setOnClickListener(v ->
                startActivity(new Intent(this, RecordsActivity.class)));
        btnTaskInfo.setOnClickListener(v ->
                startActivity(new Intent(this, TaskInfoActivity.class))
        );

    }

    private void startGame() {
        int gridSize = 4;
        String size = spinnerSize.getSelectedItem().toString();
        if (size.equals("6x6")) gridSize = 6;
        if (size.equals("8x8")) gridSize = 8;

        GameConfig.Mode mode =
                spinnerMode.getSelectedItem().toString().equals("Тройки")
                        ? GameConfig.Mode.TRIPLES
                        : GameConfig.Mode.PAIRS;

        GameConfig config = new GameConfig(gridSize, mode);

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("config", config);
        startActivity(intent);
    }
}



