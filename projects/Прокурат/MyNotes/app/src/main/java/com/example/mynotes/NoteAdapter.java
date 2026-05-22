package com.example.mynotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class NoteAdapter extends BaseAdapter {
    private ArrayList<Note> notesList;
    private LayoutInflater inflater;

    public NoteAdapter(LayoutInflater inflater, ArrayList<Note> notesList) {
        this.inflater = inflater;
        this.notesList = notesList;
    }

    @Override
    public int getCount() {
        return notesList.size();
    }

    @Override
    public Object getItem(int position) {
        return notesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notesList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_note, parent, false);
            holder = new ViewHolder();
            holder.tvId = convertView.findViewById(R.id.tv_note_id);
            holder.tvDescription = convertView.findViewById(R.id.tv_note_description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = notesList.get(position);
        holder.tvId.setText("ID: " + note.getId());
        holder.tvDescription.setText(note.getDescription());

        return convertView;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvDescription;
    }
}