package com.example.lab1memorygame;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Класс GameStatistics - управление статистикой игры
 * Сохраняет и загружает статистику игр через SharedPreferences
 * Отслеживает общую статистику, статистику по режимам и размерам поля
 */
public class GameStatistics {
    // Имя файла для хранения статистики
    private static final String PREFS_NAME = "MemoryGameStats";
    
    // Ключи для общей статистики
    private static final String KEY_TOTAL_GAMES = "total_games";        // Всего игр
    private static final String KEY_WINS = "wins";                      // Побед
    private static final String KEY_TOTAL_TIME = "total_time";          // Общее время
    private static final String KEY_BEST_TIME = "best_time";            // Лучшее время
    private static final String KEY_LAST_TIME = "last_time";            // Время последней игры
    private static final String KEY_LAST_GAME_DATE = "last_game_date";  // Дата последней игры
    private static final String KEY_MIN_MOVES = "min_moves";            // Минимум ходов
    private static final String KEY_TOTAL_MOVES = "total_moves";        // Всего ходов
    private static final String KEY_TOTAL_PAIRS = "total_pairs";        // Всего пар найдено
    private static final String KEY_WIN_STREAK = "win_streak";          // Текущая серия побед
    private static final String KEY_BEST_STREAK = "best_streak";        // Лучшая серия побед
    
    // Ключи для статистики по режиму "Пары"
    private static final String KEY_PAIRS_GAMES = "pairs_games";        // Игр в режиме пар
    private static final String KEY_PAIRS_BEST = "pairs_best";          // Лучшее время (пары)
    private static final String KEY_PAIRS_AVG = "pairs_avg_time";       // Среднее время (пары)
    private static final String KEY_PAIRS_MOVES = "pairs_min_moves";    // Минимум ходов (пары)
    
    // Ключи для статистики по режиму "Тройки"
    private static final String KEY_TRIPLES_GAMES = "triples_games";
    private static final String KEY_TRIPLES_BEST = "triples_best";
    private static final String KEY_TRIPLES_AVG = "triples_avg_time";
    private static final String KEY_TRIPLES_MOVES = "triples_min_moves";
    
    // Ключи для статистики по режиму "Гибрид"
    private static final String KEY_HYBRID_GAMES = "hybrid_games";
    private static final String KEY_HYBRID_BEST = "hybrid_best";
    private static final String KEY_HYBRID_AVG = "hybrid_avg_time";
    private static final String KEY_HYBRID_MOVES = "hybrid_min_moves";
    
    // Ключи для статистики по размеру поля 4x4
    private static final String KEY_4X4_GAMES = "4x4_games";
    private static final String KEY_4X4_BEST = "4x4_best";
    private static final String KEY_4X4_MOVES = "4x4_min_moves";
    
    // Ключи для статистики по размеру поля 4x5
    private static final String KEY_4X5_GAMES = "4x5_games";
    private static final String KEY_4X5_BEST = "4x5_best";
    private static final String KEY_4X5_MOVES = "4x5_min_moves";
    
    // Ключи для статистики по размеру поля 6x6
    private static final String KEY_6X6_GAMES = "6x6_games";
    private static final String KEY_6X6_BEST = "6x6_best";
    private static final String KEY_6X6_MOVES = "6x6_min_moves";

    // Объект для работы с SharedPreferences
    private SharedPreferences prefs;

    /**
     * Конструктор - инициализирует SharedPreferences
     * @param context - контекст приложения
     */
    public GameStatistics(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Сохраняет результат завершённой игры
     * @param timeSeconds - время игры в секундах
     * @param mode - режим игры ("pairs", "triples", "hybrid")
     * @param moves - количество ходов
     * @param gridRows - строк в поле
     * @param gridColumns - столбцов в поле
     */
    public void saveGameResult(int timeSeconds, String mode, int moves, int gridRows, int gridColumns) {
        SharedPreferences.Editor editor = prefs.edit();
        
        // Обновляем общую статистику
        int totalGames = prefs.getInt(KEY_TOTAL_GAMES, 0);
        editor.putInt(KEY_TOTAL_GAMES, totalGames + 1);
        editor.putInt(KEY_WINS, prefs.getInt(KEY_WINS, 0) + 1);
        
        // Обновляем общее время
        int totalTime = prefs.getInt(KEY_TOTAL_TIME, 0);
        editor.putInt(KEY_TOTAL_TIME, totalTime + timeSeconds);
        
        // Обновляем лучшее время
        int bestTime = prefs.getInt(KEY_BEST_TIME, Integer.MAX_VALUE);
        if (timeSeconds < bestTime) {
            editor.putInt(KEY_BEST_TIME, timeSeconds);
        }
        
        editor.putInt(KEY_LAST_TIME, timeSeconds);
        editor.putString(KEY_LAST_GAME_DATE, getCurrentDate());
        
        // Обновляем минимальное количество ходов
        int minMoves = prefs.getInt(KEY_MIN_MOVES, Integer.MAX_VALUE);
        if (moves < minMoves) {
            editor.putInt(KEY_MIN_MOVES, moves);
        }
        
        // Обновляем общее количество ходов
        int totalMoves = prefs.getInt(KEY_TOTAL_MOVES, 0);
        editor.putInt(KEY_TOTAL_MOVES, totalMoves + moves);
        
        // Обновляем серию побед
        int winStreak = prefs.getInt(KEY_WIN_STREAK, 0) + 1;
        editor.putInt(KEY_WIN_STREAK, winStreak);
        
        int bestStreak = prefs.getInt(KEY_BEST_STREAK, 0);
        if (winStreak > bestStreak) {
            editor.putInt(KEY_BEST_STREAK, winStreak);
        }
        
        // Обновляем статистику по режимам
        updateModeStats(editor, mode, timeSeconds, moves);
        
        // Обновляем статистику по размеру поля
        updateGridStats(editor, gridRows, gridColumns, timeSeconds, moves);
        
        editor.apply();
    }
    
    /**
     * Обновляет статистику для конкретного режима игры
     * @param editor - редактор SharedPreferences
     * @param mode - режим игры
     * @param timeSeconds - время игры
     * @param moves - количество ходов
     */
    private void updateModeStats(SharedPreferences.Editor editor, String mode, int timeSeconds, int moves) {
        switch (mode) {
            case "pairs":
                int pairsGames = prefs.getInt(KEY_PAIRS_GAMES, 0);
                editor.putInt(KEY_PAIRS_GAMES, pairsGames + 1);
                
                int pairsBest = prefs.getInt(KEY_PAIRS_BEST, Integer.MAX_VALUE);
                if (timeSeconds < pairsBest) {
                    editor.putInt(KEY_PAIRS_BEST, timeSeconds);
                }
                
                int pairsAvg = calculateAverage(KEY_PAIRS_AVG, timeSeconds, pairsGames);
                editor.putInt(KEY_PAIRS_AVG, pairsAvg);
                
                int pairsMoves = prefs.getInt(KEY_PAIRS_MOVES, Integer.MAX_VALUE);
                if (moves < pairsMoves) {
                    editor.putInt(KEY_PAIRS_MOVES, moves);
                }
                break;
                
            case "triples":
                int triplesGames = prefs.getInt(KEY_TRIPLES_GAMES, 0);
                editor.putInt(KEY_TRIPLES_GAMES, triplesGames + 1);
                
                int triplesBest = prefs.getInt(KEY_TRIPLES_BEST, Integer.MAX_VALUE);
                if (timeSeconds < triplesBest) {
                    editor.putInt(KEY_TRIPLES_BEST, timeSeconds);
                }
                
                int triplesAvg = calculateAverage(KEY_TRIPLES_AVG, timeSeconds, triplesGames);
                editor.putInt(KEY_TRIPLES_AVG, triplesAvg);
                
                int triplesMoves = prefs.getInt(KEY_TRIPLES_MOVES, Integer.MAX_VALUE);
                if (moves < triplesMoves) {
                    editor.putInt(KEY_TRIPLES_MOVES, moves);
                }
                break;
                
            case "hybrid":
                int hybridGames = prefs.getInt(KEY_HYBRID_GAMES, 0);
                editor.putInt(KEY_HYBRID_GAMES, hybridGames + 1);
                
                int hybridBest = prefs.getInt(KEY_HYBRID_BEST, Integer.MAX_VALUE);
                if (timeSeconds < hybridBest) {
                    editor.putInt(KEY_HYBRID_BEST, timeSeconds);
                }
                
                int hybridAvg = calculateAverage(KEY_HYBRID_AVG, timeSeconds, hybridGames);
                editor.putInt(KEY_HYBRID_AVG, hybridAvg);
                
                int hybridMoves = prefs.getInt(KEY_HYBRID_MOVES, Integer.MAX_VALUE);
                if (moves < hybridMoves) {
                    editor.putInt(KEY_HYBRID_MOVES, moves);
                }
                break;
        }
    }
    
    /**
     * Обновляет статистику для конкретного размера поля
     * @param editor - редактор SharedPreferences
     * @param rows - количество строк
     * @param columns - количество столбцов
     * @param timeSeconds - время игры
     * @param moves - количество ходов
     */
    private void updateGridStats(SharedPreferences.Editor editor, int rows, int columns, int timeSeconds, int moves) {
        String gridKey = rows + "x" + columns;
        
        if ("4x4".equals(gridKey)) {
            int games = prefs.getInt(KEY_4X4_GAMES, 0);
            editor.putInt(KEY_4X4_GAMES, games + 1);
            
            int best = prefs.getInt(KEY_4X4_BEST, Integer.MAX_VALUE);
            if (timeSeconds < best) {
                editor.putInt(KEY_4X4_BEST, timeSeconds);
            }
            
            int minMoves = prefs.getInt(KEY_4X4_MOVES, Integer.MAX_VALUE);
            if (moves < minMoves) {
                editor.putInt(KEY_4X4_MOVES, moves);
            }
        } else if ("4x5".equals(gridKey) || "5x4".equals(gridKey)) {
            int games = prefs.getInt(KEY_4X5_GAMES, 0);
            editor.putInt(KEY_4X5_GAMES, games + 1);
            
            int best = prefs.getInt(KEY_4X5_BEST, Integer.MAX_VALUE);
            if (timeSeconds < best) {
                editor.putInt(KEY_4X5_BEST, timeSeconds);
            }
            
            int minMoves = prefs.getInt(KEY_4X5_MOVES, Integer.MAX_VALUE);
            if (moves < minMoves) {
                editor.putInt(KEY_4X5_MOVES, moves);
            }
        } else if ("6x6".equals(gridKey)) {
            int games = prefs.getInt(KEY_6X6_GAMES, 0);
            editor.putInt(KEY_6X6_GAMES, games + 1);
            
            int best = prefs.getInt(KEY_6X6_BEST, Integer.MAX_VALUE);
            if (timeSeconds < best) {
                editor.putInt(KEY_6X6_BEST, timeSeconds);
            }
            
            int minMoves = prefs.getInt(KEY_6X6_MOVES, Integer.MAX_VALUE);
            if (moves < minMoves) {
                editor.putInt(KEY_6X6_MOVES, moves);
            }
        }
    }
    
    /**
     * Вычисляет новое среднее значение
     * @param key - ключ для текущего среднего
     * @param newValue - новое значение
     * @param gamesCount - количество предыдущих игр
     * @return новое среднее значение
     */
    private int calculateAverage(String key, int newValue, int gamesCount) {
        int currentAvg = prefs.getInt(key, 0);
        return (currentAvg * gamesCount + newValue) / (gamesCount + 1);
    }
    
    /**
     * Получает текущую дату в формате dd.MM.yyyy
     * @return строка с датой
     */
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Геттеры для общей статистики
    public int getTotalGames() { return prefs.getInt(KEY_TOTAL_GAMES, 0); }
    public int getWins() { return prefs.getInt(KEY_WINS, 0); }
    public int getTotalTime() { return prefs.getInt(KEY_TOTAL_TIME, 0); }
    public int getBestTime() { return prefs.getInt(KEY_BEST_TIME, Integer.MAX_VALUE); }
    public int getLastTime() { return prefs.getInt(KEY_LAST_TIME, 0); }
    public String getLastGameDate() { return prefs.getString(KEY_LAST_GAME_DATE, "—"); }
    public int getMinMoves() { return prefs.getInt(KEY_MIN_MOVES, Integer.MAX_VALUE); }
    
    /**
     * Вычисляет среднее количество ходов
     * @return среднее количество ходов за все игры
     */
    public int getAverageMoves() {
        int totalGames = getTotalGames();
        if (totalGames == 0) return 0;
        return prefs.getInt(KEY_TOTAL_MOVES, 0) / totalGames;
    }
    
    public int getTotalPairs() { return prefs.getInt(KEY_TOTAL_PAIRS, 0); }
    public int getWinStreak() { return prefs.getInt(KEY_WIN_STREAK, 0); }
    public int getBestStreak() { return prefs.getInt(KEY_BEST_STREAK, 0); }
    
    /**
     * Вычисляет процент побед
     * @return процент побед (0-100)
     */
    public int getWinRate() {
        int totalGames = getTotalGames();
        if (totalGames == 0) return 0;
        return (getWins() * 100) / totalGames;
    }
    
    /**
     * Вычисляет среднее время игры
     * @return среднее время в секундах
     */
    public int getAverageTime() {
        int totalGames = getTotalGames();
        if (totalGames == 0) return 0;
        return getTotalTime() / totalGames;
    }

    // Геттеры для статистики по режимам
    public int getPairsGames() { return prefs.getInt(KEY_PAIRS_GAMES, 0); }
    public int getPairsBest() { return prefs.getInt(KEY_PAIRS_BEST, Integer.MAX_VALUE); }
    public int getPairsAverage() { return prefs.getInt(KEY_PAIRS_AVG, 0); }
    public int getPairsMoves() { return prefs.getInt(KEY_PAIRS_MOVES, Integer.MAX_VALUE); }

    public int getTriplesGames() { return prefs.getInt(KEY_TRIPLES_GAMES, 0); }
    public int getTriplesBest() { return prefs.getInt(KEY_TRIPLES_BEST, Integer.MAX_VALUE); }
    public int getTriplesAverage() { return prefs.getInt(KEY_TRIPLES_AVG, 0); }
    public int getTriplesMoves() { return prefs.getInt(KEY_TRIPLES_MOVES, Integer.MAX_VALUE); }

    public int getHybridGames() { return prefs.getInt(KEY_HYBRID_GAMES, 0); }
    public int getHybridBest() { return prefs.getInt(KEY_HYBRID_BEST, Integer.MAX_VALUE); }
    public int getHybridAverage() { return prefs.getInt(KEY_HYBRID_AVG, 0); }
    public int getHybridMoves() { return prefs.getInt(KEY_HYBRID_MOVES, Integer.MAX_VALUE); }
    
    // Геттеры для статистики по размерам поля
    public int get4x4Games() { return prefs.getInt(KEY_4X4_GAMES, 0); }
    public int get4x4Best() { return prefs.getInt(KEY_4X4_BEST, Integer.MAX_VALUE); }
    public int get4x4Moves() { return prefs.getInt(KEY_4X4_MOVES, Integer.MAX_VALUE); }
    
    public int get4x5Games() { return prefs.getInt(KEY_4X5_GAMES, 0); }
    public int get4x5Best() { return prefs.getInt(KEY_4X5_BEST, Integer.MAX_VALUE); }
    public int get4x5Moves() { return prefs.getInt(KEY_4X5_MOVES, Integer.MAX_VALUE); }
    
    public int get6x6Games() { return prefs.getInt(KEY_6X6_GAMES, 0); }
    public int get6x6Best() { return prefs.getInt(KEY_6X6_BEST, Integer.MAX_VALUE); }
    public int get6x6Moves() { return prefs.getInt(KEY_6X6_MOVES, Integer.MAX_VALUE); }

    /**
     * Сбрасывает всю статистику
     */
    public void resetStatistics() {
        prefs.edit().clear().apply();
    }

    /**
     * Форматирует время в формат MM:SS
     * @param seconds - время в секундах
     * @return строка в формате "MM:SS" или "—" если нет данных
     */
    public String formatTime(int seconds) {
        if (seconds == Integer.MAX_VALUE || seconds == 0) {
            return "—";
        }
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
    }
}
