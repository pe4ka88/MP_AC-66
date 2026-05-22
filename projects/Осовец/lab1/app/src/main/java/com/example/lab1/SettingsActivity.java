package com.example.lab1;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup gridSizeRadioGroup;
    private RadioGroup matchModeRadioGroup;
    private RadioGroup cardSetRadioGroup;
    private RadioGroup colorSchemeRadioGroup;
    private Switch mixedModeSwitch;
    
    private ImageView cardBackPreview;
    private Button saveButton;
    private Button cancelButton;
    private Button resetButton;
    
    private ScrollView settingsScrollView;
    private LinearLayout settingsContainer;
    
    private GameSettings settings;
    
    // Текущие выбранные значения
    private int selectedGridRows = 4;
    private int selectedGridCols = 4;
    private int selectedMatchCount = 2;
    private boolean selectedMixedMode = false;
    private int selectedCardSet = 0;
    private int selectedColorScheme = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        settings = new GameSettings(this);
        
        initializeViews();
        loadCurrentSettings();
        setupListeners();
        updatePreview();
        applyColorScheme();
    }

    private void initializeViews() {
        settingsScrollView = findViewById(R.id.settingsScrollView);
        settingsContainer = findViewById(R.id.settingsContainer);
        
        gridSizeRadioGroup = findViewById(R.id.gridSizeRadioGroup);
        matchModeRadioGroup = findViewById(R.id.matchModeRadioGroup);
        cardSetRadioGroup = findViewById(R.id.cardSetRadioGroup);
        colorSchemeRadioGroup = findViewById(R.id.colorSchemeRadioGroup);
        mixedModeSwitch = findViewById(R.id.mixedModeSwitch);
        
        cardBackPreview = findViewById(R.id.cardBackPreview);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        resetButton = findViewById(R.id.resetButton);
    }

    private void loadCurrentSettings() {
        // Загружаем размер поля
        selectedGridRows = settings.getGridRows();
        selectedGridCols = settings.getGridCols();
        selectGridSize();
        
        // Загружаем режим игры
        selectedMatchCount = settings.getMatchCount();
        if (selectedMatchCount == 2) {
            ((RadioButton) findViewById(R.id.modePairs)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.modeTriplets)).setChecked(true);
        }
        
        // Загружаем смешанный режим
        selectedMixedMode = settings.isMixedMode();
        mixedModeSwitch.setChecked(selectedMixedMode);
        updateMixedModeUI();
        
        // Загружаем набор картинок
        selectedCardSet = settings.getCardSet();
        selectCardSet();
        
        // Загружаем цветовую схему
        selectedColorScheme = settings.getColorScheme();
        selectColorScheme();
    }

    private void selectGridSize() {
        if (selectedGridRows == 4 && selectedGridCols == 4) {
            ((RadioButton) findViewById(R.id.gridSize4x4)).setChecked(true);
        } else if (selectedGridRows == 4 && selectedGridCols == 5) {
            ((RadioButton) findViewById(R.id.gridSize4x5)).setChecked(true);
        } else if (selectedGridRows == 5 && selectedGridCols == 6) {
            ((RadioButton) findViewById(R.id.gridSize5x6)).setChecked(true);
        } else if (selectedGridRows == 6 && selectedGridCols == 6) {
            ((RadioButton) findViewById(R.id.gridSize6x6)).setChecked(true);
        }
    }

    private void selectCardSet() {
        switch (selectedCardSet) {
            case 0:
                ((RadioButton) findViewById(R.id.cardSetShapes)).setChecked(true);
                break;
            case 1:
                ((RadioButton) findViewById(R.id.cardSetAnimals)).setChecked(true);
                break;
            case 2:
                ((RadioButton) findViewById(R.id.cardSetFruits)).setChecked(true);
                break;
            case 3:
                ((RadioButton) findViewById(R.id.cardSetEmojis)).setChecked(true);
                break;
        }
    }

    private void selectColorScheme() {
        switch (selectedColorScheme) {
            case 0:
                ((RadioButton) findViewById(R.id.colorBlue)).setChecked(true);
                break;
            case 1:
                ((RadioButton) findViewById(R.id.colorGreen)).setChecked(true);
                break;
            case 2:
                ((RadioButton) findViewById(R.id.colorPurple)).setChecked(true);
                break;
            case 3:
                ((RadioButton) findViewById(R.id.colorOrange)).setChecked(true);
                break;
            case 4:
                ((RadioButton) findViewById(R.id.colorPink)).setChecked(true);
                break;
        }
    }

    private void setupListeners() {
        gridSizeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.gridSize4x4) {
                selectedGridRows = 4;
                selectedGridCols = 4;
            } else if (checkedId == R.id.gridSize4x5) {
                selectedGridRows = 4;
                selectedGridCols = 5;
            } else if (checkedId == R.id.gridSize5x6) {
                selectedGridRows = 5;
                selectedGridCols = 6;
            } else if (checkedId == R.id.gridSize6x6) {
                selectedGridRows = 6;
                selectedGridCols = 6;
            }
            updateMatchModeAvailability();
        });

        matchModeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.modePairs) {
                selectedMatchCount = 2;
            } else if (checkedId == R.id.modeTriplets) {
                selectedMatchCount = 3;
            }
        });

        mixedModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedMixedMode = isChecked;
            updateMixedModeUI();
            updateMatchModeAvailability();
        });

        cardSetRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.cardSetShapes) {
                selectedCardSet = 0;
            } else if (checkedId == R.id.cardSetAnimals) {
                selectedCardSet = 1;
            } else if (checkedId == R.id.cardSetFruits) {
                selectedCardSet = 2;
            } else if (checkedId == R.id.cardSetEmojis) {
                selectedCardSet = 3;
            }
        });

        colorSchemeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.colorBlue) {
                selectedColorScheme = 0;
            } else if (checkedId == R.id.colorGreen) {
                selectedColorScheme = 1;
            } else if (checkedId == R.id.colorPurple) {
                selectedColorScheme = 2;
            } else if (checkedId == R.id.colorOrange) {
                selectedColorScheme = 3;
            } else if (checkedId == R.id.colorPink) {
                selectedColorScheme = 4;
            }
            updatePreview();
            applyColorScheme();
        });

        saveButton.setOnClickListener(v -> saveSettings());
        cancelButton.setOnClickListener(v -> finish());
        resetButton.setOnClickListener(v -> resetSettings());
    }

    private void updateMatchModeAvailability() {
        int totalCards = selectedGridRows * selectedGridCols;
        RadioButton tripletsButton = findViewById(R.id.modeTriplets);
        RadioButton pairsButton = findViewById(R.id.modePairs);
        
        if (selectedMixedMode) {
            // В смешанном режиме отключаем выбор пары/тройки
            tripletsButton.setEnabled(false);
            pairsButton.setEnabled(false);
            
            // Проверяем, можно ли использовать смешанный режим для данного размера поля
            // Нужно, чтобы можно было разбить на комбинацию пар и троек
            boolean canUseMixed = checkMixedModeCompatibility(totalCards);
            if (!canUseMixed) {
                Toast.makeText(this, 
                    "Для этого размера поля смешанный режим недоступен", 
                    Toast.LENGTH_SHORT).show();
                selectedMixedMode = false;
                mixedModeSwitch.setChecked(false);
                updateMixedModeUI();
            }
        } else {
            pairsButton.setEnabled(true);
            // Тройки возможны только если общее количество карт делится на 3
            if (totalCards % 3 != 0) {
                tripletsButton.setEnabled(false);
                if (selectedMatchCount == 3) {
                    selectedMatchCount = 2;
                    pairsButton.setChecked(true);
                }
            } else {
                tripletsButton.setEnabled(true);
            }
        }
    }

    private boolean checkMixedModeCompatibility(int totalCards) {
        // Проверяем, можно ли разбить totalCards на комбинацию пар и троек
        // pairsCount * 2 + tripletsCount * 3 = totalCards
        // Нужно хотя бы по одной паре и тройке
        for (int pairs = 1; pairs <= totalCards / 2; pairs++) {
            int remaining = totalCards - pairs * 2;
            if (remaining > 0 && remaining % 3 == 0 && remaining / 3 >= 1) {
                return true;
            }
        }
        return false;
    }

    private void updateMixedModeUI() {
        // Обновляем состояние RadioGroup в зависимости от смешанного режима
        matchModeRadioGroup.setAlpha(selectedMixedMode ? 0.5f : 1.0f);
        
        RadioButton tripletsButton = findViewById(R.id.modeTriplets);
        RadioButton pairsButton = findViewById(R.id.modePairs);
        
        if (selectedMixedMode) {
            tripletsButton.setEnabled(false);
            pairsButton.setEnabled(false);
        } else {
            pairsButton.setEnabled(true);
            updateMatchModeAvailability();
        }
    }

    private void updatePreview() {
        // Создаем превью рубашки карты с выбранной цветовой схемой
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(24);
        drawable.setStroke(4, getDarkerColor(getColorForScheme(selectedColorScheme)));
        drawable.setColor(getColorForScheme(selectedColorScheme));
        
        cardBackPreview.setImageDrawable(drawable);
    }

    private void applyColorScheme() {
        int color = getColorForScheme(selectedColorScheme);
        int backgroundColor = getLighterColor(color);
        
        settingsScrollView.setBackgroundColor(backgroundColor);
        saveButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
    }

    private int getColorForScheme(int scheme) {
        switch (scheme) {
            case 0: return Color.parseColor("#1976D2"); // Синий
            case 1: return Color.parseColor("#388E3C"); // Зелёный
            case 2: return Color.parseColor("#7B1FA2"); // Фиолетовый
            case 3: return Color.parseColor("#F57C00"); // Оранжевый
            case 4: return Color.parseColor("#C2185B"); // Розовый
            default: return Color.parseColor("#1976D2");
        }
    }

    private int getLighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] * 0.2f; // Уменьшаем насыщенность
        hsv[2] = 0.95f; // Увеличиваем яркость
        return Color.HSVToColor(hsv);
    }

    private int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.7f; // Уменьшаем яркость
        return Color.HSVToColor(hsv);
    }

    private void saveSettings() {
        // Проверка совместимости настроек
        int totalCards = selectedGridRows * selectedGridCols;
        
        if (selectedMixedMode) {
            // Проверяем совместимость со смешанным режимом
            if (!checkMixedModeCompatibility(totalCards)) {
                Toast.makeText(this, 
                    "Для этого размера поля смешанный режим недоступен", 
                    Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (totalCards % selectedMatchCount != 0) {
                Toast.makeText(this, 
                    "Количество карт должно делиться на " + selectedMatchCount, 
                    Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // Сохраняем настройки
        settings.setGridRows(selectedGridRows);
        settings.setGridCols(selectedGridCols);
        settings.setMatchCount(selectedMatchCount);
        settings.setMixedMode(selectedMixedMode);
        settings.setCardSet(selectedCardSet);
        settings.setColorScheme(selectedColorScheme);
        
        Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
        
        // Устанавливаем результат и закрываем
        setResult(RESULT_OK);
        finish();
    }

    private void resetSettings() {
        new AlertDialog.Builder(this)
            .setTitle("Сбросить настройки?")
            .setMessage("Все настройки будут сброшены к значениям по умолчанию.")
            .setPositiveButton("Сбросить", (dialog, which) -> {
                settings.resetToDefaults();
                loadCurrentSettings();
                updatePreview();
                applyColorScheme();
                Toast.makeText(this, "Настройки сброшены", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Отмена", null)
            .show();
    }
}
