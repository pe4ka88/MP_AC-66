package com.example.myapplication5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication5.Note;

import java.util.ArrayList;
import java.util.List;

public class DB {

    // ---------------------------------------------------------------
    // Внутренний класс DBHelper — по аналогии с методичкой
    // ---------------------------------------------------------------
    private static class DBHelper extends SQLiteOpenHelper {

        private static final String DB_NAME    = "notes.db";
        private static final int    DB_VERSION = 1;
        static final String         DB_TABLE   = "notes";

        DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Создаём таблицу
            db.execSQL("CREATE TABLE " + DB_TABLE + "("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "description TEXT"
                    + ");");

            // Предзаполняем базу 20+ записями сразу при первом создании
            for (int i = 1; i <= 25; i++) {
                ContentValues cv = new ContentValues();
                cv.put("description", "Заметка номер " + i);
                db.insert(DB_TABLE, null, cv);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }

    // ---------------------------------------------------------------
    // Поля класса DB
    // ---------------------------------------------------------------
    private final Context   mCtx;
    private DBHelper        mDBHelper;
    private SQLiteDatabase  mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // Открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx);
        mDB = mDBHelper.getWritableDatabase();
    }

    // Закрыть подключение (вызывается в onDestroy Activity)
    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    // ---------------------------------------------------------------
    // CRUD-методы
    // ---------------------------------------------------------------

    // Получить все заметки в виде List<Note>
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = mDB.query(
                DBHelper.DB_TABLE,  // таблица
                null,               // все столбцы
                null,               // WHERE — нет
                null,               // аргументы WHERE — нет
                null,               // GROUP BY — нет
                null,               // HAVING — нет
                "id ASC"            // ORDER BY id
        );

        if (cursor.moveToFirst()) {
            int idColIndex   = cursor.getColumnIndex("id");
            int descColIndex = cursor.getColumnIndex("description");
            do {
                int    id   = cursor.getInt(idColIndex);
                String desc = cursor.getString(descColIndex);
                notes.add(new Note(id, desc));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return notes;
    }

    // Добавить заметку
    public void addNote(String description) {
        ContentValues cv = new ContentValues();
        cv.put("description", description);
        mDB.insert(DBHelper.DB_TABLE, null, cv);
    }

    // Удалить заметку по id; возвращает true если запись была найдена и удалена
    public boolean deleteNote(int id) {
        int rows = mDB.delete(
                DBHelper.DB_TABLE,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
        return rows > 0;
    }

    // Обновить описание заметки по id; возвращает true если запись была найдена
    public boolean updateNote(int id, String newDescription) {
        ContentValues cv = new ContentValues();
        cv.put("description", newDescription);
        int rows = mDB.update(
                DBHelper.DB_TABLE,
                cv,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
        return rows > 0;
    }
}