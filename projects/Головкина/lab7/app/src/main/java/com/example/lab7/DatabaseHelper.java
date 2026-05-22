package com.example.lab7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "history.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HISTORY = "history";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type"; // audio, video, photo
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String COLUMN_FILE_PATH = "file_path";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_HISTORY + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TYPE + " TEXT NOT NULL,"
                    + COLUMN_FILE_NAME + " TEXT NOT NULL,"
                    + COLUMN_FILE_PATH + " TEXT,"
                    + COLUMN_TIMESTAMP + " TEXT NOT NULL"
                    + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.d("DatabaseHelper", "Таблица истории создана");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // Добавление записи в историю
    public long addToHistory(String type, String fileName, String filePath) {
        long id = -1;
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_TYPE, type);
            values.put(COLUMN_FILE_NAME, fileName);
            values.put(COLUMN_FILE_PATH, filePath);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            values.put(COLUMN_TIMESTAMP, sdf.format(new Date()));

            id = db.insert(TABLE_HISTORY, null, values);
            Log.d("DatabaseHelper", "Добавлено в историю: " + type + " - " + fileName);

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Ошибка добавления в историю: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return id;
    }

    // Получение всей истории
    public List<HistoryItem> getAllHistory() {
        List<HistoryItem> historyList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_ID + " DESC";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(COLUMN_ID);
                    int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
                    int nameIndex = cursor.getColumnIndex(COLUMN_FILE_NAME);
                    int pathIndex = cursor.getColumnIndex(COLUMN_FILE_PATH);
                    int timeIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);

                    // Проверяем, что индексы корректны
                    if (idIndex >= 0 && typeIndex >= 0 && nameIndex >= 0 && timeIndex >= 0) {
                        HistoryItem item = new HistoryItem(
                                cursor.getInt(idIndex),
                                cursor.getString(typeIndex),
                                cursor.getString(nameIndex),
                                pathIndex >= 0 ? cursor.getString(pathIndex) : "",
                                cursor.getString(timeIndex)
                        );
                        historyList.add(item);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Ошибка при получении истории: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return historyList;
    }

    // Очистка истории
    public void clearHistory() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_HISTORY, null, null);
            Log.d("DatabaseHelper", "История очищена");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Ошибка очистки истории: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    // Класс для элементов истории
    public static class HistoryItem {
        private int id;
        private String type;
        private String fileName;
        private String filePath;
        private String timestamp;

        public HistoryItem(int id, String type, String fileName, String filePath, String timestamp) {
            this.id = id;
            this.type = type;
            this.fileName = fileName;
            this.filePath = filePath;
            this.timestamp = timestamp;
        }

        public int getId() { return id; }
        public String getType() { return type; }
        public String getFileName() { return fileName; }
        public String getFilePath() { return filePath; }
        public String getTimestamp() { return timestamp; }

        public String getTypeIcon() {
            switch (type) {
                case "audio": return "🎵";
                case "video": return "🎬";
                case "photo": return "📷";
                default: return "📁";
            }
        }
    }
}