package com.example.myapplication5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication5.Note;

import java.util.List;

public class NotesListAdapter extends BaseAdapter {

    private final Context    mContext;
    private List<Note>       mNotes;
    private final LayoutInflater mInflater;

    public NotesListAdapter(Context context, List<Note> notes) {
        mContext  = context;
        mNotes    = notes;
        mInflater = LayoutInflater.from(context);
    }

    // Обновить данные адаптера (вызывается при изменениях БД)
    public void updateData(List<Note> notes) {
        mNotes = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return mNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mNotes.get(position).getId();
    }

    // ViewHolder — паттерн для оптимизации прокрутки списка
    static class ViewHolder {
        TextView tvId;
        TextView tvDescription;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_note, parent, false);
            holder = new ViewHolder();
            holder.tvId          = convertView.findViewById(R.id.tvNoteId);
            holder.tvDescription = convertView.findViewById(R.id.tvNoteDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = mNotes.get(position);
        holder.tvId.setText(String.valueOf(note.getId()));
        holder.tvDescription.setText(note.getDescription());

        return convertView;
    }
}
