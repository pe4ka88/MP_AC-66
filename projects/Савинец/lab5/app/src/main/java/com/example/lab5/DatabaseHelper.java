package com.example.lab5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 3;

    // Имя таблицы и столбцы
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DESCRIPTION = "description";

    // SQL для создания таблицы
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NOTES + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_DESCRIPTION + " TEXT NOT NULL"
                    + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.d("DatabaseHelper", "Таблица создана");

        // Добавляем тестовые заметки: меньше 20 записей по условию работы
        insertTestNotes(db, 18);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // При обновлении версии удаляем старую таблицу и создаем новую
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    private void insertTestNotes(SQLiteDatabase db, int count) {
        ContentValues values = new ContentValues();
        for (int i = 1; i <= count; i++) {
            values.put(COLUMN_DESCRIPTION, "Заметка #" + i + " (Савинец М. Д.) - " + getRandomText());
            db.insert(TABLE_NOTES, null, values);
        }
        Log.d("DatabaseHelper", count + " тестовых заметок добавлено");
    }

    private String getRandomText() {
        String[] texts = {
                "Важная заметка", "Напоминание", "Идея", "План", "Задача",
                "Список покупок", "Встреча", "Звонок", "Подарок", "Путешествие",
                "Учеба", "Работа", "Дом", "Семья", "Друзья",
                "Здоровье", "Спорт", "Отдых", "Развлечения", "Финансы"
        };
        return texts[(int)(Math.random() * texts.length)];
    }

    // Добавление заметки
    public long addNote(String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, description);
        long id = db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    // Получение всех заметок
    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COLUMN_ID + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Note note = new Note(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    noteList.add(note);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Ошибка при получении заметок: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return noteList;
    }

    // Удаление заметки по ID
    public int deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // Обновление заметки
    public int updateNote(int id, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, description);

        int result = db.update(TABLE_NOTES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // Проверка существования заметки
    public boolean noteExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;

        try {
            cursor = db.query(TABLE_NOTES, new String[]{COLUMN_ID},
                    COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
            exists = cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Ошибка при проверке существования: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return exists;
    }

    // Получение количества заметок
    public int getNotesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTES, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Ошибка при подсчете: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return count;
    }

    // Очистка базы данных (для тестирования)
    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, null, null);
        db.close();
    }
}