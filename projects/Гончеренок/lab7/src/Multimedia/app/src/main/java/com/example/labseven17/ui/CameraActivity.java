package com.example.labseven17.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.labseven17.R;
import com.example.labseven17.databinding.ActivityCameraBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private ActivityCameraBinding binding;
    private Uri currentPhotoUri;
    private float scaleFactor = 1.0f;
    private ScaleGestureDetector scaleDetector;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentPhotoUri != null) {
                    binding.ivPhoto.setImageURI(currentPhotoUri);
                    binding.tvPhotoStatus.setText(R.string.photo_saved);
                    binding.ivPhoto.setScaleX(1f);
                    binding.ivPhoto.setScaleY(1f);
                    scaleFactor = 1f;
                } else {
                    binding.tvPhotoStatus.setText(R.string.photo_not_captured);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnTakePhoto.setOnClickListener(v -> checkPermissionAndCapture());

        scaleDetector = new ScaleGestureDetector(this, new ScaleListener());
        binding.ivPhoto.setOnTouchListener((v, event) -> onPhotoTouch(event));
    }

    private void checkPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            currentPhotoUri = createImageUri();
            if (currentPhotoUri != null) {
                takePictureLauncher.launch(currentPhotoUri);
            }
        } catch (IOException e) {
            Toast.makeText(this, R.string.camera_open_error, Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageUri() throws IOException {
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (picturesDir == null) {
            throw new IOException("Pictures dir is unavailable");
        }

        File image = File.createTempFile("PHOTO_" + stamp + "_", ".jpg", picturesDir);
        return FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                image
        );
    }

    private boolean onPhotoTouch(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 4.0f));
            binding.ivPhoto.setScaleX(scaleFactor);
            binding.ivPhoto.setScaleY(scaleFactor);
            return true;
        }
    }
}
