package com.example.lab1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameSettings {
    private static final String PREFS_NAME = "MemoryGamePrefs";
    private static final String KEY_GRID_ROWS = "gridRows";
    private static final String KEY_GRID_COLS = "gridCols";
    private static final String KEY_GRID_SIZE = "gridSize"; // Deprecated, kept for compatibility
    private static final String KEY_GAMES_PLAYED = "gamesPlayed";
    private static final String KEY_BEST_TIME = "bestTime";
    private static final String KEY_BEST_MOVES = "bestMoves";
    private static final String KEY_MATCH_COUNT = "matchCount"; // 2 для пар, 3 для троек
    private static final String KEY_CARD_SET = "cardSet"; // 0-shapes, 1-animals, 2-fruits, 3-emojis
    private static final String KEY_COLOR_SCHEME = "colorScheme"; // 0-blue, 1-green, 2-purple, 3-orange, 4-pink
    private static final String KEY_MIXED_MODE = "mixedMode"; // true - смешанный режим (пары + тройки)
    
    // Расширенная статистика
    private static final String KEY_TOTAL_PLAY_TIME = "totalPlayTime"; // Общее время игры в мс
    private static final String KEY_GAMES_WON = "gamesWon"; // Количество побед
    private static final String KEY_WIN_STREAK = "winStreak"; // Текущая серия побед
    private static final String KEY_BEST_WIN_STREAK = "bestWinStreak"; // Лучшая серия побед
    private static final String KEY_TOTAL_MOVES = "totalMoves"; // Общее количество ходов
    private static final String KEY_TOTAL_PAIRS_FOUND = "totalPairsFound"; // Всего найдено пар
    private static final String KEY_GAME_HISTORY = "gameHistory"; // История последних игр (JSON)
    private static final String KEY_LAST_PLAYED = "lastPlayed"; // Дата последней игры
    private static final String KEY_PLAYER_NAME = "playerName"; // Имя игрока
    
    // Статистика по размерам поля
    private static final String KEY_STATS_4X4_GAMES = "stats4x4Games";
    private static final String KEY_STATS_4X4_BEST_TIME = "stats4x4BestTime";
    private static final String KEY_STATS_4X4_BEST_MOVES = "stats4x4BestMoves";
    private static final String KEY_STATS_6X6_GAMES = "stats6x6Games";
    private static final String KEY_STATS_6X6_BEST_TIME = "stats6x6BestTime";
    private static final String KEY_STATS_6X6_BEST_MOVES = "stats6x6BestMoves";
    private static final String KEY_STATS_CUSTOM_GAMES = "statsCustomGames";
    private static final String KEY_STATS_CUSTOM_BEST_TIME = "statsCustomBestTime";
    private static final String KEY_STATS_CUSTOM_BEST_MOVES = "statsCustomBestMoves";
    
    // Статистика по режимам
    private static final String KEY_STATS_PAIRS_GAMES = "statsPairsGames";
    private static final String KEY_STATS_PAIRS_BEST_TIME = "statsPairsBestTime";
    private static final String KEY_STATS_TRIPLETS_GAMES = "statsTripletsGames";
    private static final String KEY_STATS_TRIPLETS_BEST_TIME = "statsTripletsBestTime";
    private static final String KEY_STATS_MIXED_GAMES = "statsMixedGames";
    private static final String KEY_STATS_MIXED_BEST_TIME = "statsMixedBestTime";
    
    // Достижения
    private static final String KEY_ACHIEVEMENT_FIRST_WIN = "achievementFirstWin";
    private static final String KEY_ACHIEVEMENT_SPEED_DEMON = "achievementSpeedDemon"; // Победа за 30 сек
    private static final String KEY_ACHIEVEMENT_PERFECTIONIST = "achievementPerfectionist"; // Минимум ходов
    private static final String KEY_ACHIEVEMENT_MARATHON = "achievementMarathon"; // 10 игр за день
    private static final String KEY_ACHIEVEMENT_STREAK_MASTER = "achievementStreakMaster"; // 5 побед подряд
    
    private static final int MAX_HISTORY_SIZE = 20; // Максимум записей в истории
    
    // Константы наборов карт
    public static final int CARD_SET_SHAPES = 0;
    public static final int CARD_SET_ANIMALS = 1;
    public static final int CARD_SET_FRUITS = 2;
    public static final int CARD_SET_EMOJIS = 3;
    
    // Константы цветовых схем
    public static final int COLOR_BLUE = 0;
    public static final int COLOR_GREEN = 1;
    public static final int COLOR_PURPLE = 2;
    public static final int COLOR_ORANGE = 3;
    public static final int COLOR_PINK = 4;
    
    private SharedPreferences prefs;
    
    public GameSettings(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    // Размер поля - строки
    public void setGridRows(int rows) {
        prefs.edit().putInt(KEY_GRID_ROWS, rows).apply();
    }
    
    public int getGridRows() {
        return prefs.getInt(KEY_GRID_ROWS, 4); // По умолчанию 4
    }
    
    // Размер поля - столбцы
    public void setGridCols(int cols) {
        prefs.edit().putInt(KEY_GRID_COLS, cols).apply();
    }
    
    public int getGridCols() {
        return prefs.getInt(KEY_GRID_COLS, 4); // По умолчанию 4
    }
    
    // Совместимость со старым API
    public void setGridSize(int size) {
        prefs.edit()
            .putInt(KEY_GRID_SIZE, size)
            .putInt(KEY_GRID_ROWS, size)
            .putInt(KEY_GRID_COLS, size)
            .apply();
    }
    
    public int getGridSize() {
        return prefs.getInt(KEY_GRID_SIZE, 4); // По умолчанию 4x4
    }
    
    // Режим игры (пары/тройки)
    public void setMatchCount(int count) {
        prefs.edit().putInt(KEY_MATCH_COUNT, count).apply();
    }
    
    public int getMatchCount() {
        return prefs.getInt(KEY_MATCH_COUNT, 2); // По умолчанию пары
    }
    
    // Смешанный режим (пары + тройки)
    public void setMixedMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_MIXED_MODE, enabled).apply();
    }
    
    public boolean isMixedMode() {
        return prefs.getBoolean(KEY_MIXED_MODE, false); // По умолчанию выключен
    }
    
    // Набор картинок
    public void setCardSet(int cardSet) {
        prefs.edit().putInt(KEY_CARD_SET, cardSet).apply();
    }
    
    public int getCardSet() {
        return prefs.getInt(KEY_CARD_SET, CARD_SET_SHAPES); // По умолчанию фигуры
    }
    
    // Цветовая схема
    public void setColorScheme(int colorScheme) {
        prefs.edit().putInt(KEY_COLOR_SCHEME, colorScheme).apply();
    }
    
    public int getColorScheme() {
        return prefs.getInt(KEY_COLOR_SCHEME, COLOR_BLUE); // По умолчанию синяя
    }
    
    // Получение основного цвета текущей схемы
    public int getPrimaryColor() {
        switch (getColorScheme()) {
            case COLOR_GREEN: return Color.parseColor("#388E3C");
            case COLOR_PURPLE: return Color.parseColor("#7B1FA2");
            case COLOR_ORANGE: return Color.parseColor("#F57C00");
            case COLOR_PINK: return Color.parseColor("#C2185B");
            case COLOR_BLUE:
            default: return Color.parseColor("#1976D2");
        }
    }
    
    // Получение тёмного оттенка текущей схемы
    public int getPrimaryDarkColor() {
        switch (getColorScheme()) {
            case COLOR_GREEN: return Color.parseColor("#1B5E20");
            case COLOR_PURPLE: return Color.parseColor("#4A148C");
            case COLOR_ORANGE: return Color.parseColor("#E65100");
            case COLOR_PINK: return Color.parseColor("#880E4F");
            case COLOR_BLUE:
            default: return Color.parseColor("#0D47A1");
        }
    }
    
    // Получение светлого фонового цвета текущей схемы
    public int getBackgroundColor() {
        switch (getColorScheme()) {
            case COLOR_GREEN: return Color.parseColor("#E8F5E9");
            case COLOR_PURPLE: return Color.parseColor("#F3E5F5");
            case COLOR_ORANGE: return Color.parseColor("#FFF3E0");
            case COLOR_PINK: return Color.parseColor("#FCE4EC");
            case COLOR_BLUE:
            default: return Color.parseColor("#E3F2FD");
        }
    }
    
    // Получение цвета текста для текущей схемы
    public int getTextColor() {
        switch (getColorScheme()) {
            case COLOR_GREEN: return Color.parseColor("#1B5E20");
            case COLOR_PURPLE: return Color.parseColor("#4A148C");
            case COLOR_ORANGE: return Color.parseColor("#E65100");
            case COLOR_PINK: return Color.parseColor("#880E4F");
            case COLOR_BLUE:
            default: return Color.parseColor("#1976D2");
        }
    }
    
    // Статистика
    public void incrementGamesPlayed() {
        int count = prefs.getInt(KEY_GAMES_PLAYED, 0);
        prefs.edit().putInt(KEY_GAMES_PLAYED, count + 1).apply();
    }
    
    public int getGamesPlayed() {
        return prefs.getInt(KEY_GAMES_PLAYED, 0);
    }
    
    public void updateBestTime(long time) {
        long bestTime = prefs.getLong(KEY_BEST_TIME, Long.MAX_VALUE);
        if (time < bestTime) {
            prefs.edit().putLong(KEY_BEST_TIME, time).apply();
        }
    }
    
    public long getBestTime() {
        return prefs.getLong(KEY_BEST_TIME, Long.MAX_VALUE);
    }
    
    public void updateBestMoves(int moves) {
        int bestMoves = prefs.getInt(KEY_BEST_MOVES, Integer.MAX_VALUE);
        if (moves < bestMoves) {
            prefs.edit().putInt(KEY_BEST_MOVES, moves).apply();
        }
    }
    
    public int getBestMoves() {
        return prefs.getInt(KEY_BEST_MOVES, Integer.MAX_VALUE);
    }
    
    // Сброс настроек к значениям по умолчанию
    public void resetToDefaults() {
        prefs.edit()
            .putInt(KEY_GRID_ROWS, 4)
            .putInt(KEY_GRID_COLS, 4)
            .putInt(KEY_GRID_SIZE, 4)
            .putInt(KEY_MATCH_COUNT, 2)
            .putBoolean(KEY_MIXED_MODE, false)
            .putInt(KEY_CARD_SET, CARD_SET_SHAPES)
            .putInt(KEY_COLOR_SCHEME, COLOR_BLUE)
            .apply();
    }
    
    // Сброс статистики
    public void resetStatistics() {
        prefs.edit()
            .putInt(KEY_GAMES_PLAYED, 0)
            .putLong(KEY_BEST_TIME, Long.MAX_VALUE)
            .putInt(KEY_BEST_MOVES, Integer.MAX_VALUE)
            .putLong(KEY_TOTAL_PLAY_TIME, 0)
            .putInt(KEY_GAMES_WON, 0)
            .putInt(KEY_WIN_STREAK, 0)
            .putInt(KEY_BEST_WIN_STREAK, 0)
            .putInt(KEY_TOTAL_MOVES, 0)
            .putInt(KEY_TOTAL_PAIRS_FOUND, 0)
            .putString(KEY_GAME_HISTORY, "[]")
            .putInt(KEY_STATS_4X4_GAMES, 0)
            .putLong(KEY_STATS_4X4_BEST_TIME, Long.MAX_VALUE)
            .putInt(KEY_STATS_4X4_BEST_MOVES, Integer.MAX_VALUE)
            .putInt(KEY_STATS_6X6_GAMES, 0)
            .putLong(KEY_STATS_6X6_BEST_TIME, Long.MAX_VALUE)
            .putInt(KEY_STATS_6X6_BEST_MOVES, Integer.MAX_VALUE)
            .putInt(KEY_STATS_CUSTOM_GAMES, 0)
            .putLong(KEY_STATS_CUSTOM_BEST_TIME, Long.MAX_VALUE)
            .putInt(KEY_STATS_CUSTOM_BEST_MOVES, Integer.MAX_VALUE)
            .putInt(KEY_STATS_PAIRS_GAMES, 0)
            .putLong(KEY_STATS_PAIRS_BEST_TIME, Long.MAX_VALUE)
            .putInt(KEY_STATS_TRIPLETS_GAMES, 0)
            .putLong(KEY_STATS_TRIPLETS_BEST_TIME, Long.MAX_VALUE)
            .putInt(KEY_STATS_MIXED_GAMES, 0)
            .putLong(KEY_STATS_MIXED_BEST_TIME, Long.MAX_VALUE)
            .putBoolean(KEY_ACHIEVEMENT_FIRST_WIN, false)
            .putBoolean(KEY_ACHIEVEMENT_SPEED_DEMON, false)
            .putBoolean(KEY_ACHIEVEMENT_PERFECTIONIST, false)
            .putBoolean(KEY_ACHIEVEMENT_MARATHON, false)
            .putBoolean(KEY_ACHIEVEMENT_STREAK_MASTER, false)
            .apply();
    }
    
    // ==================== РАСШИРЕННАЯ СТАТИСТИКА ====================
    
    // Имя игрока
    public void setPlayerName(String name) {
        prefs.edit().putString(KEY_PLAYER_NAME, name).apply();
    }
    
    public String getPlayerName() {
        return prefs.getString(KEY_PLAYER_NAME, "Игрок");
    }
    
    // Общее время игры
    public void addPlayTime(long timeMs) {
        long total = prefs.getLong(KEY_TOTAL_PLAY_TIME, 0);
        prefs.edit().putLong(KEY_TOTAL_PLAY_TIME, total + timeMs).apply();
    }
    
    public long getTotalPlayTime() {
        return prefs.getLong(KEY_TOTAL_PLAY_TIME, 0);
    }
    
    public String getTotalPlayTimeFormatted() {
        long totalMs = getTotalPlayTime();
        long seconds = totalMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;
        
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%dч %02dм %02dс", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format(Locale.getDefault(), "%dм %02dс", minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%dс", seconds);
        }
    }
    
    // Количество побед
    public void incrementGamesWon() {
        int count = prefs.getInt(KEY_GAMES_WON, 0);
        prefs.edit().putInt(KEY_GAMES_WON, count + 1).apply();
    }
    
    public int getGamesWon() {
        return prefs.getInt(KEY_GAMES_WON, 0);
    }
    
    // Серия побед
    public void incrementWinStreak() {
        int streak = prefs.getInt(KEY_WIN_STREAK, 0) + 1;
        int bestStreak = prefs.getInt(KEY_BEST_WIN_STREAK, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_WIN_STREAK, streak);
        if (streak > bestStreak) {
            editor.putInt(KEY_BEST_WIN_STREAK, streak);
        }
        editor.apply();
        
        // Проверка достижения
        if (streak >= 5) {
            unlockAchievement(KEY_ACHIEVEMENT_STREAK_MASTER);
        }
    }
    
    public void resetWinStreak() {
        prefs.edit().putInt(KEY_WIN_STREAK, 0).apply();
    }
    
    public int getWinStreak() {
        return prefs.getInt(KEY_WIN_STREAK, 0);
    }
    
    public int getBestWinStreak() {
        return prefs.getInt(KEY_BEST_WIN_STREAK, 0);
    }
    
    // Общее количество ходов
    public void addMoves(int moves) {
        int total = prefs.getInt(KEY_TOTAL_MOVES, 0);
        prefs.edit().putInt(KEY_TOTAL_MOVES, total + moves).apply();
    }
    
    public int getTotalMoves() {
        return prefs.getInt(KEY_TOTAL_MOVES, 0);
    }
    
    // Всего найдено пар
    public void addPairsFound(int pairs) {
        int total = prefs.getInt(KEY_TOTAL_PAIRS_FOUND, 0);
        prefs.edit().putInt(KEY_TOTAL_PAIRS_FOUND, total + pairs).apply();
    }
    
    public int getTotalPairsFound() {
        return prefs.getInt(KEY_TOTAL_PAIRS_FOUND, 0);
    }
    
    // Дата последней игры
    public void updateLastPlayed() {
        prefs.edit().putLong(KEY_LAST_PLAYED, System.currentTimeMillis()).apply();
    }
    
    public long getLastPlayed() {
        return prefs.getLong(KEY_LAST_PLAYED, 0);
    }
    
    public String getLastPlayedFormatted() {
        long lastPlayed = getLastPlayed();
        if (lastPlayed == 0) {
            return "Никогда";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(lastPlayed));
    }
    
    // Средняя статистика
    public double getAverageTime() {
        int gamesWon = getGamesWon();
        if (gamesWon == 0) return 0;
        return (double) getTotalPlayTime() / gamesWon;
    }
    
    public String getAverageTimeFormatted() {
        double avgMs = getAverageTime();
        if (avgMs == 0) return "—";
        int seconds = (int) (avgMs / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    
    public double getAverageMoves() {
        int gamesWon = getGamesWon();
        if (gamesWon == 0) return 0;
        return (double) getTotalMoves() / gamesWon;
    }
    
    public String getAverageMovesFormatted() {
        double avg = getAverageMoves();
        if (avg == 0) return "—";
        return String.format(Locale.getDefault(), "%.1f", avg);
    }
    
    // Процент побед
    public double getWinRate() {
        int played = getGamesPlayed();
        if (played == 0) return 0;
        return (double) getGamesWon() / played * 100;
    }
    
    public String getWinRateFormatted() {
        return String.format(Locale.getDefault(), "%.1f%%", getWinRate());
    }
    
    // ==================== СТАТИСТИКА ПО РАЗМЕРАМ ====================
    
    public void updateGridSizeStats(int rows, int cols, long time, int moves) {
        String sizeKey = getGridSizeKey(rows, cols);
        
        // Увеличиваем счетчик игр
        int gamesKey = getStatGamesKey(sizeKey);
        int games = prefs.getInt(gamesKey + "_GAMES", 0);
        prefs.edit().putInt(gamesKey + "_GAMES", games + 1).apply();
        
        // Обновляем лучшее время
        long bestTime = prefs.getLong(gamesKey + "_BEST_TIME", Long.MAX_VALUE);
        if (time < bestTime) {
            prefs.edit().putLong(gamesKey + "_BEST_TIME", time).apply();
        }
        
        // Обновляем лучшие ходы
        int bestMoves = prefs.getInt(gamesKey + "_BEST_MOVES", Integer.MAX_VALUE);
        if (moves < bestMoves) {
            prefs.edit().putInt(gamesKey + "_BEST_MOVES", moves).apply();
        }
    }
    
    private String getGridSizeKey(int rows, int cols) {
        if (rows == 4 && cols == 4) return "4x4";
        if (rows == 6 && cols == 6) return "6x6";
        return "custom";
    }
    
    private int getStatGamesKey(String sizeKey) {
        switch (sizeKey) {
            case "4x4": return 0;
            case "6x6": return 1;
            default: return 2;
        }
    }
    
    public int getGridSizeGames(String sizeKey) {
        int key = getStatGamesKey(sizeKey);
        return prefs.getInt(key + "_GAMES", 0);
    }
    
    public long getGridSizeBestTime(String sizeKey) {
        int key = getStatGamesKey(sizeKey);
        return prefs.getLong(key + "_BEST_TIME", Long.MAX_VALUE);
    }
    
    public int getGridSizeBestMoves(String sizeKey) {
        int key = getStatGamesKey(sizeKey);
        return prefs.getInt(key + "_BEST_MOVES", Integer.MAX_VALUE);
    }
    
    // ==================== СТАТИСТИКА ПО РЕЖИМАМ ====================
    
    public void updateModeStats(int matchCount, boolean mixedMode, long time) {
        String modeKey = getModeKey(matchCount, mixedMode);
        
        int games = prefs.getInt(modeKey + "_GAMES", 0);
        prefs.edit().putInt(modeKey + "_GAMES", games + 1).apply();
        
        long bestTime = prefs.getLong(modeKey + "_BEST_TIME", Long.MAX_VALUE);
        if (time < bestTime) {
            prefs.edit().putLong(modeKey + "_BEST_TIME", time).apply();
        }
    }
    
    private String getModeKey(int matchCount, boolean mixedMode) {
        if (mixedMode) return "MODE_MIXED";
        return matchCount == 2 ? "MODE_PAIRS" : "MODE_TRIPLETS";
    }
    
    public int getModeGames(String modeKey) {
        return prefs.getInt(modeKey + "_GAMES", 0);
    }
    
    public long getModeBestTime(String modeKey) {
        return prefs.getLong(modeKey + "_BEST_TIME", Long.MAX_VALUE);
    }
    
    // ==================== ИСТОРИЯ ИГР ====================
    
    public void addGameToHistory(int rows, int cols, int matchCount, boolean mixedMode, 
                                  long time, int moves, boolean won) {
        try {
            JSONArray history = new JSONArray(prefs.getString(KEY_GAME_HISTORY, "[]"));
            
            JSONObject game = new JSONObject();
            game.put("date", System.currentTimeMillis());
            game.put("rows", rows);
            game.put("cols", cols);
            game.put("matchCount", matchCount);
            game.put("mixedMode", mixedMode);
            game.put("time", time);
            game.put("moves", moves);
            game.put("won", won);
            
            // Добавляем в начало
            JSONArray newHistory = new JSONArray();
            newHistory.put(game);
            for (int i = 0; i < history.length() && i < MAX_HISTORY_SIZE - 1; i++) {
                newHistory.put(history.get(i));
            }
            
            prefs.edit().putString(KEY_GAME_HISTORY, newHistory.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    public List<GameHistoryEntry> getGameHistory() {
        List<GameHistoryEntry> result = new ArrayList<>();
        try {
            JSONArray history = new JSONArray(prefs.getString(KEY_GAME_HISTORY, "[]"));
            for (int i = 0; i < history.length(); i++) {
                JSONObject game = history.getJSONObject(i);
                result.add(new GameHistoryEntry(
                    game.getLong("date"),
                    game.getInt("rows"),
                    game.getInt("cols"),
                    game.getInt("matchCount"),
                    game.getBoolean("mixedMode"),
                    game.getLong("time"),
                    game.getInt("moves"),
                    game.getBoolean("won")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // ==================== ДОСТИЖЕНИЯ ====================
    
    public void unlockAchievement(String achievementKey) {
        prefs.edit().putBoolean(achievementKey, true).apply();
    }
    
    public boolean isAchievementUnlocked(String achievementKey) {
        return prefs.getBoolean(achievementKey, false);
    }
    
    public void checkAndUnlockAchievements(long time, int moves, int totalPairs) {
        // Первая победа
        unlockAchievement(KEY_ACHIEVEMENT_FIRST_WIN);
        
        // Скоростной демон - победа менее чем за 30 секунд (на 4x4)
        if (time < 30000) {
            unlockAchievement(KEY_ACHIEVEMENT_SPEED_DEMON);
        }
        
        // Перфекционист - минимум ходов (количество пар = количество ходов)
        if (moves == totalPairs) {
            unlockAchievement(KEY_ACHIEVEMENT_PERFECTIONIST);
        }
    }
    
    public List<Achievement> getAllAchievements() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new Achievement("🏆", "Первая победа", "Выиграйте первую игру", 
            isAchievementUnlocked(KEY_ACHIEVEMENT_FIRST_WIN)));
        achievements.add(new Achievement("⚡", "Скоростной демон", "Победите менее чем за 30 секунд",
            isAchievementUnlocked(KEY_ACHIEVEMENT_SPEED_DEMON)));
        achievements.add(new Achievement("🎯", "Перфекционист", "Завершите игру с минимумом ходов",
            isAchievementUnlocked(KEY_ACHIEVEMENT_PERFECTIONIST)));
        achievements.add(new Achievement("🔥", "Мастер серий", "Выиграйте 5 игр подряд",
            isAchievementUnlocked(KEY_ACHIEVEMENT_STREAK_MASTER)));
        return achievements;
    }
    
    public int getUnlockedAchievementsCount() {
        int count = 0;
        if (isAchievementUnlocked(KEY_ACHIEVEMENT_FIRST_WIN)) count++;
        if (isAchievementUnlocked(KEY_ACHIEVEMENT_SPEED_DEMON)) count++;
        if (isAchievementUnlocked(KEY_ACHIEVEMENT_PERFECTIONIST)) count++;
        if (isAchievementUnlocked(KEY_ACHIEVEMENT_STREAK_MASTER)) count++;
        return count;
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ====================
    
    public static class GameHistoryEntry {
        public final long date;
        public final int rows;
        public final int cols;
        public final int matchCount;
        public final boolean mixedMode;
        public final long time;
        public final int moves;
        public final boolean won;
        
        public GameHistoryEntry(long date, int rows, int cols, int matchCount, boolean mixedMode,
                                 long time, int moves, boolean won) {
            this.date = date;
            this.rows = rows;
            this.cols = cols;
            this.matchCount = matchCount;
            this.mixedMode = mixedMode;
            this.time = time;
            this.moves = moves;
            this.won = won;
        }
        
        public String getDateFormatted() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
            return sdf.format(new Date(date));
        }
        
        public String getTimeFormatted() {
            int seconds = (int) (time / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        
        public String getGridSizeFormatted() {
            return rows + "x" + cols;
        }
        
        public String getModeFormatted() {
            if (mixedMode) return "Смешанный";
            return matchCount == 2 ? "Пары" : "Тройки";
        }
    }
    
    public static class Achievement {
        public final String icon;
        public final String title;
        public final String description;
        public final boolean unlocked;
        
        public Achievement(String icon, String title, String description, boolean unlocked) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.unlocked = unlocked;
        }
    }
}
