package com.example.lab5mp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mynotes.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NOTES = "notes";
    public static final String COL_ID = "_id";
    public static final String COL_TEXT = "text";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + TABLE_NOTES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TEXT + " TEXT NOT NULL);";
        db.execSQL(create);

        // Предзаполнение минимум 20 записей
        for (int i = 1; i <= 20; i++) {
            ContentValues cv = new ContentValues();
            cv.put(COL_TEXT, "Sample note " + i);
            db.insert(TABLE_NOTES, null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // Получить все заметки
    public Cursor getAllNotes() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NOTES, null, null, null, null, null, COL_ID + " ASC");
    }

    // Добавить заметку
    public long addNote(String text) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEXT, text);
        return db.insert(TABLE_NOTES, null, cv);
    }

    // Удалить заметку по ID
    public int deleteNote(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NOTES, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Обновить заметку
    public int updateNote(long id, String newText) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEXT, newText);
        return db.update(TABLE_NOTES, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}
