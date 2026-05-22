package com.example.lab_1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private GridView mGrid;
    private GridAdapter mAdapter;
    private Button restartButton;
    private Button startButton;
    private Button recordsButton;
    private TextView textTime, textRemaining, textMotivation;
    private EditText editUserName;
    private View mainLayout;

    private int seconds = 0;
    private boolean running = false;
    private String currentUserName = "Player";

    private final String[] motivations = {
        "Great job!", "Keep going!", "You're a pro!", "Amazing speed!",
        "Focus!", "Almost there!", "Fantastic!", "Sharp mind!", "You got this!"
    };
    private final Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showStartScreen();
    }

    private void showStartScreen() {
        setContentView(R.layout.start_activity);
        startButton = findViewById(R.id.start_button);
        recordsButton = findViewById(R.id.records_button);
        editUserName = findViewById(R.id.edit_user_name);
        
        final Spinner spinnerWidth = findViewById(R.id.spinner_width);
        final Spinner spinnerHeight = findViewById(R.id.spinner_height);
        final Spinner spinnerMode = findViewById(R.id.spinner_mode);
        final Spinner spinnerColor = findViewById(R.id.spinner_color);

        SharedPreferences prefs = getSharedPreferences("Records", Context.MODE_PRIVATE);
        String lastUserName = prefs.getString("last_user_name", "");
        if (!lastUserName.isEmpty()) {
            editUserName.setText(lastUserName);
            currentUserName = lastUserName;
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editUserName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentUserName = name;

                SharedPreferences.Editor editor = getSharedPreferences("Records", Context.MODE_PRIVATE).edit();
                editor.putString("last_user_name", currentUserName);
                editor.apply();

                int width = Integer.parseInt(spinnerWidth.getSelectedItem().toString());
                int height = Integer.parseInt(spinnerHeight.getSelectedItem().toString());
                int mode = spinnerMode.getSelectedItemPosition() == 0 ? 2 : 3;
                int themeIndex = spinnerColor.getSelectedItemPosition();

                if (width * height < mode) {
                    Toast.makeText(MainActivity.this, "Grid too small for this mode!", Toast.LENGTH_SHORT).show();
                    return;
                }

                startGame(width, height, mode, themeIndex);
            }
        });

        recordsButton.setOnClickListener(v -> showRecordsScreen());
    }

    private void showRecordsScreen() {
        setContentView(R.layout.records_activity);
        TextView fullRecordsList = findViewById(R.id.full_records_list);
        Button backButton = findViewById(R.id.back_button);

        SharedPreferences prefs = getSharedPreferences("Records", Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet("list", new HashSet<>());
        List<String> list = new ArrayList<>(set);

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    int t1 = Integer.parseInt(o1.split(":")[1]);
                    int t2 = Integer.parseInt(o2.split(":")[1]);
                    return Integer.compare(t1, t2);
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        if (list.isEmpty()) {
            fullRecordsList.setText("No records yet. Be the first!");
        } else {
            StringBuilder sb = new StringBuilder();
            int rank = 1;
            for (String s : list) {
                String[] parts = s.split(":");
                sb.append(rank).append(". ").append(parts[0]).append(" - ").append(parts[1]).append("s\n");
                rank++;
            }
            fullRecordsList.setText(sb.toString());
        }

        backButton.setOnClickListener(v -> showStartScreen());
    }

    private void startGame(int width, int height, int mode, int themeIndex) {
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.main_layout);
        mGrid = findViewById(R.id.field);
        restartButton = findViewById(R.id.restart_button);
        textTime = findViewById(R.id.textTime);
        textRemaining = findViewById(R.id.textRemaining);
        textMotivation = findViewById(R.id.textMotivation);

        applyTheme(themeIndex);

        mGrid.setNumColumns(width);
        int totalCells = width * height;
        int activeCells = (totalCells / mode) * mode;

        mAdapter = new GridAdapter(this, width, height, mode, activeCells);
        mGrid.setAdapter(mAdapter);

        updateStats();
        startTimer();

        mGrid.setOnItemClickListener((parent, v, position, id) -> {
            if (mAdapter.handleCellClick(position)) {
                updateStats();

                if (new Random().nextInt(4) == 0) { // 25% chance
                    showMotivation();
                }

                if (mAdapter.isGameOver()) {
                    endGame();
                }
            }
        });

        restartButton.setOnClickListener(v -> {
            stopTimer();
            showStartScreen();
        });
    }

    private void applyTheme(int themeIndex) {
        int primaryColor;
        int backgroundColor;
        switch (themeIndex) {
            case 1: 
                primaryColor = getResources().getColor(R.color.green_primary);
                backgroundColor = Color.parseColor("#E8F5E9"); // Light Green
                break;
            case 2: 
                primaryColor = getResources().getColor(R.color.orange_primary);
                backgroundColor = Color.parseColor("#FFF3E0"); // Light Orange
                break;
            default: 
                primaryColor = getResources().getColor(R.color.blue_primary);
                backgroundColor = Color.parseColor("#E8EAF6"); // Light Blue
                break;
        }
        
        if (mainLayout != null) {
            mainLayout.setBackgroundColor(backgroundColor);
        }
        textMotivation.setTextColor(primaryColor);
    }

    private void startTimer() {
        seconds = 0;
        running = true;
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (running) {
                    seconds++;
                    textTime.setText(getString(R.string.str_time, seconds));
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        running = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void updateStats() {
        textRemaining.setText(getString(R.string.str_remaining, mAdapter.getRemainingSets()));
    }

    private void showMotivation() {
        String msg = motivations[new Random().nextInt(motivations.length)];
        textMotivation.setText(msg);
        textMotivation.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> textMotivation.setVisibility(View.INVISIBLE), 1500);
    }

    private void endGame() {
        stopTimer();
        saveRecord(currentUserName, seconds);

        String message = getString(R.string.game_over) + "\n" + currentUserName + ": " + seconds + "s";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void saveRecord(String name, int time) {
        SharedPreferences prefs = getSharedPreferences("Records", Context.MODE_PRIVATE);
        Set<String> records = new HashSet<>(prefs.getStringSet("list", new HashSet<>()));
        records.add(name + ":" + time);
        prefs.edit().putStringSet("list", records).apply();
    }
}
