package com.example.labb5new5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notes.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, fullText TEXT)");

        // тут создание 20 записей
        for (int i = 1; i <= 20; i++) {
            ContentValues cv = new ContentValues();
            cv.put("description", "Заметка №" + i);
            cv.put("fullText", "Это подробный текст заметки номер " + i + ". Здесь может быть длинное описание, мысли, идеи, планы и т.д.");
            db.insert("notes", null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor c = db.rawQuery("SELECT * FROM notes", null)) {
            while (c.moveToNext()) {
                list.add(new Note(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2)
                ));
            }
        }

        return list;
    }


    public void addNote(String description, String fullText) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("description", description);
        cv.put("fullText", fullText);
        db.insert("notes", null, cv);
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("notes", "id=?", new String[]{String.valueOf(id)});
    }

    public void updateNote(int id, String newDesc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("description", newDesc);
        db.update("notes", cv, "id=?", new String[]{String.valueOf(id)});
    }
}
