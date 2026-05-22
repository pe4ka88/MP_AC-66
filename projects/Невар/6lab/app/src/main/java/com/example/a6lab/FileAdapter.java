package com.example.a6lab;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<MediaFile> files;
    private OnFileDeleteListener deleteListener;

    public interface OnFileDeleteListener {
        void onDelete(int position);
    }

    public FileAdapter(List<MediaFile> files, OnFileDeleteListener deleteListener) {
        this.files = files;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaFile file = files.get(position);
        holder.tvFileName.setText(file.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = null;
            if (file.getType().equals("image")) {
                intent = new Intent(v.getContext(), ImageActivity.class);
            } else if (file.getType().equals("audio")) {
                intent = new Intent(v.getContext(), AudioActivity.class);
            } else if (file.getType().equals("video")) {
                intent = new Intent(v.getContext(), VideoActivity.class);
            }
            if (intent != null) {
                intent.putExtra("uri", file.getUriString());
                intent.putExtra("name", file.getName());
                v.getContext().startActivity(intent);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;
        android.widget.Button btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}