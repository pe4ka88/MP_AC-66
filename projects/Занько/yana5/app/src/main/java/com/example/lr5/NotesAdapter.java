package com.example.lr5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Note> notes = new ArrayList<>();

    public NotesAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.notes = notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
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
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.item_note, parent, false);
            holder = new ViewHolder();
            holder.tvId = view.findViewById(R.id.tvItemId);
            holder.tvDesc = view.findViewById(R.id.tvItemDesc);
            holder.tvAuthor = view.findViewById(R.id.tvItemAuthor);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Note note = notes.get(position);
        holder.tvId.setText("№ " + note.getId());
        holder.tvDesc.setText(note.getDescription());
        holder.tvAuthor.setText("Разработал Занько Я.С. • АС-66");

        return view;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvDesc;
        TextView tvAuthor;
    }
}
