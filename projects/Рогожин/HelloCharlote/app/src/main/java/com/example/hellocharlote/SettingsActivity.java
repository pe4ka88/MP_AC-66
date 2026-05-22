package com.example.hellocharlote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar seekVolume;
    private CheckBox checkMusic;
    private RadioGroup difficultyGroup;

    private SharedPreferences prefs;

    private static final String PREFS_NAME = "game_settings";
    private static final String KEY_VOLUME = "music_volume";
    private static final String KEY_MUSIC = "music_enabled";
    private static final String KEY_DIFFICULTY = "difficulty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seekVolume = findViewById(R.id.seekVolume);
        checkMusic = findViewById(R.id.checkMusic);
        difficultyGroup = findViewById(R.id.difficultyGroup);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadSettings();
        setupListeners();
    }

    private void loadSettings() {
        int volume = prefs.getInt(KEY_VOLUME, 70);
        boolean musicEnabled = prefs.getBoolean(KEY_MUSIC, true);
        int difficulty = prefs.getInt(KEY_DIFFICULTY, R.id.diffNormal);

        seekVolume.setProgress(volume);
        checkMusic.setChecked(musicEnabled);
        difficultyGroup.check(difficulty);
    }

    private void setupListeners() {

        // Изменение громкости музыки
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt(KEY_VOLUME, progress).apply();
                // Применяем громкость сразу
                startService(new Intent(SettingsActivity.this, MusicService.class));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Включение/выключение музыки
        checkMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_MUSIC, isChecked).apply();
            startService(new Intent(SettingsActivity.this, MusicService.class));
        });

        // Выбор сложности (только сохранение)
        difficultyGroup.setOnCheckedChangeListener((group, checkedId) ->
                prefs.edit().putInt(KEY_DIFFICULTY, checkedId).apply()
        );
    }
}