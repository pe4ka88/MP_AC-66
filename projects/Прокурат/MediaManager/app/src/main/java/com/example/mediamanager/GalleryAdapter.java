package com.example.mediamanager;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<GalleryActivity.PhotoItem> photoList;
    private Context context;
    private OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(String photoPath);
        void onPhotoDelete(String photoPath, int position);
    }

    public GalleryAdapter(Context context, List<GalleryActivity.PhotoItem> photoList, OnPhotoClickListener listener) {
        this.context = context;
        this.photoList = photoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryActivity.PhotoItem item = photoList.get(position);
        holder.tvPhotoName.setText(item.getName());

        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(item.getDate()));
        holder.tvPhotoDate.setText(date);

        Bitmap bitmap = BitmapFactory.decodeFile(item.getPath());
        if (bitmap != null) {
            holder.ivThumb.setImageBitmap(bitmap);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPhotoClick(item.getPath());
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                deletePhoto(item.getPath(), position);
            }
        });
    }

    private void deletePhoto(String photoPath, int position) {
        File photoFile = new File(photoPath);

        boolean deleted = photoFile.delete();

        if (deleted) {
            String selection = MediaStore.Images.Media.DATA + "=?";
            String[] selectionArgs = new String[]{photoPath};

            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            context.getContentResolver().delete(uri, selection, selectionArgs);

            photoList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, photoList.size());

            Toast.makeText(context, "Фото удалено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvPhotoName, tvPhotoDate;
        Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvPhotoName = itemView.findViewById(R.id.tvPhotoName);
            tvPhotoDate = itemView.findViewById(R.id.tvPhotoDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}