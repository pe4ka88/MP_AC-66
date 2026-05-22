package com.example.fileexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnSelectFile;
    private TextView tvFileName, tvFileType;
    private Uri selectedFileUri;
    private String selectedFileName;

    private final ActivityResultLauncher<String[]> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    selectedFileName = getFileName(uri);
                    tvFileName.setText("Файл: " + selectedFileName);

                    String fileType = getFileType(selectedFileName);
                    tvFileType.setText("Тип файла: " + fileType);

                    openMediaDisplayActivity();
                } else {
                    Toast.makeText(this, "Файл не выбран", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                for (Boolean granted : permissions.values()) {
                    if (!granted) {
                        allGranted = false;
                        break;
                    }
                }

                if (allGranted) {
                    openFilePicker();
                } else {
                    Toast.makeText(this, "Нужны разрешения для работы с файлами", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        checkAndRequestPermissions();
    }

    private void initViews() {
        btnSelectFile = findViewById(R.id.btnSelectFile);
        tvFileName = findViewById(R.id.tvFileName);
        tvFileType = findViewById(R.id.tvFileType);

        btnSelectFile.setOnClickListener(v -> {
            if (havePermissions()) {
                openFilePicker();
            } else {
                checkAndRequestPermissions();
            }
        });
    }

    private boolean havePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
            };

            boolean needRequest = false;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    needRequest = true;
                    break;
                }
            }

            if (needRequest) {
                permissionLauncher.launch(permissions);
            } else {
                openFilePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            } else {
                openFilePicker();
            }
        }
    }

    private void openFilePicker() {
        filePickerLauncher.launch(new String[]{"*/*"});
    }

    private void openMediaDisplayActivity() {
        Intent intent = new Intent(this, MediaDisplayActivity.class);
        intent.setData(selectedFileUri);
        intent.putExtra("file_name", selectedFileName);
        startActivity(intent);
    }

    private String getFileName(Uri uri) {
        String fileName = null;

        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fileName == null) {
            String path = uri.getPath();
            if (path != null) {
                int lastSlash = path.lastIndexOf('/');
                if (lastSlash >= 0) {
                    fileName = path.substring(lastSlash + 1);
                }
            }
        }

        return fileName != null ? fileName : "unknown";
    }

    private String getFileType(String fileName) {
        if (fileName == null) return "неизвестен";

        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png") || lowerName.endsWith(".gif") ||
                lowerName.endsWith(".bmp") || lowerName.endsWith(".webp")) {
            return "графический файл";
        } else if (lowerName.endsWith(".mp3") || lowerName.endsWith(".wav") ||
                lowerName.endsWith(".ogg") || lowerName.endsWith(".aac") ||
                lowerName.endsWith(".m4a") || lowerName.endsWith(".flac")) {
            return "аудиофайл";
        } else if (lowerName.endsWith(".mp4") || lowerName.endsWith(".avi") ||
                lowerName.endsWith(".mkv") || lowerName.endsWith(".3gp") ||
                lowerName.endsWith(".webm") || lowerName.endsWith(".mov")) {
            return "видеофайл";
        } else {
            return "неподдерживаемый тип";
        }
    }
}