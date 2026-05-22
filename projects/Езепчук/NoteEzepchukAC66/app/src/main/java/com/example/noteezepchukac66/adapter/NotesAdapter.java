package com.example.noteezepchukac66.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteezepchukac66.R;
import com.example.noteezepchukac66.model.Note;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onClick(Note note);
    }

    public interface OnMultiSelectListener {
        void onMultiSelect(boolean active);
    }

    private Context context;
    private ArrayList<Note> list;
    private OnNoteClickListener clickListener;
    private OnMultiSelectListener multiSelectListener;

    // Для множественного выбора
    private final ArrayList<Integer> selectedPositions = new ArrayList<>();

    public NotesAdapter(Context context,
                        ArrayList<Note> list,
                        OnNoteClickListener clickListener,
                        OnMultiSelectListener multiSelectListener) {
        this.context = context;
        this.list = list;
        this.clickListener = clickListener;
        this.multiSelectListener = multiSelectListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        Note note = list.get(position);

        holder.tvTitle.setText(note.getTitle());
        holder.tvDescription.setText(note.getDescription());
        holder.tvDate.setText(note.getDate());
        holder.ivPin.setVisibility(note.getIs_pined() == 1 ? View.VISIBLE : View.GONE);

        // ===== Цвета через тему Material 3 =====
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();

        // Фон карточки
        theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true);
        holder.cardView.setCardBackgroundColor(typedValue.data);

        // Основной текст
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
        int primaryTextColor = typedValue.data;
        holder.tvTitle.setTextColor(primaryTextColor);
        holder.tvDescription.setTextColor(primaryTextColor);

        // Вторичный текст (дата)
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurfaceVariant, typedValue, true);
        holder.tvDate.setTextColor(typedValue.data);

        // ===== Подсветка выбранных =====
        if (selectedPositions.contains(position)) {

            theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
            holder.cardView.setStrokeColor(typedValue.data);
            holder.cardView.setStrokeWidth(4);

        } else {
            holder.cardView.setStrokeWidth(0);
        }

        // ===== Клик =====
        holder.itemView.setOnClickListener(v -> {
            if (selectedPositions.isEmpty()) {
                clickListener.onClick(note);
            } else {
                toggleSelection(position);
            }
        });

        // ===== Долгое нажатие =====
        holder.itemView.setOnLongClickListener(v -> {
            toggleSelection(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ===== Логика множественного выбора =====
    private void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(Integer.valueOf(position));
        } else {
            selectedPositions.add(position);
        }
        notifyItemChanged(position);

        Log.i("NotesAdapter", "Selected count: " + selectedPositions.size());
        if (multiSelectListener != null) {
            multiSelectListener.onMultiSelect(!selectedPositions.isEmpty());
        }
    }

    public ArrayList<Note> getSelectedNotes() {
        ArrayList<Note> selectedNotes = new ArrayList<>();
        for (int pos : selectedPositions) {
            selectedNotes.add(list.get(pos));
        }
        return selectedNotes;
    }

    public void clearSelection() {
        ArrayList<Integer> copy = new ArrayList<>(selectedPositions);
        selectedPositions.clear();
        for (int pos : copy) notifyItemChanged(pos);

        if (multiSelectListener != null) {
            multiSelectListener.onMultiSelect(false);
        }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate;
        ImageView ivPin;
        MaterialCardView cardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.textTitle);
            tvDescription = itemView.findViewById(R.id.textDescription);
            tvDate = itemView.findViewById(R.id.textDate);
            ivPin = itemView.findViewById(R.id.imagePin);
        }
    }
}