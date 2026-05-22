package com.example.fivelab10;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notes.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DESCRIPTION = "description";

        private static final String[] DEFAULT_NOTES = {
            "Сделать зарядку утром",
            "Проверить дедлайн по лабораторной",
            "Купить кофе и воду",
            "Созвон с командой в 15:00",
            "Переписать конспект по Android",
            "Подготовить презентацию",
            "Оплатить интернет",
            "Сходить в спортзал",
            "Прочитать 20 страниц книги",
            "Обновить резюме",
            "Проверить почту преподавателя",
            "Сделать резервную копию проекта",
            "Продумать тему курсовой",
            "Написать 5 тест-кейсов",
            "Проверить работу SQLite",
            "Отправить отчет в LMS",
            "Подготовить вопросы к защите",
            "Проверить UI на телефоне",
            "Пройтись по баг-листу",
            "Запланировать задачи на завтра"
        };

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NOTES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DESCRIPTION + " TEXT" + ")";
        db.execSQL(createTable);
        seedDefaultNotes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NOTES,
                new String[]{COLUMN_ID, COLUMN_DESCRIPTION},
                null,
                null,
                null,
                null,
                COLUMN_ID + " ASC"
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                    int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
                    do {
                        int id = cursor.getInt(idIndex);
                        String description = cursor.getString(descriptionIndex);
                        notes.add(new Note(id, description));
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        return notes;
    }

    public long addNote(String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, description);
        return db.insert(TABLE_NOTES, null, values);
    }

    public int deleteNoteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NOTES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int updateNote(int id, String newDescription) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, newDescription);
        return db.update(TABLE_NOTES, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void ensureMinimumNotes() {
        SQLiteDatabase db = getWritableDatabase();
        if (getNotesCount(db) < 20) {
            db.beginTransaction();
            try {
                db.delete(TABLE_NOTES, null, null);
                seedDefaultNotes(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    private void seedDefaultNotes(SQLiteDatabase db) {
        for (String text : DEFAULT_NOTES) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DESCRIPTION, text);
            db.insert(TABLE_NOTES, null, values);
        }
    }

    private int getNotesCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTES, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            cursor.close();
        }
    }
}
