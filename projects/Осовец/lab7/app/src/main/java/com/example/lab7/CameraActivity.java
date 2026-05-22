package com.example.lab7;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab7.data.HistoryLogger;
import com.example.lab7.ui.UiHelpers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private ImageView photoPreview;
    private TextView statusText;

    private final List<File> photos = new ArrayList<>();
    private int currentIndex = -1;
    private Uri pendingCaptureUri;
    private File pendingCaptureFile;
    private float scale = 1.0f;

    private final ActivityResultLauncher<String> requestCameraPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    capturePhoto();
                } else {
                    toast("Разрешение камеры отклонено");
                }
            }
    );

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success && pendingCaptureUri != null) {
                    reloadPhotos();
                    if (!photos.isEmpty()) {
                        currentIndex = photos.size() - 1;
                        showPhoto(currentIndex);
                    }
                    HistoryLogger.log(this, "CAMERA", "CAPTURE");
                } else {
                    cleanupFailedCapture();
                    toast("Съемка отменена");
                }
                pendingCaptureUri = null;
                pendingCaptureFile = null;
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        photoPreview = findViewById(R.id.imagePhotoPreview);
        statusText = findViewById(R.id.textCameraStatus);

        UiHelpers.bindBack(this);
        UiHelpers.bindAuthorButton(this);
        bindActions();

        reloadPhotos();
        if (!photos.isEmpty()) {
            currentIndex = photos.size() - 1;
            showPhoto(currentIndex);
        } else {
            statusText.setText("Снимков пока нет. Нажмите \"Сделать фото\"");
        }
    }

    private void bindActions() {
        findViewById(R.id.btnCapturePhoto).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                capturePhoto();
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA);
            }
        });

        findViewById(R.id.btnPhotoPrev).setOnClickListener(v -> showOffset(-1));
        findViewById(R.id.btnPhotoNext).setOnClickListener(v -> showOffset(1));

        findViewById(R.id.btnPhotoZoomIn).setOnClickListener(v -> updateScale(1.15f));
        findViewById(R.id.btnPhotoZoomOut).setOnClickListener(v -> updateScale(0.87f));
    }

    private void capturePhoto() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            toast("Камера недоступна в этом эмуляторе");
            return;
        }

        File imageFile;
        try {
            imageFile = createImageFile();
        } catch (IOException e) {
            toast("Не удалось создать файл для снимка");
            return;
        }

        pendingCaptureFile = imageFile;
        try {
            pendingCaptureUri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    imageFile
            );
        } catch (IllegalArgumentException e) {
            cleanupFailedCapture();
            toast("Не удалось открыть камеру");
            return;
        }
        takePictureLauncher.launch(pendingCaptureUri);
    }

    private File createImageFile() throws IOException {
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File dir = getExternalFilesDir("photos");
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        return File.createTempFile("lab7_" + stamp + "_", ".jpg", dir);
    }

    private void reloadPhotos() {
        photos.clear();
        File dir = getExternalFilesDir("photos");
        if (dir == null || !dir.exists()) {
            return;
        }
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jpg"));
        if (files != null) {
            Arrays.sort(files, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));
            photos.addAll(Arrays.asList(files));
        }
    }

    private void showOffset(int delta) {
        if (photos.isEmpty()) {
            statusText.setText("Снимков пока нет");
            return;
        }
        currentIndex += delta;
        if (currentIndex < 0) {
            currentIndex = photos.size() - 1;
        } else if (currentIndex >= photos.size()) {
            currentIndex = 0;
        }
        showPhoto(currentIndex);
    }

    private void showPhoto(int index) {
        if (index < 0 || index >= photos.size()) {
            return;
        }
        File file = photos.get(index);
        scale = 1.0f;
        photoPreview.setScaleX(scale);
        photoPreview.setScaleY(scale);
        photoPreview.setImageURI(Uri.fromFile(file));
        statusText.setText("Фото " + (index + 1) + " из " + photos.size() + ": " + file.getName());
        HistoryLogger.log(this, "CAMERA", "VIEW_PHOTO");
    }

    private void cleanupFailedCapture() {
        if (pendingCaptureFile != null && pendingCaptureFile.exists() && pendingCaptureFile.length() == 0L) {
            pendingCaptureFile.delete();
        }
    }

    private void updateScale(float factor) {
        scale *= factor;
        if (scale < 0.75f) {
            scale = 0.75f;
        }
        if (scale > 2.0f) {
            scale = 2.0f;
        }
        photoPreview.setScaleX(scale);
        photoPreview.setScaleY(scale);
    }

    private void toast(@NonNull String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
