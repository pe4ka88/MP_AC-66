package com.example.note.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.note.model.Note;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notes.db";
    private static final int VERSION = 6; // версия с embedding
    public static final String TABLE_NOTES = "notes";
    private static final String TABLE = "notes";

    public NotesDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    // =========================
    // CREATE TABLE
    // =========================
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "description TEXT," +
                "date TEXT," +
                "is_pinned INTEGER DEFAULT 0," +
                "embedding TEXT)");
    }

    // =========================
    // UPGRADE
    // =========================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN is_pinned INTEGER DEFAULT 0");
        }
        if (oldVersion < 6) {  // добавляем новую версию для embedding
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN embedding TEXT");
        }

    }

    // =========================
    // ADD NOTE (обычный)
    // =========================
    public void addNote(String title, String description) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("title", title);
        cv.put("description", description);

        String currentDate = new SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());
        cv.put("date", currentDate);
        cv.put("is_pinned", 0);
        cv.put("embedding", "[]"); // пустой embedding по умолчанию

        db.insert(TABLE, null, cv);
        db.close();
    }

    // =========================
    // ADD NOTE WITH EMBEDDING
    // =========================
    public void addNoteWithEmbedding(String title, String description, List<Float> embedding) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("title", title);
        cv.put("description", description);

        String currentDate = new SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());
        cv.put("date", currentDate);
        cv.put("is_pinned", 0);

        // Сохраняем embedding как JSON
        cv.put("embedding", new JSONArray(embedding).toString());

        db.insert(TABLE, null, cv);
        db.close();
    }

    // =========================
    // DELETE NOTE
    // =========================
    public void deleteNote(int id) {
        getWritableDatabase().delete(
                TABLE,
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    // =========================
    // UPDATE NOTE
    // =========================
    public void updateNoteWithEmbedding(int id,
                                        String title,
                                        String description,
                                        List<Float> embedding) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("embedding", embeddingToString(embedding));

        db.update(
                TABLE_NOTES,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
    }
    private String embeddingToString(List<Float> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < embedding.size(); i++) {
            sb.append(embedding.get(i));
            if (i < embedding.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("]");
        return sb.toString();
    }
    // =========================
    // PIN / UNPIN
    // =========================
    public void updatePin(int id, int isPinned) {
        ContentValues cv = new ContentValues();
        cv.put("is_pinned", isPinned);

        getWritableDatabase().update(
                TABLE,
                cv,
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    public void setPinned(int id, boolean pinned) {
        updatePin(id, pinned ? 1 : 0);
    }

    // =========================
    // GET ALL NOTES
    // =========================
    public ArrayList<Note> getNotes() {
        ArrayList<Note> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT id, title, description, date, is_pinned, embedding FROM notes ORDER BY date DESC", null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    int isPinned = cursor.getInt(cursor.getColumnIndexOrThrow("is_pinned"));
                    String embString = cursor.getString(cursor.getColumnIndexOrThrow("embedding"));

                    // Парсим embedding через метод Note
                    List<Float> embedding = Note.parseEmbedding(embString);

                    Note note = new Note(id, title, description, date, isPinned, embedding);
                    notesList.add(note);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("NotesDBHelper", "Error reading notes from DB", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        Log.i("NotesDBHelper", "getNotes: total notes loaded = " + notesList.size());
        return notesList;
    }
}