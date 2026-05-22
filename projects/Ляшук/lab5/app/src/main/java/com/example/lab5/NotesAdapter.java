package com.example.lab5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NotesAdapter extends ArrayAdapter<Note> {

    public NotesAdapter(Context context, List<Note> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_note, parent, false);
        }

        TextView textViewId = convertView.findViewById(R.id.textViewId);
        TextView textViewDescription = convertView.findViewById(R.id.textViewDescription);

        if (note != null) {
            textViewId.setText(String.valueOf(note.getId()));
            textViewDescription.setText(note.getDescription());
        }

        return convertView;
    }
}
