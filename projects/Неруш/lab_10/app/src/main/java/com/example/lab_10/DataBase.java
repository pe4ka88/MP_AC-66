package com.example.lab_10;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mynotes.db";
    private static final int DATABASE_VERSION = 1;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (_id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    public Cursor getAllNotes() {
        return getReadableDatabase().rawQuery("SELECT * FROM notes", null);
    }

    public void addNote(String description) {
        ContentValues values = new ContentValues();
        values.put("description", description);
        getWritableDatabase().insert("notes", null, values);
    }

    public int deleteNoteById(int id) {
        return getWritableDatabase().delete("notes", "_id=?", new String[]{String.valueOf(id)});
    }

    public int updateNoteById(int id, String description) {
        ContentValues values = new ContentValues();
        values.put("description", description);
        return getWritableDatabase().update("notes", values, "_id=?", new String[]{String.valueOf(id)});
    }
}
