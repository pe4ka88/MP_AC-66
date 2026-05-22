package com.example.lab5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends BaseAdapter {

    private List<Note> noteList;
    private LayoutInflater inflater;

    public NoteAdapter(List<Note> noteList, LayoutInflater inflater) {
        this.noteList = noteList;
        this.inflater = inflater;
    }

    public void updateData(List<Note> newList) {
        this.noteList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return noteList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_note, parent, false);
            holder = new ViewHolder();
            holder.tvId = convertView.findViewById(R.id.tvNoteId);
            holder.tvDescription = convertView.findViewById(R.id.tvNoteDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = noteList.get(position);

        holder.tvId.setText("#" + note.getId());
        holder.tvDescription.setText(note.getDescription());

        return convertView;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvDescription;
    }
}