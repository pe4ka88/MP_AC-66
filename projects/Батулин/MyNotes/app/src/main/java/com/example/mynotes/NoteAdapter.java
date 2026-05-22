package com.example.mynotes; // Замени на свой пакет

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {
    public NoteAdapter(Context context, List<Note> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView tvId = convertView.findViewById(R.id.tvId);
        TextView tvDesc = convertView.findViewById(R.id.tvDesc);

        tvId.setText("ID: " + note.id);
        tvDesc.setText(note.description);
        return convertView;
    }
}
