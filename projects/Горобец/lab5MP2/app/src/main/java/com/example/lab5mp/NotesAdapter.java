package com.example.lab5mp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NotesAdapter extends BaseAdapter {

    private Context context;
    private List<Note> notes;
    private LayoutInflater inflater;

    public NotesAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.item_note, parent, false);
        }

        TextView tvId = v.findViewById(R.id.tvId);
        TextView tvText = v.findViewById(R.id.tvText);

        Note note = notes.get(position);

        tvId.setText(String.valueOf(note.id));
        tvText.setText(note.text);

        return v;
    }
}
