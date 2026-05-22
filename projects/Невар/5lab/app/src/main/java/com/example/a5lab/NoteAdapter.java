package com.example.a5lab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.a5lab.model.Note;
import java.util.List;
import android.content.Context;

public class NoteAdapter extends BaseAdapter {
    private Context context;
    private List<Note> notes;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
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
        return notes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_note, parent, false);
        }

        Note note = notes.get(position);

        TextView tvId = convertView.findViewById(R.id.tv_note_id);
        TextView tvDesc = convertView.findViewById(R.id.tv_note_desc);

        tvId.setText("№" + note.getId());
        tvDesc.setText(note.getDescription());

        return convertView;
    }

    public void updateData(List<Note> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged();
    }
}