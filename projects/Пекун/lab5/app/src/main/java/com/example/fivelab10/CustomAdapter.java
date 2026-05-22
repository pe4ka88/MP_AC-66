package com.example.fivelab10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class    CustomAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<Note> notes;

    public CustomAdapter(Context context, List<Note> notes) {
        this.inflater = LayoutInflater.from(context);
        this.notes = new ArrayList<>(notes);
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

    public void updateData(List<Note> newNotes) {
        notes.clear();
        notes.addAll(newNotes);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_note, parent, false);
            holder = new ViewHolder();
            holder.textId = convertView.findViewById(R.id.textId);
            holder.textDescription = convertView.findViewById(R.id.textDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = notes.get(position);
        holder.textId.setText("ID: " + note.getId());
        holder.textDescription.setText("Description: " + note.getDescription());

        return convertView;
    }

    private static class ViewHolder {
        TextView textId;
        TextView textDescription;
    }
}
