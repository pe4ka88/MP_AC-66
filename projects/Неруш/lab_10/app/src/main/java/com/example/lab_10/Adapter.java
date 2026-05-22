package com.example.lab_10;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class Adapter extends CursorAdapter {

    public Adapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView noteNumber = view.findViewById(R.id.noteNumber);
        TextView noteDescription = view.findViewById(R.id.noteDescription);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

        noteNumber.setText(String.valueOf(id));
        noteDescription.setText(description);
    }
}