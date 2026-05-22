package com.example.labb5new5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends BaseAdapter {

    private Context context;
    private List<Note> notes;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public int getCount() { return notes.size(); }

    @Override
    public Object getItem(int i) { return notes.get(i); }

    @Override
    public long getItemId(int i) { return notes.get(i).id; }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
            v = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);

        Note n = notes.get(i);

        TextView tvId = v.findViewById(R.id.tvId);
        TextView tvDesc = v.findViewById(R.id.tvDesc);

        tvId.setText(String.valueOf(n.id));
        tvDesc.setText(n.description);

        return v;
    }
}
