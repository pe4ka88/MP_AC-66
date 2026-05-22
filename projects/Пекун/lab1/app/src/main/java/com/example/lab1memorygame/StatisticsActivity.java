package com.example.lab1memorygame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

/**
 * StatisticsActivity - экран статистики игры
 * Отображает детальную статистику по всем играм, режимам и размерам поля
 */
public class StatisticsActivity extends AppCompatActivity {

    // Элементы интерфейса для отображения статистики
    private TextView tvLastGameDate;      // Дата последней игры
    private TextView tvTotalGames;        // Всего игр
    private TextView tvWinRate;           // Процент побед
    private TextView tvBestTime;          // Лучшее время
    private TextView tvMinMoves;          // Минимум ходов
    private TextView tvAverageTime;       // Среднее время
    private TextView tvBestStreak;        // Лучшая серия побед
    private TextView tvStatsPairs;        // Статистика режима "Пары"
    private TextView tvStatsTriples;      // Статистика режима "Тройки"
    private TextView tvStatsHybrid;       // Статистика режима "Гибрид"
    private TextView tv4x4Stats;          // Статистика поля 4x4
    private TextView tv4x5Stats;          // Статистика поля 4x5
    private TextView tv6x6Stats;          // Статистика поля 6x6
    private Button btnResetStats;         // Кнопка сброса статистики
    private Button btnBack;               // Кнопка возврата

    // Объект для работы со статистикой
    private GameStatistics statistics;

    /**
     * Метод onCreate - инициализация экрана статистики
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statistics = new GameStatistics(this);

        // Инициализация всех элементов интерфейса
        tvLastGameDate = findViewById(R.id.tvLastGameDate);
        tvTotalGames = findViewById(R.id.tvTotalGames);
        tvWinRate = findViewById(R.id.tvWinRate);
        tvBestTime = findViewById(R.id.tvBestTime);
        tvMinMoves = findViewById(R.id.tvMinMoves);
        tvAverageTime = findViewById(R.id.tvAverageTime);
        tvBestStreak = findViewById(R.id.tvBestStreak);
        tvStatsPairs = findViewById(R.id.tvStatsPairs);
        tvStatsTriples = findViewById(R.id.tvStatsTriples);
        tvStatsHybrid = findViewById(R.id.tvStatsHybrid);
        tv4x4Stats = findViewById(R.id.tv4x4Stats);
        tv4x5Stats = findViewById(R.id.tv4x5Stats);
        tv6x6Stats = findViewById(R.id.tv6x6Stats);
        btnResetStats = findViewById(R.id.btnResetStats);
        btnBack = findViewById(R.id.btnBack);

        // Загружаем статистику из SharedPreferences
        loadStatistics();

        // Обработчик кнопки сброса статистики
        btnResetStats.setOnClickListener(v -> showResetConfirmation());
        
        // Обработчик кнопки возврата
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Загружает и отображает всю статистику
     */
    private void loadStatistics() {
        // Профиль игрока - дата последней игры
        String lastDate = statistics.getLastGameDate();
        tvLastGameDate.setText("Последняя игра: " + lastDate);

        // Общая статистика
        tvTotalGames.setText(String.valueOf(statistics.getTotalGames()));
        
        int winRate = statistics.getWinRate();
        tvWinRate.setText(winRate + "%");

        // Рекорды
        tvBestTime.setText(statistics.formatTime(statistics.getBestTime()));
        
        int minMoves = statistics.getMinMoves();
        tvMinMoves.setText(minMoves == Integer.MAX_VALUE ? "—" : String.valueOf(minMoves));
        
        tvAverageTime.setText(statistics.formatTime(statistics.getAverageTime()));
        tvBestStreak.setText(String.valueOf(statistics.getBestStreak()));

        // Статистика по режимам
        loadModeStats();
        
        // Статистика по размерам поля
        loadGridStats();
    }

    /**
     * Загружает статистику по всем режимам игры (пары, тройки, гибрид)
     */
    private void loadModeStats() {
        // Режим пар
        int pairsGames = statistics.getPairsGames();
        if (pairsGames > 0) {
            String pairsStats = String.format(Locale.getDefault(),
                    "Игр: %d | Лучшее: %s | Среднее: %s",
                    pairsGames,
                    statistics.formatTime(statistics.getPairsBest()),
                    statistics.formatTime(statistics.getPairsAverage()));
            tvStatsPairs.setText(pairsStats);
        } else {
            tvStatsPairs.setText("Нет данных");
        }

        // Режим троек
        int triplesGames = statistics.getTriplesGames();
        if (triplesGames > 0) {
            String triplesStats = String.format(Locale.getDefault(),
                    "Игр: %d | Лучшее: %s | Среднее: %s",
                    triplesGames,
                    statistics.formatTime(statistics.getTriplesBest()),
                    statistics.formatTime(statistics.getTriplesAverage()));
            tvStatsTriples.setText(triplesStats);
        } else {
            tvStatsTriples.setText("Нет данных");
        }

        // Гибридный режим
        int hybridGames = statistics.getHybridGames();
        if (hybridGames > 0) {
            String hybridStats = String.format(Locale.getDefault(),
                    "Игр: %d | Лучшее: %s | Среднее: %s",
                    hybridGames,
                    statistics.formatTime(statistics.getHybridBest()),
                    statistics.formatTime(statistics.getHybridAverage()));
            tvStatsHybrid.setText(hybridStats);
        } else {
            tvStatsHybrid.setText("Нет данных");
        }
    }

    /**
     * Загружает статистику по всем размерам поля (4x4, 4x5, 6x6)
     */
    private void loadGridStats() {
        // Поле 4x4
        int games4x4 = statistics.get4x4Games();
        if (games4x4 > 0) {
            String stats = String.format(Locale.getDefault(),
                    "%d игр\n%s",
                    games4x4,
                    statistics.formatTime(statistics.get4x4Best()));
            tv4x4Stats.setText(stats);
        } else {
            tv4x4Stats.setText("0 игр\n—");
        }

        // Поле 4x5
        int games4x5 = statistics.get4x5Games();
        if (games4x5 > 0) {
            String stats = String.format(Locale.getDefault(),
                    "%d игр\n%s",
                    games4x5,
                    statistics.formatTime(statistics.get4x5Best()));
            tv4x5Stats.setText(stats);
        } else {
            tv4x5Stats.setText("0 игр\n—");
        }

        // Поле 6x6
        int games6x6 = statistics.get6x6Games();
        if (games6x6 > 0) {
            String stats = String.format(Locale.getDefault(),
                    "%d игр\n%s",
                    games6x6,
                    statistics.formatTime(statistics.get6x6Best()));
            tv6x6Stats.setText(stats);
        } else {
            tv6x6Stats.setText("0 игр\n—");
        }
    }

    /**
     * Показывает диалог подтверждения сброса статистики
     */
    private void showResetConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.btn_reset_stats)
                .setMessage(R.string.stats_reset_confirm)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    statistics.resetStatistics();
                    loadStatistics();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
