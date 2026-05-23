package com.example.a6lab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MediaFilesPrefs";
    private static final String KEY_FILES = "files_list";

    private RecyclerView recyclerView;
    private Button btnAddFile;
    private List<MediaFile> files;
    private FileAdapter adapter;

    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String fileName = getFileName(uri);
                        String type = getFileType(fileName);
                        files.add(new MediaFile(fileName, uri.toString(), type));
                        adapter.notifyItemInserted(files.size() - 1);
                        saveFiles();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnAddFile = findViewById(R.id.btnAddFile);

        files = loadFiles();
        adapter = new FileAdapter(files, position -> {
            files.remove(position);
            adapter.notifyItemRemoved(position);
            saveFiles();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddFile.setOnClickListener(v -> openFilePicker());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "audio/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        filePickerLauncher.launch(intent);
    }

    private String getFileName(Uri uri) {
        String result = "Неизвестный файл";
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                result = uri.getLastPathSegment();
            }
        }
        return result;
    }

    private String getFileType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") ||
                lower.endsWith(".gif") || lower.endsWith(".bmp") || lower.endsWith(".webp")) {
            return "image";
        } else if (lower.endsWith(".mp3") || lower.endsWith(".wav") || lower.endsWith(".ogg") ||
                lower.endsWith(".flac") || lower.endsWith(".aac") || lower.endsWith(".m4a")) {
            return "audio";
        } else if (lower.endsWith(".mp4") || lower.endsWith(".avi") || lower.endsWith(".mkv") ||
                lower.endsWith(".mov") || lower.endsWith(".3gp")) {
            return "video";
        }
        return "unknown";
    }

    private void saveFiles() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(files);
        prefs.edit().putString(KEY_FILES, json).apply();
    }

    private List<MediaFile> loadFiles() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_FILES, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<MediaFile>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }
}