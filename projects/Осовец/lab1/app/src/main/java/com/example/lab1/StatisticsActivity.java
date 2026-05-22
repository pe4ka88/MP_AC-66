package com.example.lab1;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private GameSettings settings;
    private ScrollView scrollView;
    private LinearLayout mainContainer;
    
    // Общая статистика
    private TextView playerNameText;
    private TextView gamesPlayedText;
    private TextView gamesWonText;
    private TextView winRateText;
    private ProgressBar winRateProgress;
    private TextView totalPlayTimeText;
    private TextView lastPlayedText;
    
    // Рекорды
    private TextView bestTimeText;
    private TextView bestMovesText;
    private TextView avgTimeText;
    private TextView avgMovesText;
    private TextView winStreakText;
    private TextView bestWinStreakText;
    private TextView totalPairsFoundText;
    
    // Статистика по размерам
    private LinearLayout gridStats4x4;
    private LinearLayout gridStats6x6;
    private LinearLayout gridStatsCustom;
    
    // Достижения
    private LinearLayout achievementsContainer;
    private TextView achievementsCountText;
    
    // История
    private LinearLayout historyContainer;
    
    // Кнопки
    private Button closeButton;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        
        settings = new GameSettings(this);
        initializeViews();
        applyColorScheme();
        loadStatistics();
        setupClickListeners();
        
        // Анимация появления
        scrollView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
    }
    
    private void initializeViews() {
        scrollView = findViewById(R.id.statisticsScrollView);
        mainContainer = findViewById(R.id.statisticsMainContainer);
        
        // Общая статистика
        playerNameText = findViewById(R.id.playerNameText);
        gamesPlayedText = findViewById(R.id.gamesPlayedText);
        gamesWonText = findViewById(R.id.gamesWonText);
        winRateText = findViewById(R.id.winRateText);
        winRateProgress = findViewById(R.id.winRateProgress);
        totalPlayTimeText = findViewById(R.id.totalPlayTimeText);
        lastPlayedText = findViewById(R.id.lastPlayedText);
        
        // Рекорды
        bestTimeText = findViewById(R.id.bestTimeText);
        bestMovesText = findViewById(R.id.bestMovesText);
        avgTimeText = findViewById(R.id.avgTimeText);
        avgMovesText = findViewById(R.id.avgMovesText);
        winStreakText = findViewById(R.id.winStreakText);
        bestWinStreakText = findViewById(R.id.bestWinStreakText);
        totalPairsFoundText = findViewById(R.id.totalPairsFoundText);
        
        // Статистика по размерам
        gridStats4x4 = findViewById(R.id.gridStats4x4);
        gridStats6x6 = findViewById(R.id.gridStats6x6);
        gridStatsCustom = findViewById(R.id.gridStatsCustom);
        
        // Достижения
        achievementsContainer = findViewById(R.id.achievementsContainer);
        achievementsCountText = findViewById(R.id.achievementsCountText);
        
        // История
        historyContainer = findViewById(R.id.historyContainer);
        
        // Кнопки
        closeButton = findViewById(R.id.closeButton);
        resetButton = findViewById(R.id.resetStatisticsButton);
    }
    
    private void applyColorScheme() {
        int primaryColor = settings.getPrimaryColor();
        int backgroundColor = settings.getBackgroundColor();
        
        scrollView.setBackgroundColor(backgroundColor);
        closeButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(primaryColor));
    }
    
    private void loadStatistics() {
        // Имя игрока
        playerNameText.setText(settings.getPlayerName());
        
        // Общая статистика
        int gamesPlayed = settings.getGamesPlayed();
        int gamesWon = settings.getGamesWon();
        gamesPlayedText.setText(String.valueOf(gamesPlayed));
        gamesWonText.setText(String.valueOf(gamesWon));
        
        double winRate = settings.getWinRate();
        winRateText.setText(settings.getWinRateFormatted());
        winRateProgress.setProgress((int) winRate);
        
        totalPlayTimeText.setText(settings.getTotalPlayTimeFormatted());
        lastPlayedText.setText(settings.getLastPlayedFormatted());
        
        // Рекорды
        long bestTime = settings.getBestTime();
        int bestMoves = settings.getBestMoves();
        
        if (bestTime == Long.MAX_VALUE) {
            bestTimeText.setText("—");
        } else {
            int seconds = (int) (bestTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            bestTimeText.setText(String.format("%02d:%02d", minutes, seconds));
        }
        
        bestMovesText.setText(bestMoves == Integer.MAX_VALUE ? "—" : String.valueOf(bestMoves));
        avgTimeText.setText(settings.getAverageTimeFormatted());
        avgMovesText.setText(settings.getAverageMovesFormatted());
        winStreakText.setText(String.valueOf(settings.getWinStreak()));
        bestWinStreakText.setText(String.valueOf(settings.getBestWinStreak()));
        totalPairsFoundText.setText(String.valueOf(settings.getTotalPairsFound()));
        
        // Статистика по размерам
        loadGridStats("4x4", gridStats4x4);
        loadGridStats("6x6", gridStats6x6);
        loadGridStats("custom", gridStatsCustom);
        
        // Достижения
        loadAchievements();
        
        // История
        loadHistory();
    }
    
    private void loadGridStats(String sizeKey, LinearLayout container) {
        int games = settings.getGridSizeGames(sizeKey);
        long bestTime = settings.getGridSizeBestTime(sizeKey);
        int bestMoves = settings.getGridSizeBestMoves(sizeKey);
        
        TextView gamesText = (TextView) container.getChildAt(1);
        TextView timeText = (TextView) container.getChildAt(3);
        TextView movesText = (TextView) container.getChildAt(5);
        
        gamesText.setText(String.valueOf(games));
        timeText.setText(bestTime == Long.MAX_VALUE ? "—" : formatTime(bestTime));
        movesText.setText(bestMoves == Integer.MAX_VALUE ? "—" : String.valueOf(bestMoves));
    }
    
    private String formatTime(long timeMs) {
        int seconds = (int) (timeMs / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    private void loadAchievements() {
        achievementsContainer.removeAllViews();
        List<GameSettings.Achievement> achievements = settings.getAllAchievements();
        
        int unlockedCount = settings.getUnlockedAchievementsCount();
        achievementsCountText.setText(unlockedCount + "/" + achievements.size());
        
        for (GameSettings.Achievement achievement : achievements) {
            View achievementView = getLayoutInflater().inflate(R.layout.item_achievement, achievementsContainer, false);
            
            TextView iconText = achievementView.findViewById(R.id.achievementIcon);
            TextView titleText = achievementView.findViewById(R.id.achievementTitle);
            TextView descText = achievementView.findViewById(R.id.achievementDescription);
            ImageView lockIcon = achievementView.findViewById(R.id.achievementLock);
            
            iconText.setText(achievement.icon);
            titleText.setText(achievement.title);
            descText.setText(achievement.description);
            
            if (achievement.unlocked) {
                lockIcon.setVisibility(View.GONE);
                achievementView.setAlpha(1.0f);
            } else {
                lockIcon.setVisibility(View.VISIBLE);
                achievementView.setAlpha(0.5f);
            }
            
            achievementsContainer.addView(achievementView);
        }
    }
    
    private void loadHistory() {
        historyContainer.removeAllViews();
        List<GameSettings.GameHistoryEntry> history = settings.getGameHistory();
        
        if (history.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("История пуста");
            emptyText.setTextColor(0xFF757575);
            emptyText.setPadding(16, 32, 16, 32);
            historyContainer.addView(emptyText);
            return;
        }
        
        for (GameSettings.GameHistoryEntry entry : history) {
            View historyView = getLayoutInflater().inflate(R.layout.item_history, historyContainer, false);
            
            TextView dateText = historyView.findViewById(R.id.historyDate);
            TextView modeText = historyView.findViewById(R.id.historyMode);
            TextView resultText = historyView.findViewById(R.id.historyResult);
            TextView statsText = historyView.findViewById(R.id.historyStats);
            ImageView statusIcon = historyView.findViewById(R.id.historyStatusIcon);
            
            dateText.setText(entry.getDateFormatted());
            modeText.setText(entry.getGridSizeFormatted() + " • " + entry.getModeFormatted());
            
            if (entry.won) {
                resultText.setText("Победа!");
                resultText.setTextColor(0xFF4CAF50);
                statusIcon.setImageResource(android.R.drawable.star_big_on);
            } else {
                resultText.setText("Не завершено");
                resultText.setTextColor(0xFFFF5722);
                statusIcon.setImageResource(android.R.drawable.ic_delete);
            }
            
            statsText.setText("Время: " + entry.getTimeFormatted() + " • Ходы: " + entry.moves);
            
            historyContainer.addView(historyView);
        }
    }
    
    private void setupClickListeners() {
        closeButton.setOnClickListener(v -> finish());
        
        resetButton.setOnClickListener(v -> showResetConfirmDialog());
        
        // Редактирование имени игрока
        playerNameText.setOnClickListener(v -> showEditNameDialog());
    }
    
    private void showEditNameDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(settings.getPlayerName());
        input.setSelection(input.getText().length());
        
        new AlertDialog.Builder(this)
            .setTitle("Имя игрока")
            .setView(input)
            .setPositiveButton("Сохранить", (dialog, which) -> {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    settings.setPlayerName(name);
                    playerNameText.setText(name);
                }
            })
            .setNegativeButton("Отмена", null)
            .show();
    }
    
    private void showResetConfirmDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Сброс статистики")
            .setMessage("Вы уверены, что хотите сбросить всю статистику? Это действие нельзя отменить.")
            .setPositiveButton("Сбросить", (dialog, which) -> {
                settings.resetStatistics();
                loadStatistics();
                android.widget.Toast.makeText(this, "Статистика сброшена", android.widget.Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Отмена", null)
            .show();
    }
}
