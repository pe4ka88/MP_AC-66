package com.example.lr5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class NotesDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "mynotes.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NOTES = "notes";
    public static final String COL_ID = "_id";
    public static final String COL_DESC = "description";

    public NotesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NOTES + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_DESC + " TEXT NOT NULL)"
        );

        for (int i = 1; i <= 20; i++) {
            ContentValues cv = new ContentValues();
            cv.put(COL_DESC, "Стартовая заметка №" + i + " — Занько Я.С., АС-66");
            db.insert(TABLE_NOTES, null, cv);
        }
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
                null,
                null,
                null,
                null,
                null,
                COL_ID + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC));
                notes.add(new Note(id, description));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public long addNote(String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DESC, description);
        return db.insert(TABLE_NOTES, null, cv);
    }

    public int deleteNoteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NOTES, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int updateNote(int id, String newDescription) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DESC, newDescription);
        return db.update(TABLE_NOTES, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}
