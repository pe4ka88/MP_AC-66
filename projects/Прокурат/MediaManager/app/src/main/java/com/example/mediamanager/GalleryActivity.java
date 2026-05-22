package com.example.mediamanager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private Button btnBack;
    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private List<PhotoItem> photoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initViews();
        loadPhotosFromGallery();
        setupRecyclerView();
        setListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void loadPhotosFromGallery() {
        photoList.clear();

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        };

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        );

        if (cursor != null) {
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

            while (cursor.moveToNext()) {
                String path = cursor.getString(dataColumn);
                String name = cursor.getString(nameColumn);
                long date = cursor.getLong(dateColumn) * 1000L;

                PhotoItem item = new PhotoItem();
                item.setName(name);
                item.setPath(path);
                item.setDate(date);
                photoList.add(item);
            }
            cursor.close();
        }

        if (photoList.isEmpty()) {
            Toast.makeText(this, "Фотографий не найдено", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GalleryAdapter(this, photoList, new GalleryAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(String photoPath) {
                Intent intent = new Intent(GalleryActivity.this, PhotoViewerActivity.class);
                intent.putExtra("photo_path", photoPath);
                startActivity(intent);
            }

            @Override
            public void onPhotoDelete(String photoPath, int position) {
                loadPhotosFromGallery();
                adapter.notifyDataSetChanged();
                Toast.makeText(GalleryActivity.this, "Фото удалено", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPhotosFromGallery();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public static class PhotoItem {
        private String name;
        private String path;
        private long date;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public long getDate() { return date; }
        public void setDate(long date) { this.date = date; }
    }
}